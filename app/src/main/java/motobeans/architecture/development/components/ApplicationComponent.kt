package motobeans.architecture.development.components

import android.app.Application
import com.finance.app.Report
import com.finance.app.TestActivity
import com.finance.app.camera.CameraActivity
import com.finance.app.camera.PicturePreviewActivity
import com.finance.app.locationTracker.ForegroundLocationTrackerService
import com.finance.app.presenter.presenter.*
import com.finance.app.utility.LeadAndLoanDetail
import com.finance.app.utility.LeadMetaData
import com.finance.app.view.activity.*
import com.finance.app.view.adapters.recycler.adapter.TempRecyclerAdapter
import com.finance.app.view.adapters.recycler.holder.TempHolder
import com.finance.app.view.customViews.*
import com.finance.app.view.dialogs.BankDetailDialogFragment
import com.finance.app.view.dialogs.KycDetailDialog
import com.finance.app.view.dialogs.ReferenceDetailDialogFragment
import com.finance.app.view.fragment.LeadsListingFragment
import com.finance.app.view.fragment.NavMenuFragment
import com.finance.app.view.fragment.PersonalFormFragment
import com.finance.app.view.fragment.loanApplicationFragments.FragmentPreview
import com.finance.app.view.fragment.loanApplicationFragments.LoanInfoFragmentNew
import com.finance.app.view.fragment.loanApplicationFragments.PropertyFragmentNew
import com.finance.app.view.fragment.loanApplicationFragments.ReferenceFragmentNew
import com.finance.app.view.fragment.loanApplicationFragments.assets_liability.AssetLiabilityFragmentForm
import com.finance.app.view.fragment.loanApplicationFragments.assets_liability.AssetLiabilityFragmentNew
import com.finance.app.view.fragment.loanApplicationFragments.bank.BankDetailFormFragment
import com.finance.app.view.fragment.loanApplicationFragments.bank.BankDetailFragmentNew
import com.finance.app.view.fragment.loanApplicationFragments.document_checklist.DocumentCheckListFragmentNew
import com.finance.app.view.fragment.loanApplicationFragments.document_checklist.DocumentChecklistForm
import com.finance.app.view.fragment.loanApplicationFragments.document_upload_kyc.DocumentFormFragment
import com.finance.app.view.fragment.loanApplicationFragments.document_upload_kyc.DocumentUploadFragmentNew
import com.finance.app.view.fragment.loanApplicationFragments.employment.EmploymentFormFragmentNew
import com.finance.app.view.fragment.loanApplicationFragments.employment.EmploymentInfoFragmentNew
import com.finance.app.view.fragment.loanApplicationFragments.personal.PersonalFormFragmentNew
import com.finance.app.view.fragment.loanApplicationFragments.personal.PersonalInfoFragmentNew
import com.finance.app.viewModel.AppDataViewModel
import com.finance.app.viewModel.LeadDataViewModel
import com.finance.app.viewModel.SyncDataViewModel
import com.finance.app.viewModel.TempViewModel
import com.finance.app.workers.document.UploadDocumentWorker
import com.finance.app.workers.location.UploadLocationWorker
import com.optcrm.optreporting.AppModule
import com.optcrm.optreporting.app.workers.UtilWorkersTask
import dagger.Component
import motobeans.architecture.customAppComponents.activity.BaseAppCompatActivity
import motobeans.architecture.customAppComponents.jetpack.SuperWorker
import motobeans.architecture.development.modules.NetworkModule
import motobeans.architecture.development.modules.PrimitivesModule
import motobeans.architecture.development.modules.UtilityModule
import org.w3c.dom.DocumentFragment
import javax.inject.Singleton
import com.finance.app.presenter.presenter.Presenter as Presenter1

/**
 * Created by munishkumarthakur on 04/11/17.
 */
@Singleton
@Component(
        modules = arrayOf(
                AppModule::class , NetworkModule::class , UtilityModule::class , PrimitivesModule::class
        )
)
interface ApplicationComponent {

    fun inject(app: Application)

    /**
     * Activities
     */
    fun inject(activity: TestActivity)

