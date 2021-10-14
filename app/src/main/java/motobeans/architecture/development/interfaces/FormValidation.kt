package motobeans.architecture.development.interfaces

import com.finance.app.databinding.*
import com.finance.app.persistence.model.DropdownMaster
import com.finance.app.persistence.model.LoanProductMaster
import com.finance.app.persistence.model.LoanPurpose
import com.finance.app.persistence.model.UserBranches
import com.finance.app.view.activity.UpdateCallActivity
import com.finance.app.view.customViews.ChannelPartnerViewCreateLead
import com.finance.app.view.customViews.CustomChannelPartnerView
import com.finance.app.view.customViews.CustomDocumentCheckListView
import com.finance.app.view.customViews.CustomSpinnerView

interface FormValidation {
    fun validateTemp(binding: TempActivityBinding): Boolean
    fun validateLogin(binding: ActivityLoginBinding): Boolean
    fun validatePersonalInfo(binding: LayoutCustomViewPersonalBinding,
                             spinnerDMList: ArrayList<CustomSpinnerView<DropdownMaster>>,religion:CustomSpinnerView<DropdownMaster> ): Boolean

    fun validateLoanInformation(binding: FragmentLoanInformationBinding,
                                loanProduct: CustomSpinnerView<LoanProductMaster>,
                                loanPurpose: CustomSpinnerView<LoanPurpose>,
                                spinnerDMList: ArrayList<CustomSpinnerView<DropdownMaster>>,
                                customChannelPartnerView: CustomChannelPartnerView): Boolean
    fun validateSalaryEmployment(salaryBinding: LayoutSalaryBinding, salarySpinnerList: ArrayList<CustomSpinnerView<DropdownMaster>>): Boolean
    fun validateSenpEmployment(senpBinding: LayoutSenpBinding, senpSpinnerList: ArrayList<CustomSpinnerView<DropdownMaster>>): Boolean
    fun validateEmploymentSalary(salaryBinding: LayoutSalaryBinding): Boolean
    fun validateEmploymentBusiness(businessBinding: LayoutSenpBinding): Boolean
    fun validateAddLead(binding: ActivityLeadCreateBinding, loanProduct: CustomSpinnerView<LoanProductMaster>, branches: CustomSpinnerView<UserBranches>,customChannelPartnerViewCreateLead: ChannelPartnerViewCreateLead): Boolean
    fun validateBankDetail(binding: DialogBankDetailFormBinding): Boolean
    fun validateReference(binding: DialogReferenceDetailsBinding): Boolean
    fun validateProperty(binding: FragmentPropertyInfoBinding): Boolean
    fun validateAssets(binding: FragmentAssetLiablityBinding): Boolean
    fun validateCards(binding: LayoutCreditCardDetailsBinding): Boolean
    fun validateObligations(binding: LayoutObligationBinding): Boolean
    fun validateObligationDialog(binding: AddObligationDialogBinding): Boolean
    fun validateAssetLiabilityForm(binding: FragmentAssetLiablityBinding): Boolean
    fun disableAssetLiabilityFields(binding: FragmentAssetLiablityBinding)
    fun validateUpdateCallForm(binding: ActivityUpdateCallBinding, formType: UpdateCallActivity.RequestLayout): Boolean
    fun validateCardsDialog(binding: AssetCreditcardDialogBinding): Boolean
    fun validateAssetsDialog(binding: AddAssestsDialogBinding): Boolean
    fun validateAssetLiabilityInfo(binding: LayoutCustomviewAssetliabilityBinding): Boolean
    fun validateKycDetail(binding: LayoutKycFormBinding): Boolean
    fun validateKycDocumentDetail(binding: ActivityDocumentUploadingBinding):Boolean
    fun validateResetPassword(binding:ActivityResetPasswordBinding):Boolean
    fun validateForgetPassword(binding: ActivityForgetPasswordBinding): Boolean
    fun validatePassword(binding: ActivitySetPasswordBinding): Boolean
    fun validateKycDocumentDetailSelf(binding: SelfDeclarationUploadDocumentActivityBinding):Boolean
    fun validateUploadKycDocumentDetail(binding: PerformKycDocumentUploadActivityBinding):Boolean


}
