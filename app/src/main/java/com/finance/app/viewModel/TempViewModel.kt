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

import androidx.lifecycle.MutableLiveData
import androidx.fragment.app.FragmentActivity
import com.finance.app.persistence.db.MasterDB

/**
 * Created by munishkumarthakur on 09/12/17.
 */
class TempViewModel(private val activity: FragmentActivity, private val database: MasterDB)
    : BaseViewModel(activity){


    /** ProductViewState Mutable Live Data */
    val viewState: MutableLiveData<ProductViewState> = MutableLiveData()

    /**
     * Constructor of TempViewModel
     */
    init {
        viewState.value = ProductViewState()
    }


    /**
     * Get Current View State
     *
     * @return LiveData of View Model State
     */
    private fun currentViewState(): ProductViewState = viewState.value!!

    /**
     * This class contains different states of screen such as:
     *
     *   isLoading = api call is running and getting data from network
     *
     *   isError = if any error occurs during network operation
     *
     *   isEmptyData = if no data is available
     */
    data class ProductViewState(var isNoDataFound: Boolean = false,
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val isEmptyData: Boolean = false,
        val errorMessage: String? = null)

    /**
     * Set loading view state
     */
    private fun isLoading(isLoading: Boolean) {
        viewState.value = currentViewState().copy(isLoading = isLoading)
    }

    /**
     * Set Error view state
     */
    private fun isError(isError: Boolean, errorMessage: String?) {
        viewState.value = currentViewState().copy(isError = isError, errorMessage = errorMessage)
    }

    /**
     * Set Empty view state
     */
    private fun isEmptyList(isEmptyData: Boolean) {
        viewState.value = currentViewState().copy(isEmptyData = isEmptyData)
    }

    override fun getApiFailure(msg: String) {
        showToast(msg = msg)
    }
}