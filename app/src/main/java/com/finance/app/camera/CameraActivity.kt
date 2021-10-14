package com.finance.app.camera

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.finance.app.R
import com.finance.app.persistence.model.AllMasterDropDown
import com.finance.app.view.activity.PerformKycDocumentUploadActivity
import com.finance.app.view.activity.SelfDeclarationUploadDocumentActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.otaliastudios.cameraview.*
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.controls.Preview
import com.otaliastudios.cameraview.frame.Frame
import com.otaliastudios.cameraview.frame.FrameProcessor
import java.io.ByteArrayOutputStream
import java.io.File

import com.finance.customerapp.camera.OptionView
import com.google.android.material.bottomsheet.BottomSheetDialog
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.Constants
import java.io.File.separator
import java.io.FileOutputStream
import java.io.OutputStream

class CameraActivity : AppCompatActivity(), View.OnClickListener, OptionView.Callback {

    companion object {
        private val LOG = CameraLogger.create("DemoApp")
        private const val USE_FRAME_PROCESSOR = false
        private const val DECODE_BITMAP = false
        const val DOCUMENT_UPLOAD_BUNDLESELF = "document_upload_bundle"
        fun startActivity(context: Context , bundle: Bundle) {
            val intent = Intent(context , CameraActivity::class.java)
            intent.putExtra(DOCUMENT_UPLOAD_BUNDLESELF , bundle)
            context.startActivity(intent)
        }
    }
    private var screenTitle: String? = ""
    private var docCodeId: Int? = null
    private var applicantNumber: String? = null
    private var applicationDocumentID: String? = null
    private var allMasterDropdown: AllMasterDropDown? = null
    private val camera: CameraView by lazy { findViewById<CameraView>(R.id.camera) }
    private val controlPanel: ViewGroup by lazy { findViewById<ViewGroup>(R.id.controls) }
    private var captureTime: Long = 0
    private var documentID : Int ? = null
    private var documentName : String ? = null
    private var moduleName : String ? = null

    private var currentFilter = 0
    var bottomSheetDialoge : BottomSheetDialog?= null
    //private val allFilters = Filters.values()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_capture_activity)
        ArchitectureApp.instance.component.inject(this)
        getBundleData()
        CameraLogger.setLogLevel(CameraLogger.LEVEL_VERBOSE)
        camera.setLifecycleOwner(this)
        camera.addCameraListener(Listener())
        camera.setSnapshotMaxWidth(960)
        camera.setSnapshotMaxHeight(1280)
        if (USE_FRAME_PROCESSOR) {
            camera.addFrameProcessor(object : FrameProcessor {
                private var lastTime = System.currentTimeMillis()
                override fun process(frame: Frame) {
                    val newTime = frame.time
                    val delay = newTime - lastTime
                    lastTime = newTime
                    LOG.v("Frame delayMillis:", delay, "FPS:", 1000 / delay)
                    if (DECODE_BITMAP) {
                        if (frame.format == ImageFormat.NV21
                                ) {
                            //val data = frame.getData<ByteArray>()
                            /*val yuvImage = YuvImage(data,
                                    frame.format,
                                    frame.size.width,
                                    frame.size.height,
                                    null)*/
                            val jpegStream = ByteArrayOutputStream()
                            /*yuvImage.compressToJpeg(Rect(0, 0,
                                    frame.size.width,
                                    frame.size.height), 100, jpegStream)*/
                            val jpegByteArray = jpegStream.toByteArray()
                            val bitmap = BitmapFactory.decodeByteArray(jpegByteArray,
                                    0, jpegByteArray.size)
                            bitmap.toString()
                        }
                    }
                }
            })
        }
        findViewById<LinearLayout>(R.id.linearInfo).setOnClickListener(this)
        //findViewById<View>(R.id.capturePicture).setOnClickListener(this)
        findViewById<LinearLayout>(R.id.linearCameraCapture).setOnClickListener(this)
        //findViewById<View>(R.id.captureVideo).setOnClickListener(this)
        //findViewById<View>(R.id.captureVideoSnapshot).setOnClickListener(this)
        findViewById<LinearLayout>(R.id.linearToggleCamera).setOnClickListener(this)
        //findViewById<View>(R.id.changeFilter).setOnClickListener(this)
        //val group = controlPanel.getChildAt(0) as ViewGroup
        //val watermark = findViewById<View>(R.id.watermark)
        val options: List<Option<*>> = listOf(
                // Layout
                Option.Width(), Option.Height(),
                // Engine and preview
                Option.Mode(), Option.Engine(), Option.Preview(),
                // Some controls
                Option.Flash(), Option.WhiteBalance(), Option.Hdr(),
                //Option.PictureMetering(), Option.PictureSnapshotMetering(),
               // Option.PictureFormat(),
                // Video recording
                //Option.PreviewFrameRate(), Option.VideoCodec(), Option.Audio(), Option.AudioCodec(),
                // Gestures
                Option.Pinch(), Option.HorizontalScroll(), Option.VerticalScroll(),
                Option.Tap(), Option.LongTap(),
                // Watermarks
                //Option.OverlayInPreview(watermark),
                //Option.OverlayInPictureSnapshot(watermark),
                //Option.OverlayInVideoSnapshot(watermark),
                // Frame Processing
                //Option.FrameProcessingFormat(),
                // Other
                Option.Grid(), Option.GridColor(), Option.UseDeviceOrientation()
        )
        val dividers = listOf(
                // Layout
                false, true,
                // Engine and preview
                false, false, true,
                // Some controls
                false, false, false, false, false, true,
                // Video recording
                false, false, false, true,
                // Gestures
                false, false, false, false, true,
                // Watermarks
                false, false, true,
                // Frame Processing
                true,
                // Other
                false, false, true
        )
        /*for (i in options.indices) {
            val view = OptionView<Any>(this)
            view.setOption(options[i] as Option<Any>, this)
            view.setHasDivider(dividers[i])
            group.addView(view, MATCH_PARENT, WRAP_CONTENT)
        }
        controlPanel.viewTreeObserver.addOnGlobalLayoutListener {
            BottomSheetBehavior.from(controlPanel).state = BottomSheetBehavior.STATE_HIDDEN
        }*/

