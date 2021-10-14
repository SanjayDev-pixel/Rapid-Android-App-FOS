package com.optcrm.optreporting.app.workers

import android.content.Context
import androidx.work.WorkerParameters
import com.example.munishkumarthakur.workmanager_testing.workers.WorkerInterface.WorkerTask
import motobeans.architecture.customAppComponents.jetpack.SuperWorker

class GlobalWorkHandler(context : Context, params : WorkerParameters) : SuperWorker(context, params) {

    override fun doWork(): Result {

        println("Worker Running - initWorker Order (START) ")

        val arrayOfWorkers = arrayListOf<WorkerTask.worker>(UtilWorkersTaskLeadSync())

        for (workerTask in arrayOfWorkers) {
            workerTask.execute()
        }

        println("Worker Running - initWorker Order (SUCCESS RETURN) ")
        return Result.success()
    }
}