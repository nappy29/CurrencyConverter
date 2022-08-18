package com.example.paypaycodechallenge.data.repo

import com.example.paypaycodechallenge.data.local.dao.CurrencyDao
import com.example.paypaycodechallenge.data.model.APIResponse
import com.example.paypaycodechallenge.data.model.Currency
import com.example.paypaycodechallenge.data.remote.ApiService
import retrofit2.Response
import javax.inject.Inject

class RepositoryImpl @Inject constructor(private val apiService: ApiService, private val currencyDao: CurrencyDao): RepositoryHelper {

    override suspend fun getLiveRates(apiKey: String): Response<APIResponse> = apiService.getLiveRates(apiKey)

    override suspend fun deleteAllCurrencies() {
        currencyDao.deleteAllCurrencies()
    }

    override suspend fun insertAll(currencyList: List<Currency>) {
        currencyDao.insertAll(currencyList)
    }

    override suspend fun insert(currency: Currency) {
        currencyDao.insert(currency)
    }

    override suspend fun getAllCurrenciesLocally(): List<Currency> {
        return currencyDao.getAllCurrenciesLocally()
    }

}