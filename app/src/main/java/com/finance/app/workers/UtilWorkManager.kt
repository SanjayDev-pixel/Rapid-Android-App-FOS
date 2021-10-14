package com.finance.app.workers

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.optcrm.optreporting.app.workers.GlobalWorkHandler
import java.util.concurrent.TimeUnit

class UtilWorkManager {

    companion object {
        private const val MainWorkManagerUniqueNamePeridically = "mainWorkManagerUniqueNamePeridically"
        private const val MainWorkManagerUniqueNameOneTime = "mainWorkManagerUniqueNameOneTime"

        private val mWorkManager = WorkManager.getInstance()
        private val workConstraintsGlobal = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        fun globalWorkManagerPeriodically() {

            mWorkManager?.cancelAllWorkByTag(
                MainWorkManagerUniqueNamePeridically)

            val mainWork = PeriodicWorkRequestBuilder<GlobalWorkHandler>(1, TimeUnit.HOURS)
                .setConstraints(
                    workConstraintsGlobal)
                .addTag(
                    MainWorkManagerUniqueNamePeridically)
                .build()

            mWorkManager?.enqueue(mainWork)
        }
    }
}