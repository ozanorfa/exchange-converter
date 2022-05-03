package com.orfa.exchangeconverter.ui.converter

import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.MutableLiveData
import com.orfa.exchangeconverter.data.ConversionRates
import com.orfa.exchangeconverter.data.getCurrencyWithId
import com.orfa.exchangeconverter.models.MainRepository
import com.orfa.exchangeconverter.ui.base.BaseViewModel
import kotlinx.coroutines.*

class ConverterViewModel(private val repository: MainRepository) : BaseViewModel() {

    val currencyRates = MutableLiveData<ConversionRates>()
    val errorMessage = MutableLiveData<String>()
    val isServiceCall = MutableLiveData<Boolean>()
    var job: Job? = null

    var selectedFirstCur  = MutableLiveData<String>()
    var selectedSecondCur = MutableLiveData<String>()

    val finalValue = MutableLiveData<String>()

    init {
        isServiceCall.value = false
        selectedFirstCur.value = ""
        selectedSecondCur.value = ""
        finalValue.value = ""
    }


    val spinner1Listener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            selectedFirstCur.postValue(getCurrencyWithId(position))
            finalValue.postValue("")
        }
    }

    val spinner2Listener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            selectedSecondCur.postValue(getCurrencyWithId(position))
            finalValue.postValue("")
        }
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



    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}