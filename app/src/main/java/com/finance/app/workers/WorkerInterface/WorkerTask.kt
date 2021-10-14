package com.example.munishkumarthakur.workmanager_testing.workers.WorkerInterface

interface WorkerTask {
    interface worker {
        fun execute()
        fun handleLocalToServerSync()
        fun handleServerToLocalSync()
    }

    interface callback {
        fun isSuccess(isSuccess: Boolean)
    }

}