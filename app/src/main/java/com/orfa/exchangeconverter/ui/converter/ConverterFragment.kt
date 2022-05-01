package com.orfa.exchangeconverter.ui.converter

import android.R
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.orfa.exchangeconverter.MainActivity
import com.orfa.exchangeconverter.data.ConversionRates
import com.orfa.exchangeconverter.data.getCurrencyWithId
import com.orfa.exchangeconverter.data.getCurrrencyList
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

        binding.etAmount.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                TODO("Not yet implemented")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {


            }

            override fun afterTextChanged(p0: Editable?) {
                TODO("Not yet implemented")
            }

        })

        binding.btnConvert.setOnClickListener(View.OnClickListener {
            hideKeyboard(activity as MainActivity)
            binding.etAmount.clearFocus()

            val inputStr  = binding.etAmount.text.toString().trim()
            if (viewModel.currencyRates.value == null || inputStr.isBlank() || inputStr.toIntOrNull() == null)
                return@OnClickListener

            var firstAmounth: Double
            var finalAmounth = 0.0
            val input = inputStr.toInt()
            val firstCurrency  = viewModel.selectedFirstCur
            val secondCurrency = viewModel.selectedSecondCur

            val list = viewModel.currencyRates.value

            firstAmounth = when(firstCurrency) {
                "USD" ->  input / list!!.USD!!
                "EUR" ->  input / list!!.EUR!!
                "TRY" ->  input / list!!.TRY!!
                "SEK" ->  input / list!!.SEK!!
                "CAD" ->  input / list!!.CAD!!
                "GBP" ->  input / list!!.GBP!!
                else  -> 0.0
            }

            finalAmounth = when(secondCurrency) {
                "USD" ->  firstAmounth * list!!.USD!!
                "EUR" ->  firstAmounth * list!!.EUR!!
                "TRY" ->  firstAmounth * list!!.TRY!!
                "SEK" ->  firstAmounth * list!!.SEK!!
                "CAD" ->  firstAmounth * list!!.CAD!!
                "GBP" ->  firstAmounth * list!!.GBP!!
                else  -> 0.0
            }


        })


        val spinner1 = binding.spnFirstCountry
        val spinner2 = binding.spnSecondCountry

        val currencyList = getCurrrencyList()

        spinner1.setOnTouchListener { v, event ->
            hideKeyboard(activity as MainActivity)

            v?.onTouchEvent(event) ?: true
        }
        spinner2.setOnTouchListener { v, event ->
            hideKeyboard(activity as MainActivity)

            v?.onTouchEvent(event) ?: true
        }

        val arrayAdapter =
            activity?.let { ArrayAdapter(it, R.layout.simple_spinner_dropdown_item, currencyList) }

        spinner1.adapter = arrayAdapter
        spinner2.adapter = arrayAdapter

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