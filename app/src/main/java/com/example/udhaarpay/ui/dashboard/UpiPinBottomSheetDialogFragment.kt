package com.example.udhaarpay.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.udhaarpay.databinding.FragmentUpiPinBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class UpiPinBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentUpiPinBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpiPinBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bankName.text = "Enter UPI PIN for ${arguments?.getString("bankName")}"
        binding.submit.setOnClickListener {
            // In a real app, you would verify the PIN
            parentFragmentManager.setFragmentResult("upiPinResult", Bundle().apply {
                putBoolean("success", true)
            })
            dismiss()
        }
    }

    companion object {
        fun newInstance(bankName: String): UpiPinBottomSheetDialogFragment {
            val fragment = UpiPinBottomSheetDialogFragment()
            fragment.arguments = Bundle().apply {
                putString("bankName", bankName)
            }
            return fragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}