    fun inject(activity: BaseAppCompatActivity)
    fun inject(activity: DashboardActivity)
    fun inject(activity: SyncActivity)
    fun inject(activity: LoginActivity)
    fun inject(activity: LoanApplicationActivity)
    fun inject(activity: SplashScreen)
    fun inject(creationActivity: CreateLeadActivity)
    fun inject(activity: AllLeadActivity)
    fun inject(activity: LeadDetailActivity)
    fun inject(activity: UpdateCallActivity)
    fun inject(activity: DocumentUploadingActivity)
    fun inject(activity: ForgetPasswordActivity)
    fun inject(activity: SetPasswordActivity)
    fun inject(activity : KYCActivity)
    fun inject(activity : SelfDeclarationUploadDocumentActivity)
    fun inject(activity : PerformKycDocumentUploadActivity)
    fun inject(activity : ApplicantKycListActivity)
    fun inject(activity : Report)
    fun inject(activityCameraActivity: CameraActivity)
    fun inject(activityPicturePreviewActivity: PicturePreviewActivity)
    fun inject(activity:FaqActivity)

    /**
     * Fragment
     */
    fun inject(fragment: NavMenuFragment)

    fun inject(fragment: PersonalFormFragment)
    fun inject(fragment: LeadsListingFragment)
    fun inject(fragment: AssetLiabilityFragmentForm)
    fun inject(fragment: DocumentChecklistForm)


    fun inject(fragment: LoanInfoFragmentNew)
    fun inject(fragment: PersonalInfoFragmentNew)
    fun inject(fragment: PersonalFormFragmentNew)
    fun inject(fragment: EmploymentInfoFragmentNew)
    fun inject(fragment: EmploymentFormFragmentNew)
    fun inject(fragment: BankDetailFragmentNew)
    fun inject(fragment: BankDetailFormFragment)
    fun inject(fragment: AssetLiabilityFragmentNew)
    fun inject(fragment: PropertyFragmentNew)
    fun inject(fragment: ReferenceFragmentNew)
    fun inject(fragment: DocumentCheckListFragmentNew)
    fun inject(dialogFragment: BankDetailDialogFragment)
    fun inject(dialogFragment: ReferenceDetailDialogFragment)
    fun inject(dialogFragment: KycDetailDialog)
    fun inject(fragment : DocumentUploadFragmentNew)
    fun inject(fragment : DocumentFormFragment)

    fun inject(fragment: FragmentPreview)
    /**
     * Presenters
     */
    fun inject(presenter: LoanAppPostPresenter)

    fun inject(presenter: SendOTPPresenter)
    fun inject(presenter: BasePresenter)
    fun inject(presenter: VerifyOTPPresenter)
    fun inject(presenter: LoanAppGetPresenter)
    fun inject(presenter: CoApplicantsPresenter)
    fun inject(presenter: TestPresenter)
    fun inject(presenter: TempSyncPresenter)
    fun inject(presenter: LoginPresenter)
    fun inject(presenter: AddLeadPresenter)
    fun inject(presenter: TransactionCategoryPresenter)
    fun inject(presenter: AllMasterDropdownPresenter)
    fun inject(presenter: SourceChannelPartnerNamePresenter)
    fun inject(presenter: LoanProductPresenter)
    fun inject(presenter: PinCodeDetailPresenter)
    fun inject(presenter: DocumentUploadPresenter)
    fun inject(presenter: GetAllLeadsPresenter)
    fun inject(presenter: StateDropdownPresenter)
    fun inject(presenter: DistrictPresenter)
    fun inject(presenter: CityPresenter)
    fun inject(presenter: LeadSyncPresenter)

    /**
     * View Model
     */
    fun inject(viewModel: TempViewModel)

    fun inject(viewModel: SyncDataViewModel)
    fun inject(viewModel: LeadDataViewModel)
    fun inject(viewModel: AppDataViewModel)

    /**
     * Adapters
     */
    fun inject(adapter: TempRecyclerAdapter)

    /**
     * Holders
     */
    fun inject(other: TempHolder)

    /**
     * Others
     */

    fun inject(other: SuperWorker)

    fun inject(other: UtilWorkersTask)
    fun inject(other: UploadDocumentWorker)
    fun inject(other: UploadLocationWorker)

    fun inject(other: LeadAndLoanDetail)
    fun inject(other: CustomEmploymentInfoView)
    fun inject(other: CustomPersonalInfoView)
    fun inject(other: CustomZipAddressView)
    fun inject(other: CustomChannelPartnerView)
    fun inject(other: LeadMetaData)
    fun inject(other: CustomAssetLiabilityViewInfo)
    fun inject(presenter: Presenter1)
    fun inject(other: CustomDocumentCheckListView)
    fun inject(other: CustomChromeTab)
    fun inject(other: KycFormView)
    fun inject(other: ChannelPartnerViewCreateLead)
    fun inject(other: CustomKycDocumentUploadViewInfo)

    //Service
    fun inject(other: ForegroundLocationTrackerService)

}