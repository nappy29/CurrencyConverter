package com.example.paypaycodechallenge.data.repo

import com.example.paypaycodechallenge.data.model.APIResponse
import com.example.paypaycodechallenge.data.model.Currency
import retrofit2.Response

interface RepositoryHelper {
    suspend fun getLiveRates(apiKey: String): Response<APIResponse>

    suspend fun deleteAllCurrencies()

    suspend fun insertAll(currencyList: List<Currency>)

    suspend fun insert(currency: Currency)

    suspend fun getAllCurrenciesLocally():List<Currency>
}