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
package com.finance.app.others

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.fragment.app.FragmentActivity
import com.finance.app.persistence.db.MasterDB
import com.finance.app.viewModel.*

/**
 * Factory to wrap each ViewModel with Data access object of Product Entity
 *
 * @constructor dataSource: ProductDao
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val activity: FragmentActivity,
                       private val dataSource: MasterDB) : ViewModelProvider.Factory {

    /**
     * Get DAO of Product Entity
     *
     * @return ProductDao
     */
    fun getDBSource() = dataSource

    /**
     * Wrap DAO of Product Entity with Provided Model class T
     *
     * @param modelClass Model class of Generic meetingType <T>
     * @return T
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(TempViewModel::class.java) -> {
                return TempViewModel(activity, dataSource) as T
            }
            modelClass.isAssignableFrom(SyncDataViewModel::class.java) -> {
                return SyncDataViewModel(activity) as T
            }
            modelClass.isAssignableFrom(AppDataViewModel::class.java) -> {
                return AppDataViewModel(activity, dataSource) as T
            }
            modelClass.isAssignableFrom(LeadDataViewModel::class.java) -> {
                return LeadDataViewModel(activity) as T
            }
            modelClass.isAssignableFrom(TempStaticViewModel::class.java) -> {

                // Static View Model
                val key = "AppValidationViewModel"
                return if(hashMapViewModel.containsKey(key)){
                    getViewModel(key) as T
                } else {
                    addViewModel(key,
                            TempStaticViewModel(activity, dataSource))
                    getViewModel(key) as T
                }
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }

    }

    companion object {
        val hashMapViewModel = HashMap<String, ViewModel>()
        fun addViewModel(key: String, viewModel: ViewModel){
            hashMapViewModel.put(key, viewModel)
        }
        fun getViewModel(key: String): ViewModel? {
            return hashMapViewModel[key]
        }
    }
}
