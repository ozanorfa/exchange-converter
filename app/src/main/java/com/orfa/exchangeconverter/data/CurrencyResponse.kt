package com.orfa.exchangeconverter.data

import com.google.gson.annotations.SerializedName

data class CurrencyResponse (
    @SerializedName("result") val result: String,
    @SerializedName("error-type") val errorType: String,
    @SerializedName("conversion_rates") val conversion_rates: ConversionRates,
)