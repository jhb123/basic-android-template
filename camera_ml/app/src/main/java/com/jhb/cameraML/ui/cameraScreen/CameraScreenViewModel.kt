package com.jhb.cameraML.ui.cameraScreen

import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

private const val TAG = "CameraScreenViewModel"

class CameraScreenViewModel() : ViewModel() {

    private val _cameraState = MutableStateFlow(CameraScreenState())
    val cameraState : StateFlow<CameraScreenState> = _cameraState

    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    fun setPreprossedImage(image: InputImage?){
        _cameraState.update {
            it.copy(
                pre_processed = image
            )
        }
        analyseText(image)
    }

    fun analyseText(image: InputImage?) {
        image?.let{
            TextRecognizerOptions.CREDIT_CARD
            val result = recognizer.process(it)
                .addOnSuccessListener { visionText ->
                    // Task completed successfully
                    // ...
                    var text = ""

                    visionText.textBlocks.forEach { textBlock ->
                        textBlock.lines.forEach { line ->
                            text += "\n${line.text}"
                        }
                    }

                    _cameraState.update {
                        it.copy(
                            text = text
                        )
                    }

                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                    // ...
                }
        }
    }


}
