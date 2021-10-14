package motobeans.architecture.customAppComponents.jetpack

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.finance.app.presenter.presenter.Presenter
import com.finance.app.presenter.presenter.ViewGeneric
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.constants.Constants
import motobeans.architecture.constants.ConstantsApi
import motobeans.architecture.development.interfaces.ApiProject
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.retrofit.response.Response
import javax.inject.Inject

abstract class SuperWorker(context : Context, params : WorkerParameters) : Worker(context, params) {
    @Inject
    lateinit var dataBase: DataBaseUtil
    @Inject
    lateinit var api: ApiProject

    private val presenter = Presenter()

    init {
        ArchitectureApp.instance.component.inject(this)
        getDataFromApi()
    }

    @SuppressLint("RestrictedApi", "CheckResult")
    override fun doWork(): Result {
        return Result.success()
    }

    private fun getDataFromApi() {
//        presenter.callNetwork(ConstantsApi.CALL_ALL_MASTER_VALUE, dmiConnector = AllMasterDropdownData())
//        presenter.callNetwork(ConstantsApi.CALL_LOAN_PRODUCT, dmiConnector = LoanProductsDropdown())
//        presenter.callNetwork(ConstantsApi.CALL_ALL_STATES, dmiConnector = AllStatesList())
    }

/*
    inner class AllMasterDropdownData : ViewGeneric<String?, Response.ResponseAllMasterDropdown>(context = applicationContext) {
        override val apiRequest: String?
            get() = null

        override fun getApiSuccess(value: Response.ResponseAllMasterDropdown) {
            if (value.responseCode == Constants.SUCCESS) {
                GlobalScope.launch {
                    dataBase.provideDataBaseSource().allMasterDropDownDao().insertAllMasterDropDownValue(value.responseObj)
                }
            } else {
                showToast(value.responseMsg)
            }
        }
    }

    inner class LoanProductsDropdown : ViewGeneric<String?, Response.ResponseLoanProduct>(context = applicationContext) {
        override val apiRequest: String?
            get() = null

        override fun getApiSuccess(value: Response.ResponseLoanProduct) {
            if (value.responseCode == Constants.SUCCESS) {
                GlobalScope.launch {
                    dataBase.provideDataBaseSource().loanProductDao().insertLoanProductList(value.responseObj)
                }
            } else {
                showToast(value.responseMsg)
            }
        }
    }

    inner class AllStatesList : ViewGeneric<String?, Response.ResponseStatesDropdown>(context = applicationContext) {
        override val apiRequest: String?
            get() = null

        override fun getApiSuccess(value: Response.ResponseStatesDropdown) {
            if (value.responseCode == Constants.SUCCESS) {
                GlobalScope.launch {
                    dataBase.provideDataBaseSource().statesDao().insertStates(value.responseObj)
                }
            } else {
                showToast(value.responseMsg)
            }
        }
    }
*/

}