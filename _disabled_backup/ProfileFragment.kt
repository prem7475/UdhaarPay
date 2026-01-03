package com.example.udhaarpay.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.udhaarpay.databinding.FragmentProfileBinding
import com.example.udhaarpay.ui.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadUserProfile(1)
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.btnEdit.setOnClickListener {
            showEditDialog()
        }

        binding.btnChangePhoto.setOnClickListener {
            // In a real app, open image picker
            Toast.makeText(requireContext(), "Photo picker would open here", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showEditDialog() {
        val etName = com.google.android.material.textfield.TextInputEditText(requireContext()).apply {
            hint = "Full Name"
            setText(binding.tvName.text)
        }

        val etPhone = com.google.android.material.textfield.TextInputEditText(requireContext()).apply {
            hint = "Phone Number"
            setText(binding.tvPhone.text)
        }

        val etEmail = com.google.android.material.textfield.TextInputEditText(requireContext()).apply {
            hint = "Email"
            setText(binding.tvEmail.text)
        }

        val container = android.widget.LinearLayout(requireContext()).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            layoutParams = android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
            )
            addView(etName)
            addView(etPhone)
            addView(etEmail)
        }

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Edit Profile")
            .setView(container)
            .setPositiveButton("Save") { _, _ ->
                val name = etName.text.toString().trim()
                val phone = etPhone.text.toString().trim()
                val email = etEmail.text.toString().trim()

                if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                    Toast.makeText(requireContext(), "Fill all fields", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                viewModel.updateUserProfile(1, name, phone, email)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.user.collect { user ->
                user?.let {
                    binding.tvName.text = it.name
                    binding.tvPhone.text = it.phoneNumber
                    binding.tvEmail.text = it.email ?: "Not set"
                    binding.tvBalance.text = "Bank: ₹${it.bankBalance}\nWallet: ₹${it.walletBalance}"
                }
            }
        }

        lifecycleScope.launch {
            viewModel.success.collect { message ->
                message?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    viewModel.clearMessages()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
