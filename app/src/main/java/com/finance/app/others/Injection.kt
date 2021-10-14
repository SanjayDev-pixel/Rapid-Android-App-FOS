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

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.finance.app.persistence.db.MasterDB

/**
 * Enables injection of data sources.
 */
object Injection {

    /**
     * Provides Data Access methods of Product table
     *
     * @param context Application Context
     * @return ProductDao
     */
    fun provideDataBaseSource(context: Context): MasterDB {
        return MasterDB.getInstance(context)
    }

    /**
     * Provides ViewModelFactory where each ViewModel will be wrapped with Data access object of Product Entity
     *
     * @param context Application Context
     * @return ViewModelFactory
     */
    fun provideViewModelFactory(activity: FragmentActivity): ViewModelFactory {
        val dataSource = provideDataBaseSource(activity)
        return ViewModelFactory(activity, dataSource)
    }
}
