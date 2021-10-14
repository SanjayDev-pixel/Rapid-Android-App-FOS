package com.finance.app.view.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.afollestad.assent.Assent
import com.afollestad.assent.AssentCallback
import com.finance.app.R
import com.finance.app.camera.CameraActivity
import com.finance.app.databinding.ActivityDocumentUploadingBinding
import com.finance.app.databinding.SelfDeclarationUploadDocumentActivityBinding
import com.finance.app.locationTracker.ForegroundLocationTrackerService
import com.finance.app.persistence.model.DocumentTypeModel
import com.finance.app.persistence.model.KycDocumentModel
import com.finance.app.presenter.presenter.Presenter
import com.finance.app.presenter.presenter.ViewGeneric
import com.finance.app.utility.LeadMetaData
import com.finance.app.view.adapters.recycler.adapter.UploadedDocumentListAdapter
import com.finance.app.view.utils.getImageUriForImagePicker
import com.finance.app.view.utils.startFilePickerActivity
import com.finance.app.view.utils.startImagePickerActivity
import com.finance.app.workers.document.UploadDocumentWorker
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.Constants
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.customAppComponents.activity.BaseAppCompatActivity
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.development.interfaces.FormValidation
import motobeans.architecture.retrofit.request.Requests
import motobeans.architecture.retrofit.response.Response
import motobeans.architecture.util.delegates.ActivityBindingProviderDelegate
import motobeans.architecture.util.exIsNotEmptyOrNullOrBlank
import java.io.FileNotFoundException
import java.util.*
import javax.inject.Inject

class SelfDeclarationUploadDocumentActivity : BaseAppCompatActivity() {

    @Inject
    lateinit var dataBase: DataBaseUtil
    @Inject
    lateinit var formValidation: FormValidation

    private var screenTitle: String? = ""
    private var docCodeId: Int? = null
    private var applicantNumber: String? = null
    private var selectedDocumentUri: Uri? = null
    private var applicationDocumentID: String? = null
    var allUploadedDocumentTypes : ArrayList<DocumentTypeModel> ?=  null
    private val presenter = Presenter()
    var bottomSheetDialoge: BottomSheetDialog? = null
    private val binding: SelfDeclarationUploadDocumentActivityBinding by ActivityBindingProviderDelegate(this , R.layout.self_declaration_upload_document_activity)

    companion object {
        const val DOCUMENT_REQ_CODE = 1000
        const val DOCUMENT_IMAGE_REQ_CODE = 1001
        const val DOCUMENT_UPLOAD_BUNDLE = "document_upload_bundle"

        fun startActivity(context: Context , bundle: Bundle) {
            val intent = Intent(context , SelfDeclarationUploadDocumentActivity::class.java)
            intent.putExtra(DOCUMENT_UPLOAD_BUNDLE , bundle)
            context.startActivity(intent)
        }
    }

    inner class DocumentTypeListRequest : ViewGeneric<Requests.RequestDocumentList , Response.ResponseDocumentList>(context = this) {
        override val apiRequest: Requests.RequestDocumentList?
            get() = getDocumentTypeListRequest()

        override fun getApiSuccess(value: Response.ResponseDocumentList) {
            hideProgressDialog()
            if(value.responseObj!= null)
            {
                var documentTypestemp : ArrayList<DocumentTypeModel> = ArrayList()
                for(i in 0 until value.responseObj?.documentTypes!!.size)
                {

                    System.out.println("document Name>>>"+value.responseObj.documentTypes[i])
                    if(value.responseObj?.documentTypes[i].documentSubTypeDetail =="UID")
                    {
                        documentTypestemp.add(value.responseObj.documentTypes[i])

                    }
                    else if( value.responseObj?.documentTypes[i].documentSubTypeDetail =="FaceAuthImageWithID")
                    {
                        documentTypestemp.add(value.responseObj.documentTypes[i])
                    }

                }
                setDocumentTypeSpinner(documentTypes = documentTypestemp)
            }


            /*value.responseObj?.let { setDocumentTypeSpinner(it.documentTypes) } ?: kotlin.run {}*/
        }

        override fun getApiFailure(msg: String) {
            hideProgressDialog()
            Toast.makeText(this@SelfDeclarationUploadDocumentActivity , msg , Toast.LENGTH_LONG).show()
        }
    }

