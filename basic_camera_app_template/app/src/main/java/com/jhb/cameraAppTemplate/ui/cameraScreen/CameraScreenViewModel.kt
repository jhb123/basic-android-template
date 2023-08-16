package com.jhb.cameraAppTemplate.ui.cameraScreen

import android.media.Image
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

private const val TAG = "CameraScreenViewModel"

class CameraScreenViewModel() : ViewModel() {

    private val _cameraState = MutableStateFlow(CameraScreenState())
    val cameraState : StateFlow<CameraScreenState> = _cameraState

    fun setCaptureUseCase(imageCapture : ImageCapture){
        _cameraState.update {
            it.copy(
                imageCapture = imageCapture
            )
        }
    }

    fun setImage(image: Image){
        Log.i(TAG, "Image format ${image.format}")
        Log.i(TAG, "Image size ${image.planes.size}")
        Log.i(TAG, "Image timestamp ${image.timestamp}")
    }


}
