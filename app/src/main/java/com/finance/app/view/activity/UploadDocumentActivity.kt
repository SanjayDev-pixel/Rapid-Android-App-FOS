package com.finance.app.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.finance.app.R
import com.finance.app.databinding.ActivityUploadDocumentBinding
import com.finance.app.persistence.model.DropdownMaster
import com.finance.app.presenter.connector.DocumentUploadConnector
import com.finance.app.presenter.presenter.DocumentUploadPresenter
import com.finance.app.view.adapters.recycler.spinner.MasterSpinnerAdapter
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.customAppComponents.activity.BaseAppCompatActivity
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import motobeans.architecture.retrofit.response.Response
import motobeans.architecture.util.delegates.ActivityBindingProviderDelegate
import retrofit2.http.Multipart
import javax.inject.Inject

class UploadDocumentActivity : BaseAppCompatActivity(), DocumentUploadConnector.ViewOpt {
    // used to bind element of layout to activity
    private val binding: ActivityUploadDocumentBinding by ActivityBindingProviderDelegate(
            this, R.layout.activity_upload_document)
    @Inject
    lateinit var sharedPreferences: SharedPreferencesUtil
    @Inject
    lateinit var dataBase: DataBaseUtil
    private val documentPresenter = DocumentUploadPresenter(this)
    private lateinit var documentType: ArrayList<DropdownMaster>
    private var bundle: Bundle? = null

    companion object {
        private const val KEY_DOCUMENT_TYPE = "documentType"
        fun start(context: Context, dropDowns: ArrayList<DropdownMaster>) {
            val intent = Intent(context, UploadDocumentActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(KEY_DOCUMENT_TYPE, dropDowns)
            intent.putExtras(bundle)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun init() {
        ArchitectureApp.instance.component.inject(this)
        getDocumentType()
        hideToolbar()
        hideSecondaryToolbar()
        setClickListeners()
    }

    private fun getDocumentType() {
        documentType = ArrayList()
        bundle = intent.extras
        bundle?.let {
            documentType = bundle!!.getSerializable(KEY_DOCUMENT_TYPE) as ArrayList<DropdownMaster>
        }
        setDropDownValue(documentType)
    }

    private fun setDropDownValue(documentType: ArrayList<DropdownMaster>) {
        binding.spinnerDocumentType.adapter = MasterSpinnerAdapter(this, documentType)
    }

    private fun setClickListeners() {
        binding.btnUpload.setOnClickListener {
            documentPresenter.callNetwork(ConstantsApi.CALL_DOCUMENT_UPLOAD)
        }
    }

    override val leadId: Int?
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val applicantId: Int?
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val documentName: String?
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val documentTypeDetailId: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val actualDocument: Multipart
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun getDocumentUploadSuccess(value: Response.ResponseDocumentUpload) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDocumentUploadFailure(msg: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                if (data != null) {
                    val contentURI = data.data
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, contentURI)
                        image = bitmap
                        binding.ivUploadKyc.setImageBitmap(bitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(requireContext(), "Failed!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (requestCode == CAMERA) {
                val bitmap = data!!.extras!!.get("data") as Bitmap
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream)
                val byteArray = stream.toByteArray()
                val thumbnail = BitmapFactory.decodeByteArray(byteArray, 0,
                        byteArray.size)
                binding.ivUploadKyc.setImageBitmap(thumbnail)
                image = thumbnail
                Toast.makeText(requireContext(), "Image Saved!", Toast.LENGTH_SHORT).show()
            }
            binding.rcKYC.adapter!!.notifyDataSetChanged()
        }
    }
*/

}