    inner class UploadedDocumentListRequest : ViewGeneric<Requests.RequestUploadedDocumentList , Response.ResponseUploadedDocumentList>(context = this) {
        override val apiRequest: Requests.RequestUploadedDocumentList?
            get() = getUploadedDocumentListRequest()

        override fun getApiSuccess(value: Response.ResponseUploadedDocumentList) {
            binding.swipeLayoutDocument.isRefreshing = false
            value.responseObj?.let { response ->
                response.documents?.let { documentList ->
                    allUploadedDocumentTypes = documentList
                    setUploadedDocumentListAdapter(documentList)
                    getDocumentStatus(allUploadedDocumentTypes!!)
                }
            }
        }
    }

    inner class DownloadableDocumentLinkRequest(private val documentId: Int) : ViewGeneric<Requests.RequestDocumentDownloadableLink , Response.ResponseDocumentDownloadableLink>(context = this) {
        override val apiRequest: Requests.RequestDocumentDownloadableLink?
            get() = Requests.RequestDocumentDownloadableLink(documentId)
        override fun getApiSuccess(value: Response.ResponseDocumentDownloadableLink) {
            value.responseObj?.let { response ->
                response.documentPath?.let { url ->
                    val builder = CustomTabsIntent.Builder()
                    builder.setToolbarColor(resources.getColor(R.color.colorPrimary))
                    builder.setShowTitle(false)
                    val customTabsIntent = builder.build()
                    customTabsIntent.launchUrl(this@SelfDeclarationUploadDocumentActivity , Uri.parse(url))
                }
            }
        }
    }

    override fun init() {
        ArchitectureApp.instance.component.inject(this)
        hideToolbar()
        hideSecondaryToolbar()
        setOnClickListener()
        getBundleData()
        setLeadNumber()
        setScreenTitle()
        fetchDocumentTypeList()
        //fetchUploadedDocumentList()
    }
    private fun startLocationTrackerService(screenName : String?) {
        val leadId = LeadMetaData.getLeadId()
        val intent = Intent(applicationContext , ForegroundLocationTrackerService::class.java)
        intent.action = screenName
        intent.putExtra("LEADID",leadId)
        ContextCompat.startForegroundService(applicationContext , intent)
    }

    private fun setLeadNumber() {
        binding.header.tvLeadNumber.text = "${LeadMetaData.getLeadData()?.leadNumber}"
    }

    private fun setScreenTitle() {
        binding.tvLabelTitle.text = screenTitle?.let { title -> "$title Uploaded Document" } ?: run { "Uploaded Document" }
        startLocationTrackerService("Self-Declaration KYC")
    }

