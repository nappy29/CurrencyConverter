package com.example.paypaycodechallenge.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.work.*
import com.example.paypaycodechallenge.R
import com.example.paypaycodechallenge.databinding.ActivityMainBinding
import com.example.paypaycodechallenge.worker.BackgroundWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var workManager: WorkManager
    private lateinit var periodicRequest: PeriodicWorkRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setUpWorker(this)
    }

    fun setUpWorker(context: Context){
        val constraints = Constraints.Builder().run {
            setRequiredNetworkType(NetworkType.CONNECTED)
            build()
        }

        periodicRequest = PeriodicWorkRequestBuilder<BackgroundWorker>(28, TimeUnit.MINUTES)
            .addTag("Backgroud_TAG")
            .setConstraints(constraints)
            .build()


        workManager = WorkManager.getInstance(context)

        workManager?.enqueueUniquePeriodicWork(
            "Periodic_Fetch_Request",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest!!
        )

    }

}