package com.example.paypaycodechallenge

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.paypaycodechallenge.data.local.dao.CurrencyDao
import com.example.paypaycodechallenge.data.model.Currency
import com.example.paypaycodechallenge.data.remote.ApiService
import com.example.paypaycodechallenge.data.repo.RepositoryHelper
import com.example.paypaycodechallenge.data.repo.RepositoryImpl
import com.example.paypaycodechallenge.ui.MainViewModel
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Response
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class MainViewModeTest {

    private val testDispatcher = TestCoroutineDispatcher()
    lateinit var mainViewModel: MainViewModel
    lateinit var mainRepository: RepositoryHelper

    @Mock
    lateinit var apiService: ApiService

    @Mock
    lateinit var currencyDao: CurrencyDao

    val context  =  Mockito.mock(Context::class.java)


    var c1 = Currency(0, "usd", 1.0)
    var c2 = Currency(0, "gbp", 0.8843)
    var c3 = Currency(0, "xaf", 607.893)

    var currencyList = listOf(c1,c2,c3)

    @get:Rule
    val instantTaskExecutionRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(testDispatcher)
        mainRepository = RepositoryImpl(apiService, currencyDao)
        mainViewModel = MainViewModel(mainRepository,context)
    }

    @Test
    fun getAllCurrencies() {
        runBlocking {
            Mockito.`when`(mainRepository.getAllCurrenciesLocally())
                .thenReturn(currencyList)
            mainViewModel.getRates()
            val result = mainViewModel.localRespone.getOrAwaitValue()
            assertEquals(listOf(Currency(0, "usd", 1.0),
                Currency(0, "gbp", 0.8843),Currency(0, "xaf", 607.893) ), result)
        }
    }

    @Test
    fun `empty currency list test`() {
        runBlocking {
            Mockito.`when`(mainRepository.getAllCurrenciesLocally())
                .thenReturn(emptyList())
            mainViewModel.getRates()
            val result = mainViewModel.localRespone.getOrAwaitValue()
            assertEquals(listOf<Currency>(), result)
        }
    }


    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        afterObserve: () -> Unit = {}
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(o: T?) {
                data = o
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }
        this.observeForever(observer)
        try {
            afterObserve.invoke()
            if (!latch.await(time, timeUnit)) {
                throw TimeoutException("LiveData value was never set.")
            }
        } finally {
            this.removeObserver(observer)
        }
        @Suppress("UNCHECKED_CAST")
        return data as T
    }
}