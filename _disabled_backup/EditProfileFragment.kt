package com.example.udhaarpay.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.udhaarpay.data.AppDatabase
import com.example.udhaarpay.data.repository.BankAccountRepository
import com.example.udhaarpay.data.UserRepository
import com.example.udhaarpay.databinding.FragmentEditProfileBinding
import com.example.udhaarpay.ui.SharedViewModel
import com.example.udhaarpay.ui.SharedViewModelFactory
import com.google.android.material.snackbar.Snackbar

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels {
        SharedViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.userName.observe(viewLifecycleOwner) { name ->
            binding.nameEditText.setText(name)
        }
        sharedViewModel.userEmail.observe(viewLifecycleOwner) { email ->
            binding.emailEditText.setText(email)
        }

        binding.saveProfileButton.setOnClickListener {
            val newName = binding.nameEditText.text.toString()
            val newEmail = binding.emailEditText.text.toString()
            if (newName.isNotEmpty()) {
                sharedViewModel.setUserName(newName)
                sharedViewModel.setUserEmail(newEmail)
                Snackbar.make(view, "Profile Updated!", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}