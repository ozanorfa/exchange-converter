package com.orfa.exchangeconverter.data

import com.google.gson.annotations.SerializedName

class ConversionRates {

    @SerializedName("USD")
    var USD: Double? = null
    @SerializedName("EUR")
    var EUR: Double? = null
    @SerializedName("TRY")
    var TRY: Double? = null
    @SerializedName("SEK")
    var SEK: Double? = null
    @SerializedName("CAD")
    var CAD: Double? = null
    @SerializedName("GBP")
    var GBP: Double? = null
}

fun getCurrencyWithId(id: Int): String {
    return when (id) {
        0 -> "USD"
        1 -> "EUR"
        2 -> "TRY"
        3 -> "SEK"
        4 -> "CAD"
        5 -> "GBP"
        else -> ""
    }
}

fun getCurrrencyList(): List<String> {
    return listOf(
        "USD",
        "EUR",
        "TRY",
        "SEK",
        "CAD",
        "GBP"
    )
}