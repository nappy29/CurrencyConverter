package com.example.paypaycodechallenge

import com.example.paypaycodechallenge.data.local.dao.CurrencyDao
import com.example.paypaycodechallenge.data.model.APIResponse
import com.example.paypaycodechallenge.data.model.Currency
import com.example.paypaycodechallenge.data.remote.ApiService
import com.example.paypaycodechallenge.data.repo.RepositoryHelper
import com.example.paypaycodechallenge.data.repo.RepositoryImpl
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Response

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class RepositoryTest {

    @Mock
    lateinit var apiService: ApiService

    @Mock
    lateinit var currencyDao: CurrencyDao

    val apiKey  =  "key"

    lateinit var mainRepository: RepositoryHelper

    var c1 = Currency(0, "usd", 1.0)
    var c2 = Currency(0, "gbp", 0.8843)
    var c3 = Currency(0, "xaf", 607.893)

    var currencyList = listOf(c1,c2,c3)

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        mainRepository = RepositoryImpl(apiService, currencyDao)
    }

    @Test
    fun `get currency response api test`() {
        runBlocking {
            Mockito.`when`(apiService.getLiveRates(apiKey)).thenReturn(Response.success(APIResponse("time","usd", null)))
            val response = mainRepository.getLiveRates(apiKey)
            assertEquals(APIResponse("time","usd", null), response.body())
        }

    }

    @Test
    fun `test for different currency api response`() {
        runBlocking {
            Mockito.`when`(apiService.getLiveRates(apiKey)).thenReturn(Response.success(APIResponse("time","usd", null)))
            val response = mainRepository.getLiveRates(apiKey)
            assertNotEquals(APIResponse("time","gbp", null), response.body())
        }

    }

    @Test
    fun `get currency list from db test`() {
        runBlocking {
            Mockito.`when`(currencyDao.getAllCurrenciesLocally()).thenReturn(currencyList)
            val result = mainRepository.getAllCurrenciesLocally()
            assertEquals(listOf(Currency(0, "usd", 1.0),
                Currency(0, "gbp", 0.8843),Currency(0, "xaf", 607.893) ), result)
        }

    }
}