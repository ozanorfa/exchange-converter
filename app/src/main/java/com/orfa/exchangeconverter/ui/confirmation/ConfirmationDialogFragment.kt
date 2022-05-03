package com.orfa.exchangeconverter.ui.confirmation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.orfa.exchangeconverter.databinding.ConfirmationDialogFragmentBinding


class ConfirmationDialogFragment : DialogFragment() {

    private lateinit var viewModel: ConfirmationDialogViewModel
    private lateinit var binding: ConfirmationDialogFragmentBinding
    private val args: ConfirmationDialogFragmentArgs by navArgs()

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ConfirmationDialogViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ConfirmationDialogFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel

        val popUpText =
            "You are about to get ${args.secondValue} ${args.secondCurrency} for ${args.firstValue} ${args.secondCurrency}. Do you approve?"

        viewModel.title.postValue(popUpText)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
    }

    override fun onResume() {
        super.onResume()
        if (dialog == null) return
        val window = dialog!!.window ?: return
        val width = (resources.displayMetrics.widthPixels * 0.95).toInt()
        val params = window.attributes
        params.width = width
        window.attributes = params
    }

    private fun setObservers() {

        viewModel.isCancelled.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.isCancelled.postValue(false)
                dismissAllowingStateLoss()
            }
        }

        viewModel.navigateNext.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.navigateNext.postValue(false)

                val action = ConfirmationDialogFragmentDirections.actionSuccess()
                action.firstValue = args.firstValue
                action.firstCurrency = args.firstCurrency
                action.secondValue = args.secondValue
                action.secondCurrency = args.secondCurrency

                dismissAllowingStateLoss()
                findNavController().navigate(action)
            }
        }
    }


}