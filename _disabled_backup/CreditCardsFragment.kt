package com.example.udhaarpay.ui.creditcards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.udhaarpay.databinding.FragmentCreditCardsBinding
import com.example.udhaarpay.ui.viewmodel.CreditCardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreditCardsFragment : Fragment() {
    
    private val viewModel: CreditCardViewModel by viewModels()
    private var _binding: FragmentCreditCardsBinding? = null
    private val binding get() = _binding!!
    
    private val cardsAdapter = CreditCardsAdapter(
        onSetDefault = { cardId -> viewModel.setDefaultCard(cardId) },
        onDelete = { cardId -> showDeleteConfirmation(cardId) }
    )
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreditCardsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }
    
    private fun setupRecyclerView() {
        binding.rvCreditCards.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cardsAdapter
        }
    }
    
    private fun setupListeners() {
        binding.btnAddCard.setOnClickListener {
            findNavController().navigate(com.example.udhaarpay.R.id.action_to_addCreditCard)
        }
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.creditCards.collect { cards ->
                cardsAdapter.submitList(cards)
                updateEmptyState(cards.isEmpty())
            }
        }
        
        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.isVisible = isLoading
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
        
        lifecycleScope.launch {
            viewModel.error.collect { message ->
                message?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    viewModel.clearMessages()
                }
            }
        }
    }
    
    private fun updateEmptyState(isEmpty: Boolean) {
        binding.emptyStateContainer.isVisible = isEmpty
        binding.rvCreditCards.isVisible = !isEmpty
    }
    
    private fun showDeleteConfirmation(cardId: Long) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Card")
            .setMessage("Are you sure you want to delete this card?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteCard(cardId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
