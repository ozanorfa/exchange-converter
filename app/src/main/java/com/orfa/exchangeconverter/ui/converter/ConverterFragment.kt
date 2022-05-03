package com.orfa.exchangeconverter.ui.converter

import android.R
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.orfa.exchangeconverter.MainActivity
import com.orfa.exchangeconverter.data.ConversionRates
import com.orfa.exchangeconverter.data.getCurrrencyList
import com.orfa.exchangeconverter.databinding.ConverterFragmentBinding
import com.orfa.exchangeconverter.service.CurrencyService
import com.orfa.exchangeconverter.service.MainRepository
import com.orfa.exchangeconverter.ui.base.BaseFragment
import java.text.DecimalFormat
import java.util.*


class ConverterFragment : BaseFragment() {

    private lateinit var viewModel: ConverterViewModel
    private lateinit var binding: ConverterFragmentBinding
    private lateinit var sharedPreferences: SharedPreferences

    private val gson = Gson()
    private val retrofitService = CurrencyService.getInstance()

    var firstCurrency = ""
    var secondCurrency = ""

    var finalAmount: String = "0.0"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactoryConverter(MainRepository(retrofitService))
        )[ConverterViewModel::class.java]
        sharedPreferences =
            requireActivity().applicationContext.getSharedPreferences(TAG, Context.MODE_PRIVATE)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = ConverterFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel

        //Fetch Values
        val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)

        if (isFirstRun) {
            viewModel.getAllValues()
        } else {
            val lastCallDateStr = sharedPreferences.getString("callDate", "")
            val lastCallDate = stringDatetoDate(lastCallDateStr)

            val currentDate = Date()

            val diff: Long = (currentDate.time - lastCallDate.time) / 1000 / 60 / 60

            if (diff >= 24) {
                viewModel.getAllValues()
            } else {
                getValuesFromShared()
            }
        }

        //Spinners
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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
    }

    private fun setObservers() {

        viewModel.currencyRates.observe(viewLifecycleOwner) {

            if (viewModel.isServiceCall.value == true) {

                val json = gson.toJson(it)
                val editor = sharedPreferences.edit()

                editor.putBoolean("isFirstRun", false)
                editor.putString("callDate", getCurrentDateTime())
                editor.putString("conversionObj", json)
                editor.apply()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {

        }

        viewModel.selectedFirstCur.observe(viewLifecycleOwner) {
            firstCurrency = it
        }

        viewModel.selectedSecondCur.observe(viewLifecycleOwner) {
            secondCurrency = it
        }

        binding.etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                val inputStr = p0.toString()
                if (viewModel.currencyRates.value == null || inputStr.isBlank() || inputStr.toDoubleOrNull() == null) {
                    viewModel.finalValue.postValue("")
                    return
                }

                val firstAmount: Double
                val input = inputStr.toDouble()
                val list = viewModel.currencyRates.value

                firstAmount = when (firstCurrency) {
                    "USD" -> input / list!!.USD!!
                    "EUR" -> input / list!!.EUR!!
                    "TRY" -> input / list!!.TRY!!
                    "SEK" -> input / list!!.SEK!!
                    "CAD" -> input / list!!.CAD!!
                    "GBP" -> input / list!!.GBP!!
                    else -> 0.0
                }

                val tempFinalAmount = when (secondCurrency) {
                    "USD" -> firstAmount * list!!.USD!!
                    "EUR" -> firstAmount * list!!.EUR!!
                    "TRY" -> firstAmount * list!!.TRY!!
                    "SEK" -> firstAmount * list!!.SEK!!
                    "CAD" -> firstAmount * list!!.CAD!!
                    "GBP" -> firstAmount * list!!.GBP!!
                    else -> 0.0
                }

                val df = DecimalFormat("#.##")
                finalAmount = df.format(tempFinalAmount).toString()

                viewModel.finalValue.postValue("Final Amount: $finalAmount $secondCurrency")
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        binding.btnConvert.setOnClickListener {
            hideKeyboard(activity as MainActivity)
            binding.etAmount.clearFocus()

            val input = binding.etAmount.text.toString().trim()

            val action = ConverterFragmentDirections.actionConfirmDialog()
            action.firstCurrency = firstCurrency
            action.secondCurrency = secondCurrency
            action.firstValue = input
            action.secondValue = finalAmount

            findNavController().navigate(action)
        }

    }

    private fun getValuesFromShared() {

        viewModel.isServiceCall.postValue(false)

        val json: String? = sharedPreferences.getString("conversionObj", "")
        val obj: ConversionRates = gson.fromJson(json, ConversionRates::class.java)

        viewModel.currencyRates.postValue(obj)
    }

}

class ViewModelFactoryConverter(private val mainRepository: MainRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ConverterViewModel(mainRepository) as T
    }
}