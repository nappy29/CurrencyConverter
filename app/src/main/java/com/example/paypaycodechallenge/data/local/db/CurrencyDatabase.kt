package com.example.paypaycodechallenge.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.paypaycodechallenge.data.local.dao.CurrencyDao
import com.example.paypaycodechallenge.data.model.Currency

@Database(entities = [Currency::class], version = 1)
abstract class CurrencyDatabase: RoomDatabase() {
    abstract fun getCurrencyDao(): CurrencyDao
}