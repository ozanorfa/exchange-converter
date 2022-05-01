package com.orfa.exchangeconverter.ui.converter

import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.orfa.exchangeconverter.MainActivity
import com.orfa.exchangeconverter.data.ConversionRates
import com.orfa.exchangeconverter.databinding.ConverterFragmentBinding
import com.orfa.exchangeconverter.models.CurrencyService
import com.orfa.exchangeconverter.models.MainRepository
import com.orfa.exchangeconverter.ui.base.BaseFragment
import java.util.*


class ConverterFragment : BaseFragment() {

    private lateinit var viewModel: ConverterViewModel
    private lateinit var binding: ConverterFragmentBinding
    private lateinit var sharedPreferences : SharedPreferences

    private val gson = Gson()
    private val retrofitService = CurrencyService.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, ViewModelFactoryConverter(MainRepository(retrofitService)))[ConverterViewModel::class.java]
        sharedPreferences = requireActivity().applicationContext.getSharedPreferences(TAG,   Context.MODE_PRIVATE)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = ConverterFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel

        viewModelEvents()


        val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)

        if (isFirstRun){
            viewModel.getAllValues()
        }
        else {
            val lastCallDateStr = sharedPreferences.getString("callDate","")
            val lastCallDate = stringDatetoDate(lastCallDateStr)

            val currentDate = Date()

            val diff: Long = (currentDate.time - lastCallDate.time) / 1000 / 60 / 60

            if (diff >= 24){
                viewModel.getAllValues()
            }
            else {
               getValuesFromShared()
            }
        }




        return binding.root
    }

    private fun viewModelEvents() {

        viewModel.currencyRates.observe(viewLifecycleOwner, Observer {

            if (viewModel.isServiceCall.value == true){

                val json = gson.toJson(it)
                val editor = sharedPreferences.edit()

                editor.putBoolean("isFirstRun", false)
                editor.putString("callDate", getCurrentDateTime())
                editor.putString("conversionObj", json)
                editor.apply()
            }
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer {

        })

        binding.btnConvert.setOnClickListener(View.OnClickListener {
            hideKeyboard(activity as MainActivity)
            binding.etAmount.clearFocus()
        })


        val spinner1 = binding.spnFirstCountry
        val spinner2 = binding.spnSecondCountry

        spinner1.setOnClickListener {
            hideKeyboard(activity as MainActivity)
        }
        spinner2.setOnClickListener {
            hideKeyboard(activity as MainActivity)
        }



    }

    private fun getValuesFromShared() {

        viewModel.isServiceCall.postValue(false)

        val json: String? = sharedPreferences.getString("conversionObj", "")
        val obj: ConversionRates = gson.fromJson(json, ConversionRates::class.java)
        viewModel.currencyRates.postValue(obj)
    }

}

class ViewModelFactoryConverter(private val mainRepository: MainRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ConverterViewModel(mainRepository) as T
    }
}