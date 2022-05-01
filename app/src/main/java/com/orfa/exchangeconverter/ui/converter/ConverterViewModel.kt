package com.orfa.exchangeconverter.ui.converter

import androidx.lifecycle.MutableLiveData
import com.orfa.exchangeconverter.data.ConversionRates
import com.orfa.exchangeconverter.data.CurrencyResponse
import com.orfa.exchangeconverter.models.CurrencyService
import com.orfa.exchangeconverter.models.MainRepository
import com.orfa.exchangeconverter.ui.base.BaseViewModel
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConverterViewModel(private val repository: MainRepository) : BaseViewModel() {

    val currencyRates = MutableLiveData<ConversionRates>()
    val errorMessage = MutableLiveData<String>()
    val isServiceCall = MutableLiveData<Boolean>()
    var job: Job? = null


    init {
        isServiceCall.value = false
    }

    fun getAllValues() {

        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            errorMessage.postValue(throwable.message)
        }

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = repository.getCurrencies("USD")

            if (response.body() != null){
                if (response.body()!!.result == "success"){
                    isServiceCall.postValue(true)
                    currencyRates.postValue(response.body()!!.conversion_rates)
                }
                else {
                    errorMessage.postValue(response.body()!!.errorType)
                }
            }
            else if (response.errorBody() != null) {
                errorMessage.postValue(response.errorBody().toString())
            }
            else{
                errorMessage.postValue("Unknown Error")
            }
        }
    }

    fun getValuesFromShared() {
        isServiceCall.postValue(false)



    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}