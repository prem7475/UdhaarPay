package com.example.udhaarpay.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.udhaarpay.databinding.FragmentAddCreditCardBinding
import com.example.udhaarpay.ui.SharedViewModel
import com.example.udhaarpay.ui.SharedViewModelFactory
import com.google.android.material.snackbar.Snackbar

class AddCreditCardFragment : Fragment() {

    private var _binding: FragmentAddCreditCardBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels {
        SharedViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCreditCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveCardButton.setOnClickListener {
            val cardNumber = binding.cardNumberEditText.text.toString()

            if (cardNumber.isNotEmpty()) {
                sharedViewModel.addCreditCard("**** **** **** ${cardNumber.takeLast(4)}")
                Snackbar.make(view, "Credit Card Added!", Snackbar.LENGTH_LONG).show()
                findNavController().popBackStack()
            } else {
                Snackbar.make(view, "Please enter a card number", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}