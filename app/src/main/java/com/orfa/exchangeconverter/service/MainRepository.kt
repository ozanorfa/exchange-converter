package com.orfa.exchangeconverter.service

class MainRepository constructor(private val currencyService: CurrencyService) {

    suspend fun getCurrencies(currency: String) = currencyService.getCurrencies(currency)

}