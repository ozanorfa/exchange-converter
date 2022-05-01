package com.orfa.exchangeconverter.models

import java.util.*

class MainRepository constructor(private val currencyService: CurrencyService) {

    suspend fun getCurrencies(currency: String) = currencyService.getCurrencies(currency)

}