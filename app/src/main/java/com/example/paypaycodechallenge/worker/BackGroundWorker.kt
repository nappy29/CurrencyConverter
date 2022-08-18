package com.example.paypaycodechallenge.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.paypaycodechallenge.BuildConfig
import com.example.paypaycodechallenge.data.model.Currency
import com.example.paypaycodechallenge.data.model.Rate
import com.example.paypaycodechallenge.data.repo.RepositoryHelper
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.lang.Exception

@HiltWorker
class BackgroundWorker @AssistedInject constructor(@Assisted appContext: Context, @Assisted workerParams: WorkerParameters,
                                                   private val repository: RepositoryHelper,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        return try {
            val data = repository.getLiveRates(BuildConfig.APIKEY)?.body()?.rates

            val currencies = data?.let { getCurrenciesFromRate(it) }

            if (currencies != null) {
                repository.deleteAllCurrencies()
                repository.insertAll(currencies)
                Log.d("BackgroundWorker", currencies.toString())
            }


            Result.success()
        }catch (e: Exception){
            Log.e("Worker_fail", e.toString())
            Result.failure()
        }
    }

    private fun getCurrenciesFromRate(rate: Rate): List<Currency>{
        var currencyList: MutableList<Currency> = ArrayList()

        var obj = Gson().toJsonTree(rate).asJsonObject
        for((key, value) in obj.entrySet()){
            if(!key.equals("USD", ignoreCase = true)) {
                var currency = Currency(0,key, value.asDouble)
                currencyList.add(currency)
            }
        }
        var currency = Currency(0,"USD", 1.0)
        currencyList.add(0, currency)

        return currencyList
    }
}