package com.orfa.exchangeconverter.ui.success

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavHostController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.orfa.exchangeconverter.R
import com.orfa.exchangeconverter.databinding.ConverterFragmentBinding
import com.orfa.exchangeconverter.databinding.SuccessFragmentBinding
import com.orfa.exchangeconverter.generated.callback.OnClickListener
import com.orfa.exchangeconverter.ui.base.BaseFragment
import com.orfa.exchangeconverter.ui.confirmation.ConfirmationDialogFragmentArgs
import com.orfa.exchangeconverter.ui.confirmation.ConfirmationDialogViewModel

class SuccessFragment : BaseFragment() {

    private lateinit var viewModel: SuccessViewModel
    private lateinit var binding: SuccessFragmentBinding
    private val args: SuccessFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[SuccessViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SuccessFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel

        val shownText =
            "${args.firstValue} ${args.firstCurrency} = ${args.secondValue} ${args.secondCurrency}"
        viewModel.title.postValue(shownText)

        binding.btnApprove.setOnClickListener{

            findNavController().setGraph(R.navigation.nav_graph)
        }

        return binding.root
    }


}