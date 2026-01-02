package com.example.udhaarpay.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.udhaarpay.R
import com.example.udhaarpay.databinding.FragmentProfileSetupBinding
import com.example.udhaarpay.ui.SharedViewModel
import com.example.udhaarpay.ui.SharedViewModelFactory

class ProfileSetupFragment : Fragment() {

    private var _binding: FragmentProfileSetupBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels {
        SharedViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonDone.setOnClickListener {
            val name = binding.editTextName.text.toString()
            if (name.isNotEmpty()) {
                sharedViewModel.setUserName(name)
                findNavController().navigate(R.id.action_profile_setup_fragment_to_home_fragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}