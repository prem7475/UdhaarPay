package com.example.udhaarpay.ui.accounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.udhaarpay.databinding.FragmentAddCreditCardBinding
import com.example.udhaarpay.ui.SharedViewModel
import com.google.android.material.snackbar.Snackbar

class AddCreditCardFragment : Fragment() {

    private var _binding: FragmentAddCreditCardBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

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
            val cardHolderName = binding.cardHolderNameEditText.text.toString()
            val expiryDate = binding.expiryDateEditText.text.toString()
            val cvv = binding.cvvEditText.text.toString()

            if (cardNumber.isNotEmpty() && cardHolderName.isNotEmpty() && expiryDate.isNotEmpty() && cvv.isNotEmpty()) {
                // In a real app, you would encrypt and save this securely.
                // Here, we'll just add the card number to our ViewModel for display.
                sharedViewModel.addCreditCard("**** **** **** ${cardNumber.takeLast(4)}")
                Snackbar.make(view, "Credit Card Added!", Snackbar.LENGTH_LONG).show()
                findNavController().navigateUp()
            } else {
                Snackbar.make(view, "Please fill all fields", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}