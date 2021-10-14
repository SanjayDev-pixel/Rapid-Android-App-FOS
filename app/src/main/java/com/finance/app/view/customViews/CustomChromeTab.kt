package com.finance.app.view.customViews

import android.app.Activity
import android.view.Gravity
import com.finance.app.R
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import javax.inject.Inject


class CustomChromeTab {

    @Inject
    lateinit var sharedPreference: SharedPreferencesUtil

    init {
        ArchitectureApp.instance.component.inject(this)
    }

    fun openUrl(activity: Activity, url: String) {
        url?.let {
            showFinestWebView(activity = activity, url = url)
        }
    }

    private fun showFinestWebView(activity: Activity, url: String) {
        val appName = activity.getString(R.string.app_name)
//        FinestWebView.Builder(activity).theme(R.style.FinestWebViewTheme)
//                .titleDefault(appName)
//                .showUrl(false)
//                .statusBarColorRes(R.color.colorPrimaryDark)
//                .toolbarColorRes(R.color.colorPrimary)
//                .titleColorRes(R.color.finestWhite)
//                .urlColorRes(R.color.white)
//                .iconDefaultColorRes(R.color.finestWhite)
//                .progressBarColorRes(R.color.finestWhite)
//                .stringResCopiedToClipboard(R.string.copied_to_clipboard)
//                .stringResCopiedToClipboard(R.string.copied_to_clipboard)
//                .stringResCopiedToClipboard(R.string.copied_to_clipboard)
//                .showSwipeRefreshLayout(true)
//                .swipeRefreshColorRes(R.color.colorPrimaryDark)
//                .menuSelector(R.drawable.selector_light_theme)
//                .menuTextGravity(Gravity.CENTER)
//                .menuTextPaddingRightRes(R.dimen.defaultMenuTextPaddingLeft)
//                .dividerHeight(0)
//                .gradientDivider(false)
//                .showIconMenu(false)
//                .disableIconMenu(true)
//                .showIconBack(false)
//                .showIconForward(false)
//
//                .webViewAllowFileAccessFromFileURLs(true)
//                .webViewJavaScriptEnabled(true)
//                .webViewAllowContentAccess(true)
//                .webViewDomStorageEnabled(true)
//                .webViewAllowFileAccess(true)
//                .webViewDatabaseEnabled(true)
//                .webViewMediaPlaybackRequiresUserGesture(false)
//                .webViewAllowUniversalAccessFromFileURLs(true)
//                .webViewBlockNetworkImage(false)
//                .webViewBlockNetworkLoads(false)
//                .setCustomAnimations(R.anim.slide_up, R.anim.hold, R.anim.hold, R.anim.slide_down)
//                .show(url)
    }
}