package com.example.udhaarpay.ui.rupay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.udhaarpay.R
import com.example.udhaarpay.data.model.RuPayCard
import com.example.udhaarpay.data.model.RuPayCardType
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RuPayCardFragment : Fragment() {

    private val viewModel: RuPayCardViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RuPayCardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rupay_cards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI(view)
        observeViewModel()
    }

    private fun setupUI(view: View) {
        recyclerView = view.findViewById(R.id.rvCards)
        adapter = RuPayCardAdapter { card ->
            setDefaultCard(card)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        view.findViewById<View>(R.id.btnAddCard).setOnClickListener {
            showAddCardDialog()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cards.collect { cards ->
                adapter.submitList(cards)
            }
        }
    }

    private fun showAddCardDialog() {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_rupay_card, null)
        
        val etCardNumber = view.findViewById<TextInputEditText>(R.id.etCardNumber)
        val etCardHolder = view.findViewById<TextInputEditText>(R.id.etCardHolder)
        val etExpiry = view.findViewById<TextInputEditText>(R.id.etExpiry)
        val etCvv = view.findViewById<TextInputEditText>(R.id.etCvv)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add RuPay Card")
            .setView(view)
            .setPositiveButton("Add") { _, _ ->
                val cardNumber = etCardNumber.text.toString()
                val holder = etCardHolder.text.toString()
                val expiry = etExpiry.text.toString()
                val cvv = etCvv.text.toString()

                if (cardNumber.isNotBlank() && holder.isNotBlank() && expiry.isNotBlank() && cvv.isNotBlank()) {
                    viewModel.addRuPayCard(
                        cardNumber = cardNumber,
                        cardHolderName = holder,
                        expiryDate = expiry,
                        cvv = cvv,
                        cardType = RuPayCardType.STANDARD,
                        creditLimit = 100000.0
                    )
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setDefaultCard(card: RuPayCard) {
        viewModel.setDefaultCard(card.id)
    }

    inner class RuPayCardAdapter(
        private val onCardClick: (RuPayCard) -> Unit
    ) : RecyclerView.Adapter<RuPayCardAdapter.CardViewHolder>() {

        private var cards: List<RuPayCard> = emptyList()

        fun submitList(newList: List<RuPayCard>) {
            cards = newList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_rupay_card, parent, false)
            return CardViewHolder(view)
        }

        override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
            holder.bind(cards[position])
        }

        override fun getItemCount() = cards.size

        inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(card: RuPayCard) {
                itemView.apply {
                    setOnClickListener { onCardClick(card) }
                    // Bind card details
                }
            }
        }
    }
}
