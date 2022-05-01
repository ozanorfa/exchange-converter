package com.orfa.exchangeconverter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    override fun onSupportNavigateUp() =
        Navigation.findNavController(this, R.id.fragment_container_view).navigateUp()

}