package com.optcrm.optreporting.app.workers

import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.development.interfaces.ApiProject
import motobeans.architecture.development.interfaces.DataBaseUtil
import motobeans.architecture.development.interfaces.SharedPreferencesUtil
import javax.inject.Inject

open class UtilWorkersTask {

    @Inject
    lateinit var sharedPreferencesUtil: SharedPreferencesUtil
    @Inject
    lateinit var dataBase: DataBaseUtil
    @Inject
    lateinit var api: ApiProject

    init {
        ArchitectureApp.instance.component.inject(this)
    }
}