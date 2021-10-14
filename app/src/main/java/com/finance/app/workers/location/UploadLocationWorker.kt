package com.finance.app.workers.location

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.finance.app.persistence.model.UploadLocationRequest
import motobeans.architecture.application.ArchitectureApp
import motobeans.architecture.development.interfaces.ApiProject
import motobeans.architecture.development.interfaces.DataBaseUtil
import javax.inject.Inject


class UploadLocationWorker(context: Context , workerParams: WorkerParameters) : Worker(context , workerParams) {

    @Inject
    lateinit var database: DataBaseUtil
    @Inject
    lateinit var apiProject: ApiProject

    init {
        ArchitectureApp.instance.component.inject(this)
    }

    override fun doWork(): Result {
        val locationList = database.provideDataBaseSource().locationTrackerDao().get()
        locationList?.let {
            val locationRequestModel = UploadLocationRequest().apply { userLocationHistoryList = it }
            val uploadCall = apiProject.api.postTrackerLocation(locationRequestModel)
            val response = uploadCall.execute()
            System.out.println("Response>>>>"+response)
            if (response.isSuccessful) {
                database.provideDataBaseSource().locationTrackerDao().truncate()
            }
        }

        return Result.success()
    }
}