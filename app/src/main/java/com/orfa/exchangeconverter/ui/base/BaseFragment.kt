package com.orfa.exchangeconverter.ui.base
import android.icu.util.TimeUnit
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.time.DurationUnit

open class BaseFragment() : Fragment() {

    val pattern = "yyyy-MM-dd HH:mm"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

    }


    fun getCurrentDateTime(): String {


        var dateTime = ""

        try {
            dateTime = SimpleDateFormat(pattern, Locale.getDefault()).format(Date())
        } catch (e: Exception) {
        }
        return dateTime
    }

    fun stringDatetoDate(dateStr: String?): Date {

        if (dateStr.isNullOrBlank())
            return Date()

        return try {
            SimpleDateFormat(pattern).parse(dateStr)
        } catch (e: Exception){
            Date()
        }
    }


}