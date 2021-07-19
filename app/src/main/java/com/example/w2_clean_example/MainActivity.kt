package com.example.w2_clean_example

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.w2globaldata.documentverification_client.DocumentVerificationClientBuilder
import com.w2globaldata.documentverification_client_abstractions.*
import com.w2globaldata.documentverification_client_capture.DocumentVerificationCapturerBuilder
import com.w2globaldata.documentverification_client_capture_abstractions.DocumentCapture
import com.w2globaldata.documentverification_client_capture_abstractions.DocumentCaptureEvents
import com.w2globaldata.documentverification_client_capture_abstractions.DocumentVerificationCapturer
import com.w2globaldata.facialcomparison_client.FacialComparisonClientBuilder
import com.w2globaldata.facialcomparison_client_abstractions.Facial
import com.w2globaldata.facialcomparison_client_abstractions.FacialComparisonException
import com.w2globaldata.facialcomparison_client_capture.FacialComparisonCapturerBuilder
import com.w2globaldata.facialcomparison_client_capture_abstractions.FacialCapture
import com.w2globaldata.facialcomparison_client_capture_abstractions.FacialComparisonCaptureEvents
import com.w2globaldata.facialcomparison_client_capture_abstractions.FacialComparisonCapturer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit


private val licenseKey = "YOUR LICENSE KEY HERE"
private val clientRef = "clientreference"
private val httpClient = OkHttpClient()

class MainActivity : AppCompatActivity() {

    private var docCapturer: DocumentVerificationCapturer? = null
    private var facialCapturer: FacialComparisonCapturer? = null

    private var docImage: Bitmap? = null
    private var facialImage: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        captureDocButton.setOnClickListener { captureDoc() }
        captureFaceButton.setOnClickListener { captureFace() }

        verifyDocButton.setOnClickListener { verifyDoc() }
        compareFacesButton.setOnClickListener { compareFaces() }

        verifyRestEndpointsButton.setOnClickListener { verifyUsingRestEndpoints() }
    }

    private fun captureDoc() {
        GlobalScope.launch {
            message.text = "Loading..."
            docCapturer = DocumentVerificationCapturerBuilder(this@MainActivity, licenseKey, clientRef).build()
            docCapturer?.presentCapturePage(DocumentType.Id3, DocumentCaptureEvents({ _: String, documentCapture: DocumentCapture ->
                documentImage.setImageBitmap(documentCapture.capturedImage)
                docImage = documentCapture.capturedImage
                message.text = "Success!"
            }, { clientReference: String, exception: DocumentVerificationException ->
                message.text = "Something went wrong: $exception"
            }, {}))
        }
    }

    private fun captureFace() {
        message.text = "Loading..."
        GlobalScope.launch {
            facialCapturer = FacialComparisonCapturerBuilder(this@MainActivity, licenseKey, clientRef).build()
            facialCapturer?.presentCapturePage(FacialComparisonCaptureEvents({ _: String, facialCapture: FacialCapture ->
                faceImage.setImageBitmap(facialCapture.capturedImage)
                facialImage = facialCapture.capturedImage
                message.text = "Success!"
            }, { _: String, exception: FacialComparisonException ->
                message.text = "Something went wrong: $exception"
            }, {}))
        }
    }


    private fun verifyDoc() {
        val image = docImage

        if (image == null) {
            Toast.makeText(this, "Capture a document image before verifying", Toast.LENGTH_LONG)
                .show()
            return
        }

        message.text = "Loading..."
        GlobalScope.launch {
            val docVerifier = DocumentVerificationClientBuilder(licenseKey).build()
            val page = DocumentPage(image.toByteArray())

            try {
                docVerifier.verify(
                    clientRef,
                    Document(ArrayList(listOf(page)), DocumentType.Id3)
                ).also { Log.d("DocumentVerification", it.toString()) }

                updateMessage("Success!")
            } catch (e: Exception) {
                Log.e("DocumentVerification", e.localizedMessage ?: "")
            }
        }
    }

    private fun compareFaces() {
        val image = facialImage

        if (image == null) {
            Toast.makeText(this, "Capture a facial image before verifying", Toast.LENGTH_LONG)
                .show()
            return
        }

        message.text = "Loading..."
        GlobalScope.launch {
            val facialComparisonClient = FacialComparisonClientBuilder(licenseKey).build()

            try {
                facialComparisonClient
                    .compare(clientRef, Facial(image.toByteArray(), image.toByteArray()))
                    .also { Log.d("FacialComparison", it.toString()) }

                updateMessage("Success!")
            } catch (e: Exception) {
                updateMessage("Something went wrong: ${e.localizedMessage}")
            }
        }
    }

    private fun verifyUsingRestEndpoints() {
        val image = docImage

        if (image == null) {
            Toast.makeText(this, "Capture a document image before verifying", Toast.LENGTH_LONG)
                .show()
            return
        }

        message.text = "Loading..."
        GlobalScope.launch {
            val page = image.toByteArray()

            try {
                val documentType = "ID3"
                val response = verifyImage(page!!, documentType)

                Log.i("verifyResponse", response)

                updateMessage("Success!")
            } catch (e: Exception) {
                Log.e("DocumentVerification", e.localizedMessage ?: "")
            }
        }
    }


    private fun verifyImage(pagesImage: ByteArray, documentType: String): String {
        val requestImageBody = pagesImage.toRequestBody("image/jpeg".toMediaType())

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("Pages", "image.jpg", requestImageBody)
            .addFormDataPart("DocumentType", documentType)
            .addFormDataPart("ClientReference", clientRef)
            .build()


        val request = Request.Builder()
            .url("https://api.w2globaldata.com/document-verification/verify")
            .addHeader("Authorization", "basic W2_API_KEY_HERE_NOT_LICENSE_KEY")
            .method("POST", requestBody)
            .build()


        val extendedTimeoutClient: OkHttpClient = httpClient.newBuilder()
            .readTimeout(100, TimeUnit.SECONDS)
            .build()

        val response = extendedTimeoutClient.newCall(request).execute()

        return response.body!!.string()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        docCapturer?.notifyActivityResult(requestCode, resultCode, data)
        facialCapturer?.notifyActivityResult(requestCode, resultCode, data)
    }

    suspend fun CoroutineScope.updateMessage(msg: String) {
        withContext(Dispatchers.Main) {
            message.text = msg
        }
    }

}
