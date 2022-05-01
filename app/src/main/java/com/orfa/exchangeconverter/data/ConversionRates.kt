package com.orfa.exchangeconverter.data

import com.google.gson.annotations.SerializedName

class ConversionRates {

    @SerializedName("USD" ) var USD : Double? = null
    @SerializedName("EUR" ) var EUR : Double? = null
    @SerializedName("TRY" ) var TRY : Double? = null
    @SerializedName("SEK" ) var SEK : Double? = null
    @SerializedName("CAD" ) var CAD : Double? = null
    @SerializedName("GBP" ) var GBP : Double? = null

}