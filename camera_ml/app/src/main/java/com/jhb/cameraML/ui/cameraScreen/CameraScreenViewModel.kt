package com.jhb.cameraML.ui.cameraScreen

import android.graphics.Bitmap
import android.media.Image
import android.renderscript.ScriptGroup.Input
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

private const val TAG = "CameraScreenViewModel"

class CameraScreenViewModel() : ViewModel() {

    private val _cameraState = MutableStateFlow(CameraScreenState())
    val cameraState : StateFlow<CameraScreenState> = _cameraState

    fun setPreprossedImage(image: InputImage?){

        _cameraState.update {
            it.copy(
                pre_processed = image
            )
        }
    }


}
