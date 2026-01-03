package com.example.udhaarpay.ui.nfc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.udhaarpay.R
import com.example.udhaarpay.data.model.RuPayCard
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NFCPaymentFragment : Fragment() {

    private val viewModel: NFCPaymentViewModel by viewModels()
    private lateinit var rvCardStack: RecyclerView
    private lateinit var cardAdapter: CardStackAdapter
    private var selectedCard: RuPayCard? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_nfc_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupCardStack(view)
        setupNFCPayment(view)
        observeViewModel()
    }

    private fun setupCardStack(view: View) {
        rvCardStack = view.findViewById(R.id.rvCardStack)
        cardAdapter = CardStackAdapter { card ->
            selectedCard = card
            showCardSelected(card)
        }
        rvCardStack.adapter = cardAdapter

        // Setup card snap helper for smooth scrolling
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(rvCardStack)
    }

    private fun setupNFCPayment(view: View) {
        view.findViewById<MaterialButton>(R.id.btnTapToPay).setOnClickListener {
            if (selectedCard != null) {
                simulateNFCPayment()
            } else {
                showMessage("Please select a card first")
            }
        }

        view.findViewById<MaterialButton>(R.id.btnPaymentHistory).setOnClickListener {
            showPaymentHistory()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userCards.collect { cards ->
                cardAdapter.submitList(cards)
                if (cards.isNotEmpty()) {
                    selectedCard = cards.first()
                }
            }
        }
    }

    private fun showCardSelected(card: RuPayCard) {
        val message = "Selected: ${card.cardHolderName}\nAvailable: ₹${card.availableBalance}"
        showMessage(message)
    }

    private fun simulateNFCPayment() {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_nfc_payment, null)
        val etMerchant = view.findViewById<TextInputEditText>(R.id.etMerchantName)
        val etAmount = view.findViewById<TextInputEditText>(R.id.etAmount)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("NFC Payment")
            .setView(view)
            .setPositiveButton("Pay") { _, _ ->
                val merchant = etMerchant.text.toString()
                val amount = etAmount.text.toString().toDoubleOrNull() ?: 0.0

                if (merchant.isNotBlank() && amount > 0 && selectedCard != null) {
                    viewModel.processNFCPayment(
                        selectedCard!!.id,
                        merchant,
                        amount
                    )
                    showMessage("✅ Payment Successful!")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showPaymentHistory() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.nfcTransactions.collect { transactions ->
                if (transactions.isEmpty()) {
                    showMessage("No NFC transactions yet")
                } else {
                    val message = buildString {
                        append("Recent NFC Transactions:\n\n")
                        transactions.take(5).forEach { txn ->
                            append("${txn.merchantName}: ₹${txn.amount}\n")
                        }
                    }
                    showMessage(message)
                }
            }
        }
    }

    private fun showMessage(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Info")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    inner class CardStackAdapter(
        private val onCardClick: (RuPayCard) -> Unit
    ) : RecyclerView.Adapter<CardStackAdapter.CardViewHolder>() {

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
                itemView.setOnClickListener { onCardClick(card) }
            }
        }
    }
}
