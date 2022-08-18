package com.example.paypaycodechallenge.data.local.dao

import androidx.room.*
import com.example.paypaycodechallenge.data.model.Currency

@Dao
interface CurrencyDao {

    @Query("SELECT * FROM currencies")
    suspend fun getAllCurrenciesLocally(): List<Currency>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(currency: Currency)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(currencyList: List<Currency>)


    @Query("DELETE FROM currencies")
    suspend fun deleteAllCurrencies()

}