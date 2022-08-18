package com.example.paypaycodechallenge.ui

import android.util.Log
import androidx.lifecycle.*
import com.example.paypaycodechallenge.data.model.Currency
import com.example.paypaycodechallenge.data.repo.RepositoryHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(private val repositoryHelper: RepositoryHelper): ViewModel(){

    private val _localResponse = MutableLiveData<List<Currency>>()

    val localRespone: LiveData<List<Currency>>
        get() = _localResponse

    init {
        getRates()
        repeatFun()
    }

    fun getRates(){
        viewModelScope.launch {
            var currencies = repositoryHelper.getAllCurrenciesLocally()
            _localResponse.postValue(currencies)
            Log.d("MainViewModel", currencies.toString())
        }
    }

    private fun getRatesHelper() = viewModelScope.launch {
        delay(7000)
        getRates()
    }

    // run every 29 mins
    private fun repeatFun(): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            while(isActive) {
                withContext(Dispatchers.IO + NonCancellable) {

                    getRatesHelper()
                    delay(1740000)
                }

            }
        }
    }

}