    private fun setOnClickListener() {
        binding.swipeLayoutDocument.setOnRefreshListener { fetchUploadedDocumentList() }
        binding.header.linearBack.setOnClickListener {
          //KYCActivity.start(applicationContext,applicantNumber,2)
               //onBackPressed()
            finish()
        }
        binding.btnPickFile.setOnClickListener {
            if(binding.etDocumentName.text.toString().exIsNotEmptyOrNullOrBlank()){
                val bundle = Bundle()
                bundle.putInt(Constants.KEY_DOC_ID , docCodeId!!)
                bundle.putString(Constants.KEY_TITLE , this.getString(R.string.face_auth_image))
                bundle.putString(Constants.KEY_MODULE_NAME , "SelfDeclaration")
                bundle.putString(Constants.KEY_APPLICANT_NUMBER , applicantNumber)
                bundle.putString(Constants.KEY_DOCUMENTID , (binding.spinnerDocumentType.selectedItem as DocumentTypeModel?)?.documentID.toString())
                bundle.putString(Constants.KEY_DOCUMENTNAME , binding.etDocumentName.text.toString())
                CameraActivity.startActivity(this,bundle)
                finish()
            }else
            {
                Toast.makeText(this,"Please Select Document",Toast.LENGTH_LONG).show()
            }
            /*Assent.requestPermissions(AssentCallback { result ->
                if (result.allPermissionsGranted())
                    showFileTypeChooserDialog()
                  *//* selectedDocumentUri = this@SelfDeclarationUploadDocumentActivity.getImageUriForImagePicker("doc_image_${Date().time}")
                    selectedDocumentUri?.let { uri ->
                    this@SelfDeclarationUploadDocumentActivity.startImagePickerActivity(DOCUMENT_IMAGE_REQ_CODE , uri)
                }*//*
            } , 1 , Assent.WRITE_EXTERNAL_STORAGE , Assent.CAMERA)*/

        }
        binding.btnUpload.setOnClickListener { if (isKycDocumentDetailValid()) saveKycDocumentIntoDatabase() }
        binding.btnCheckDocumentStatus.setOnClickListener {
            fetchUploadedDocumentList()

        }
    }

    private fun getDocumentStatus(documentList: ArrayList<DocumentTypeModel>){


        bottomSheetDialoge = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val view = LayoutInflater.from(this).inflate(R.layout.self_declaration_uploaded_document_tracking, null)
        view.findViewById<TextView>(R.id.txtCancel).setOnClickListener {
            bottomSheetDialoge?.dismiss()
        }
        var img_orderconfirmed = view.findViewById<ImageView>(R.id.img_orderconfirmed)
        var orderprocessed = view.findViewById<ImageView>(R.id.orderprocessed)
        var orderpickup = view.findViewById<ImageView>(R.id.orderpickup)
        var view_order_placed = view.findViewById<View>(R.id.view_order_placed)
        var supporter_placed = view.findViewById<View>(R.id.supporter_placed)
        var view_order_confirmed = view.findViewById<View>(R.id.view_order_confirmed)
        var placed_divider = view.findViewById<View>(R.id.placed_divider)
        var view_order_processed = view.findViewById<View>(R.id.view_order_processed)
        var ready_divider = view.findViewById<View>(R.id.ready_divider)
        var con_divider = view.findViewById<View>(R.id.con_divider)
        var text_confirmed = view.findViewById<TextView>(R.id.text_confirmed)
        var textorderprocessed = view.findViewById<TextView>(R.id.textorderprocessed)
        var textorderpickup = view.findViewById<TextView>(R.id.textorderpickup)
        var placed_desc = view.findViewById<TextView>(R.id.placed_desc)
        var confirmed_desc = view.findViewById<TextView>(R.id.confirmed_desc)
        var processed_desc = view.findViewById<TextView>(R.id.processed_desc)
        for(i in 0 until documentList.size){
            if(documentList[i].documentSubTypeDetailDisplayText == "Face Auth Image with ID")
            {

                //view_order_placed.setBackground(getResources().getDrawable(R.drawable.shape_status_current))
                view_order_placed.setBackground(getResources().getDrawable(R.drawable.shape_status_completed))
                placed_divider.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                if(documentList[i].isLivePhoto == "yes" || documentList[i].isLivePhoto == null) {
                    placed_desc.text = "Uploaded Successfully"
                    placed_desc.setTextColor(resources.getColor(R.color.black_new_3))
                }else{
                    placed_desc.text = "Uploaded document is not a live photo. please upload a live photo."
                    placed_desc.setTextColor(Color.RED)
                }
            }
            if(documentList[i].documentSubTypeDetailDisplayText == "UID"){
                view_order_confirmed.setBackground(getResources().getDrawable(R.drawable.shape_status_completed))
                con_divider.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                confirmed_desc.text = "Uploaded Successfully"
            }

        }

        bottomSheetDialoge?.setContentView(view)
        bottomSheetDialoge?.setCancelable(false)
        bottomSheetDialoge?.show()


    }
    private fun setDocumentTypeSpinner(documentTypes: ArrayList<DocumentTypeModel>) {
        val adapter = ArrayAdapter<DocumentTypeModel>(this , android.R.layout.simple_spinner_item , documentTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDocumentType.adapter = adapter
        binding.spinnerDocumentType.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*> ,
                                        view: View , position: Int , id: Long) {
                if(position>=0) {
                    var str = parent.getItemAtPosition(position).toString()
                    binding.etDocumentName.setText(str + "_" + applicantNumber)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}

        }
    }