        // Animate the watermark just to show we record the animation in video snapshots
        /*val animator = ValueAnimator.ofFloat(1f, 0.8f)
        animator.duration = 300
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE
        animator.addUpdateListener { animation ->
            val scale = animation.animatedValue as Float
            val scale = animation.animatedValue as Floatn
            watermark.scaleX = scale
            watermark.scaleY = scale
            watermark.rotation = watermark.rotation + 2
        }
        animator.start()*/
    }

    private fun message(content: String, important: Boolean) {
        if (important) {
            LOG.w(content)
            Toast.makeText(this, content, Toast.LENGTH_LONG).show()
        } else {
            LOG.i(content)
            Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(moduleName == "KYCDocument")
        {
            val bundle = Bundle()
            bundle.putInt(Constants.KEY_DOC_ID , docCodeId!!)
            bundle.putString(Constants.KEY_TITLE , this.getString(R.string.kyc_auth_image))
            bundle.putString(Constants.KEY_APPLICANT_NUMBER , applicantNumber)
            PerformKycDocumentUploadActivity.startActivity(this , bundle)
            finish()
        }else
        {
            val bundle = Bundle()
            bundle.putInt(Constants.KEY_DOC_ID , docCodeId!!)
            bundle.putString(Constants.KEY_TITLE , this.getString(R.string.face_auth_image))
            bundle.putString(Constants.KEY_APPLICANT_NUMBER , applicantNumber)
            SelfDeclarationUploadDocumentActivity.startActivity(this , bundle)
            finish()
        }
    }
    private fun getBundleData() {
        val bundle = intent.getBundleExtra(DOCUMENT_UPLOAD_BUNDLESELF)
        bundle?.let {

            screenTitle = it.getString(Constants.KEY_TITLE)
            System.out.println("CameraActivityScreenTitle>>>>"+screenTitle)
            docCodeId = it.getInt(Constants.KEY_DOC_ID)
            applicantNumber = it.getString(Constants.KEY_APPLICANT_NUMBER)
            documentID = it.getString(Constants.KEY_DOCUMENTID)?.toInt()
            documentName = it.getString(Constants.KEY_DOCUMENTNAME)
            moduleName = it.getString(Constants.KEY_MODULE_NAME)
            if (it.containsKey(Constants.KEY_FORM_ID)) {
                applicationDocumentID = it.getString(Constants.KEY_FORM_ID)
            }
            System.out.println("documentID>>>"+documentID+"documentName>>"+documentName+"applicantNumber>>>"+applicantNumber+"applicationDocumentID>>>"+applicationDocumentID)
        }
    }

    private inner class Listener : CameraListener() {
        override fun onCameraOpened(options: CameraOptions) {
            /*val group = controlPanel.getChildAt(0) as ViewGroup
            for (i in 0 until group.childCount) {
                val view = group.getChildAt(i) as OptionView<*>
                view.onCameraOpened(camera, options)
            }*/
        }

        override fun onCameraError(exception: CameraException) {
            super.onCameraError(exception)
            message("Got CameraException #" + exception.reason, true)
        }

        override fun onPictureTaken(result: PictureResult) {
            super.onPictureTaken(result)
            if (camera.isTakingVideo) {
                message("Captured while taking video. Size=" + result.size, false)
                return
            }

            // This can happen if picture was taken with a gesture.
            val callbackTime = System.currentTimeMillis()
            if (captureTime == 0L) captureTime = callbackTime - 300
            LOG.w("onPictureTaken called! Launching activity. Delay:", callbackTime - captureTime)
            val bundle = Bundle()
            bundle.putInt(Constants.KEY_DOC_ID , docCodeId!!)
            bundle.putString(Constants.KEY_TITLE , getString(R.string.face_auth_image))
            bundle.putString(Constants.KEY_APPLICANT_NUMBER , applicantNumber)
            bundle.putString(Constants.KEY_MODULE_NAME,moduleName)
            bundle.putString(Constants.KEY_DOCUMENTID , documentID.toString())
            bundle.putString(Constants.KEY_DOCUMENTNAME , documentName)
            PicturePreviewActivity.pictureResult = result
            PicturePreviewActivity.startActivity(this@CameraActivity,bundle)
            finish()
           /* val intent = Intent(this@CameraActivity, PicturePreviewActivity::class.java)
            intent.putExtra("delay", callbackTime - captureTime)
            startActivity(intent)*/
            captureTime = 0
            LOG.w("onPictureTaken called! Launched activity.")
        }

        /*override fun onVideoTaken(result: VideoResult) {
            super.onVideoTaken(result)
            LOG.w("onVideoTaken called! Launching activity.")
            VideoPreviewActivity.videoResult = result
            val intent = Intent(this@CameraActivity, VideoPreviewActivity::class.java)
            startActivity(intent)
            LOG.w("onVideoTaken called! Launched activity.")
        }*/

        override fun onVideoRecordingStart() {
            super.onVideoRecordingStart()
            LOG.w("onVideoRecordingStart!")
        }
        /// @param folderName can be your app's name


        private fun contentValues() : ContentValues {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            return values
        }

        private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
            if (outputStream != null) {
                try {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun onVideoRecordingEnd() {
            super.onVideoRecordingEnd()
            message("Video taken. Processing...", false)
            LOG.w("onVideoRecordingEnd!")
        }

        override fun onExposureCorrectionChanged(newValue: Float, bounds: FloatArray, fingers: Array<PointF>?) {
            super.onExposureCorrectionChanged(newValue, bounds, fingers)
            message("Exposure correction:$newValue", false)
        }

        override fun onZoomChanged(newValue: Float, bounds: FloatArray, fingers: Array<PointF>?) {
            super.onZoomChanged(newValue, bounds, fingers)
            message("Zoom:$newValue", false)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.linearInfo -> openBottomSheetInfo()
            //R.id.capturePicture -> capturePicture()
            R.id.linearCameraCapture -> capturePictureSnapshot()
            //R.id.captureVideo -> captureVideo()
            //R.id.captureVideoSnapshot -> captureVideoSnapshot()
            R.id.linearToggleCamera -> toggleCamera()
            //R.id.changeFilter -> changeCurrentFilter()
        }
    }

    /*override fun onBackPressed() {
        val b = BottomSheetBehavior.from(controlPanel)
        if (b.state != BottomSheetBehavior.STATE_HIDDEN) {
            b.state = BottomSheetBehavior.STATE_HIDDEN
            return
        }
        super.onBackPressed()
    }*/

    private fun edit() {
        BottomSheetBehavior.from(controlPanel).state = BottomSheetBehavior.STATE_COLLAPSED
    }

    /*private fun capturePicture() {
        if (camera.mode == Mode.VIDEO) return run {
            message("Can't take HQ pictures while in VIDEO mode.", false)
        }
        if (camera.isTakingPicture) return
        captureTime = System.currentTimeMillis()
        message("Capturing picture...", false)
        camera.takePicture()
    }*/

    private fun capturePictureSnapshot() {
        if (camera.isTakingPicture) return
        if (camera.preview != Preview.GL_SURFACE) return run {
            message("Picture snapshots are only allowed with the GL_SURFACE preview.", true)
        }
        captureTime = System.currentTimeMillis()
        //message("Capturing picture snapshot...", false)
        camera.takePictureSnapshot()
    }

   /* private fun captureVideo() {
        if (camera.mode == Mode.PICTURE) return run {
            message("Can't record HQ videos while in PICTURE mode.", false)
        }
        if (camera.isTakingPicture || camera.isTakingVideo) return
        message("Recording for 5 seconds...", true)
        camera.takeVideo(File(filesDir, "video.mp4"), 5000)
    }*/

    /*private fun captureVideoSnapshot() {
        if (camera.isTakingVideo) return run {
            message("Already taking video.", false)
        }
        if (camera.preview != Preview.GL_SURFACE) return run {
            message("Video snapshots are only allowed with the GL_SURFACE preview.", true)
        }
        message("Recording snapshot for 5 seconds...", true)
        camera.takeVideoSnapshot(File(filesDir, "video.mp4"), 5000)
    }*/

    private fun toggleCamera() {
        if (camera.isTakingPicture || camera.isTakingVideo) return
        when (camera.toggleFacing()) {
            Facing.BACK -> message("Switched to back camera!", false)
            Facing.FRONT -> message("Switched to front camera!", false)
        }
    }

    /*private fun changeCurrentFilter() {
        if (camera.preview != Preview.GL_SURFACE) return run {
            message("Filters are supported only when preview is Preview.GL_SURFACE.", true)
        }
        if (currentFilter < allFilters.size - 1) {
            currentFilter++
        } else {
            currentFilter = 0
        }
        val filter = allFilters[currentFilter]
        message(filter.toString(), false)

        // Normal behavior:
        //camera.filter = filter.newInstance()

        // To test MultiFilter:
        // DuotoneFilter duotone = new DuotoneFilter();
        // duotone.setFirstColor(Color.RED);
        // duotone.setSecondColor(Color.GREEN);
        // camera.setFilter(new MultiFilter(duotone, filter.newInstance()));
    }*/

    override fun <T : Any> onValueChanged(option: Option<T>, value: T, name: String): Boolean {
        if (option is Option.Width || option is Option.Height) {
            val preview = camera.preview
            val wrapContent = value as Int == WRAP_CONTENT
            if (preview == Preview.SURFACE && !wrapContent) {
                message("The SurfaceView preview does not support width or height changes. " +
                        "The view will act as WRAP_CONTENT by default.", true)
                return false
            }
        }
        option.set(camera, value)
        //BottomSheetBehavior.from(controlPanel).state = BottomSheetBehavior.STATE_HIDDEN
        message("Changed " + option.name + " to " + name, false)
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val valid = grantResults.all { it == PERMISSION_GRANTED }
        if (valid && !camera.isOpened) {
            camera.open()
        }
    }
    private fun openBottomSheetInfo(){
        bottomSheetDialoge = BottomSheetDialog(this,R.style.BottomSheetDialogTheme)
        val view = LayoutInflater.from(this).inflate(R.layout.camera_layout_temp,null)
        view.findViewById<ImageView>(R.id.menu_close).setOnClickListener {
            bottomSheetDialoge?.dismiss()
        }
        bottomSheetDialoge?.setContentView(view)
        bottomSheetDialoge?.setCancelable(false)
        bottomSheetDialoge?.show()
    }
}
