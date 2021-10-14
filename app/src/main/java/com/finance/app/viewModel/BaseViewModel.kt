/*
* Copyright (C) 2017 The Munish/BrighterBrain Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.finance.app.viewModel

import android.app.ProgressDialog
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.finance.app.persistence.db.MasterDB
import com.finance.app.presenter.connector.ReusableView
import motobeans.architecture.util.DialogFactory
import motobeans.architecture.util.exShowToast

/**
 * Created by munishkumarthakur on 09/12/17.
 */
open class BaseViewModel(private  val activity: FragmentActivity, private val masterDB: MasterDB? = null) : ViewModel(),
        ReusableView {

    private var progressCounter = 0
    init {
    }

    override fun showToast(msg: String) {
        msg.exShowToast(activity)
    }

    companion object {
        internal var progressDialog: ProgressDialog? = null
    }

    override fun showProgressDialog() {
        when(progressDialog == null) {
            true -> progressDialog = DialogFactory.getInstance(context = activity)
        }
        progressDialog?.show()
    }

    override fun hideProgressDialog() {
        progressDialog?.hide()
    }

    override fun getApiFailure(msg: String) {
        showToast(msg = msg)
    }
}