    private fun setUploadedDocumentListAdapter(documentList: ArrayList<DocumentTypeModel>) {
        val adapter = UploadedDocumentListAdapter(documentList)
        binding.rvUploadedDocumentList.adapter = adapter
        adapter.setOnItemClickListener(object : UploadedDocumentListAdapter.ItemClickListener {
            override fun onKycDetailDownloadClicked(position: Int , documentTypeModel: DocumentTypeModel) {
                documentTypeModel.applicationDocumentID?.let { id ->
                    presenter.callNetwork(ConstantsApi.CALL_DOWNLOAD_DOCUMENT , dmiConnector = DownloadableDocumentLinkRequest(id))
                }
            }

            override fun onKycDetailDeleteClicked(position: Int , documentTypeModel: DocumentTypeModel) {
            }
        })
    }

    private fun getBundleData() {
        val bundle = intent.getBundleExtra(DOCUMENT_UPLOAD_BUNDLE)
        bundle?.let {

            screenTitle = it.getString(Constants.KEY_TITLE)
            docCodeId = it.getInt(Constants.KEY_DOC_ID)
            applicantNumber = it.getString(Constants.KEY_APPLICANT_NUMBER)
            if (it.containsKey(Constants.KEY_FORM_ID)) {
                applicationDocumentID = it.getString(Constants.KEY_FORM_ID)
            }
            System.out.println("screenTitle>>>"+screenTitle+"docCodeId>>"+docCodeId+"applicantNumber>>>"+applicantNumber+"applicationDocumentID>>>"+applicationDocumentID)
        }
    }

    private fun fetchDocumentTypeList() {
        showProgressDialog()
        if (docCodeId != null)
            presenter.callNetwork(ConstantsApi.CALL_DOC_TYPE , dmiConnector = DocumentTypeListRequest())
    }

    private fun fetchUploadedDocumentList() {
        if (LeadMetaData.getLeadId() != null && docCodeId != null)
            presenter.callNetwork(ConstantsApi.CALL_UPLOADED_DOC , dmiConnector = UploadedDocumentListRequest())
    }

    private fun getDocumentTypeListRequest(): Requests.RequestDocumentList? {
        return docCodeId?.let { Requests.RequestDocumentList(it) }
    }

    private fun getUploadedDocumentListRequest(): Requests.RequestUploadedDocumentList? {
           val leadId = LeadMetaData.getLeadId()
        System.out.println("LeadId>>>>>"+leadId)
        return docCodeId?.let { leadId?.let { it1 -> applicantNumber?.let { it2 -> Requests.RequestUploadedDocumentList(it , it1 , it2) } } }
        //return LeadMetaData.getLeadId()?.let { docCodeId?.let { codeId -> Requests.RequestUploadedDocumentList(codeId , it) } }
    }

    private fun onClearSelectedDocumentDetails() {
        binding.spinnerDocumentType.setSelection(0)
        binding.etDocumentName.setText("")
        binding.tvFileSizeErrorLabel.visibility = View.INVISIBLE
        selectedDocumentUri = null
    }

