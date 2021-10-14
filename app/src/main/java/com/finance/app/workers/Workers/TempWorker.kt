package com.optcrm.optreporting.app.workers

import android.content.Context
import androidx.work.WorkerParameters
import motobeans.architecture.customAppComponents.jetpack.SuperWorker

class TempWorker(context : Context, params : WorkerParameters) : SuperWorker(context, params) {

    override fun doWork(): Result {

        UtilWorkersTaskTemp().execute()

        return Result.success()
    }
}