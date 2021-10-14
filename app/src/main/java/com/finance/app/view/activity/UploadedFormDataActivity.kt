package com.finance.app.view.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.finance.app.R
import com.finance.app.databinding.ActivityUploadedFormDataBinding
import com.finance.app.view.adapters.recycler.adapter.UploadFormDataAdapter
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.customAppComponents.activity.BaseAppCompatActivity
import motobeans.architecture.util.delegates.ActivityBindingProviderDelegate
import java.io.IOException

class UploadedFormDataActivity : BaseAppCompatActivity() {
    private val binding: ActivityUploadedFormDataBinding by ActivityBindingProviderDelegate(
            this, R.layout.activity_uploaded_form_data)
    private var bundle: Bundle? = null
    private lateinit var imageAdapter: UploadFormDataAdapter
    companion object {
        private const val KEY_IMAGE = "image"
        private const val KEY_PDF = "pdf"
        private const val CAMERA = 1
        private var image: Bitmap? = null
        private var pdf: Uri? = null
        private lateinit var imageBitmap: ArrayList<Bitmap>
        private lateinit var pdfURI: ArrayList<Uri>
        fun start(context: Context, image: Bitmap?, pdf: Uri?) {
            val bundle = Bundle()
            bundle.putParcelable(KEY_IMAGE, image)
            bundle.putParcelable(KEY_PDF, pdf)
            val intent = Intent(context, UploadedFormDataActivity::class.java)
            intent.putExtras(bundle)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun init() {
        hideSecondaryToolbar()
        ArchitectureApp.instance.component.inject(this)
        imageBitmap = ArrayList()
        pdfURI = ArrayList()
        bundle = intent.extras
        bundle?.let {
            image = bundle!!.getParcelable(KEY_IMAGE)
            if (image != null) {
                imageBitmap.add(image!!)
            }
            pdf = bundle!!.getParcelable(KEY_PDF)
            if (pdf != null) {
                pdfURI.add(pdf!!)
            }
        }

        showImages()
        binding.btnAdd.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA)
        }
    }

    private fun showImages() {
        binding.rcView.layoutManager = LinearLayoutManager(this)
        imageAdapter = UploadFormDataAdapter(this, imageBitmap)
        binding.rcView.adapter = imageAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA && resultCode == RESULT_OK) {
            try {
                val bitmap = data?.extras!!.get("data") as Bitmap
                imageBitmap.add(bitmap)
                binding.rcView.adapter!!.notifyDataSetChanged()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