    private fun showFileTypeChooserDialog() {
        val actionList = arrayOf(Constants.ACTION_PICK_FILE , Constants.ACTION_TAKE_IMAGE)
        val builder = AlertDialog.Builder(this@SelfDeclarationUploadDocumentActivity)
        builder.setTitle("Choose Action!")
        builder.setItems(actionList) { _ , which ->
            when (actionList[which]) {
                Constants.ACTION_PICK_FILE -> this@SelfDeclarationUploadDocumentActivity.startFilePickerActivity(DOCUMENT_REQ_CODE)
                Constants.ACTION_TAKE_IMAGE -> {
                    selectedDocumentUri = this@SelfDeclarationUploadDocumentActivity.getImageUriForImagePicker("doc_image_${Date().time}")
                    selectedDocumentUri?.let { uri ->
                        this@SelfDeclarationUploadDocumentActivity.startImagePickerActivity(DOCUMENT_IMAGE_REQ_CODE , uri)
                    }
                }
            }
        }
        builder.show()
    }

    private fun onDocumentSelected(data: Intent?) {
        data?.let {
            selectedDocumentUri = it.data
            binding.btnPickFile.error = null
        }
    }

    private fun onImageSelected(data: Uri?) {
        data?.let {
            binding.btnPickFile.error = null
        }
    }

    private fun isKycDocumentDetailValid(): Boolean {
        if (selectedDocumentUri == null) {
            binding.btnPickFile.error = "Please choose document file"
            return false
        }

        try {
            val totalFileSizeInBytes = contentResolver?.openAssetFileDescriptor(selectedDocumentUri!! , "r")?.length
            totalFileSizeInBytes?.let { size ->
                if (size >= Constants.FILE_SIZE_ALLOWED) {
                    binding.tvFileSizeErrorLabel.visibility = View.VISIBLE
                    return false
                }
            }
        } catch (exp: FileNotFoundException) {
            return false
        }

        return formValidation.validateKycDocumentDetailSelf(binding)
    }

    private fun saveKycDocumentIntoDatabase() {
        val kycDocumentModel = KycDocumentModel()
        kycDocumentModel.leadID = LeadMetaData.getLeadId()
        kycDocumentModel.leadApplicantNumber = applicantNumber
        kycDocumentModel.applicationDocumentID = applicationDocumentID
        kycDocumentModel.documentID = (binding.spinnerDocumentType.selectedItem as DocumentTypeModel?)?.documentID
        kycDocumentModel.documentName = binding.etDocumentName.text.toString()
        kycDocumentModel.document = selectedDocumentUri.toString()
        System.out.println("SelctedDocumntURI"+selectedDocumentUri.toString())
        //kycDocumentModel.Active = 1
        kycDocumentModel.moduleName =Constants.SELF_DECLARATION
        //Now Save data into database,then start worker...
        val handler = Handler(Looper.getMainLooper())
        GlobalScope.launch {
            dataBase.provideDataBaseSource().kycDocumentDao().add(kycDocumentModel)
            startDocumentWorkerTask()
            handler.post {
                Toast.makeText(this@SelfDeclarationUploadDocumentActivity , "Document Saved Successfully" , Toast.LENGTH_LONG).show()
            }
        }
        //Now Clear the screen...
        onClearSelectedDocumentDetails()
    }


    private fun startDocumentWorkerTask() {

        val mWorkManager = WorkManager.getInstance()
        val mRequest = OneTimeWorkRequest.Builder(UploadDocumentWorker::class.java).build()
        val data = Data.Builder()

        mWorkManager.enqueue(mRequest)
    }

    override fun onActivityResult(requestCode: Int , resultCode: Int , data: Intent?) {
        when (requestCode) {
            DOCUMENT_REQ_CODE -> if (resultCode == Activity.RESULT_OK) onDocumentSelected(data) else selectedDocumentUri = null
            DOCUMENT_IMAGE_REQ_CODE -> if (resultCode == Activity.RESULT_OK) onImageSelected(selectedDocumentUri) else selectedDocumentUri = null
            else -> Toast.makeText(this@SelfDeclarationUploadDocumentActivity , "Did not pick image, please try again!" , Toast.LENGTH_LONG).show()
        }
    }

}