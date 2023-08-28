package com.jhb.cameraML.ui.cameraScreen

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.camera.core.Preview as CameraPreview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import java.io.File
import androidx.compose.foundation.Image as BitmapImage

private const val TAG = "CameraScreen"

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(){

    // When composing the camera screen, check that permissions have been granted.
    // Show the normal screen if there are permissions, otherwise show the request
    // screen. This uses the experimental permission API from google.

    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )

    if (cameraPermissionState.status.isGranted) {
        CameraScreenComposable()
    } else {
        PermissionRequestScreen(
            showRational= cameraPermissionState.status.shouldShowRationale,
            requestPermission = { cameraPermissionState.launchPermissionRequest() }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun CameraScreenComposable(){
    val cameraScreenViewModel: CameraScreenViewModel = viewModel()
    val uiState by cameraScreenViewModel.cameraState.collectAsState()


    // Remove bouncyness - it causes the camera preview to look weird.
    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                CameraParts(
                    modifier = Modifier.height(500.dp),
                    updatePreprocessed = { cameraScreenViewModel.setPreprossedImage(it) }
                )
            }
            item {

                uiState.pre_processed?.let { inputImage ->
                    inputImage.bitmapInternal?.let { bitmap ->
                        BitmapImage(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "some useful description",
                            modifier = Modifier.size(100.dp)
                        )
                    }
                }
            }
            item {
                uiState.text?.let { Text(text = it) }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun CameraParts(
    modifier: Modifier = Modifier,
    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    updatePreprocessed : (InputImage?)-> Unit
) {
    // CameraX still doesn't have compose support.
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    var imageCapture: ImageCapture? = remember { ImageCapture.Builder().build() }

    // When the AndroidView is composed, the CameraX code that is required to
    // set up the connection to the camera is run. The button

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        AndroidView(
            modifier = Modifier.fillMaxHeight(0.8f),
            factory = { context ->
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

                val previewView = PreviewView(context).apply {
                    this.scaleType = scaleType
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }

                // CameraX Preview UseCase
                val previewUseCase = CameraPreview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

//                imageCapture = ImageCapture.Builder()
//                    .build()
                
                coroutineScope.launch {
                    val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                    try {
                        // Must unbind the use-cases before rebinding them.
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner, cameraSelector, previewUseCase, imageCapture
                        )
                    } catch (ex: Exception) {
                        Log.e("CameraPreview", "Use case binding failed", ex)
                    }
                }
                previewView.setClipToOutline(true)
                previewView
            }
        )
        Button(
            onClick = {
                Log.i(TAG,"taking picture")

                // Create output options using the cache. This doesn't need permissions
                // and is only accessible by this app.
                val cacheDir = context.getCacheDir()
                val cacheFile = File.createTempFile("temp_image", ".jpeg", cacheDir);
                Log.i(TAG,"Building output")
                val outputOptions = ImageCapture.OutputFileOptions
                    .Builder(cacheFile)
                    .build()

                Log.i(TAG,"finished building output")

                // Set up image capture listener, which is triggered after photo has
            // been taken
                imageCapture?.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onError(exc: ImageCaptureException) {
                            Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                        }

                        override fun onImageSaved(output: ImageCapture.OutputFileResults){
                            val msg = "Photo capture succeeded: ${output.savedUri}"
                            Toast.makeText(context.applicationContext, msg, Toast.LENGTH_SHORT).show()
                            Log.d(TAG, msg)
                            //val bitmap = BitmapFactory.decodeFile(output.savedUri.toString())
                            updatePreprocessed(
                                output.savedUri?.let {
                                    InputImage.fromFilePath(context.applicationContext,
                                        it
                                    )
                                }
                            )
                            Log.i(TAG,"Finished taking photo")
                        }
                    })
            })
        {
            Text("take pic")
        }
    }
}

@Composable
fun PermissionRequestScreen( showRational: Boolean, requestPermission: () -> Unit  ){

    Column(
        modifier = Modifier.fillMaxSize(1f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        //hm, this showRational boolean looks inverted to what I expected
        val textToShow = if (!showRational) {
            // If the user has denied the permission but the rationale can be shown,
            // then gently explain why the app requires this permission
            "To use this app, permission to use the camera must be granted. " +
                    "Photos taken with this app will not be stored anywhere."
        } else {
            // You can get here by repeatedly refusing permissions or asking to
            // not be promoted again.
            "Permission to use the camera have been denied. Please go to " +
                    "your android settings and grant this app permission to use the camera."
        }
        Text(textToShow, modifier = Modifier.padding(50.dp,20.dp))
        if(!showRational){
            Button(onClick =  requestPermission) {
                Text("Request permission")
            }
        }

    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun PreviewPermissionRequestScreen(){
    PermissionRequestScreen(showRational = false, requestPermission = {})
}
