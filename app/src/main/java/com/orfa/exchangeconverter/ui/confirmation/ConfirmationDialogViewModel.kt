package com.orfa.exchangeconverter.ui.confirmation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ConfirmationDialogViewModel : ViewModel() {
    val isCancelled = MutableLiveData<Boolean>()
    val navigateNext = MutableLiveData<Boolean>()
    val title = MutableLiveData<String>()

    fun onSubmit() {
        navigateNext.postValue(true)
    }

    fun onCancel() {
        isCancelled.postValue(true)
    }
}