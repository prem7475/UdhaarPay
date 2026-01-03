package com.example.udhaarpay.ui.tickets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.udhaarpay.data.model.ticketOptions
import com.example.udhaarpay.databinding.FragmentTicketBookingBinding
import com.example.udhaarpay.ui.viewmodel.TicketBookingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TicketBookingFragment : Fragment() {

    private var _binding: FragmentTicketBookingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TicketBookingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTicketBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setUserId(1)
        setupUI()
    }

    private fun setupUI() {
        binding.btnMovies.setOnClickListener {
            val option = ticketOptions.find { it.name == "BookMyShow" }
            option?.let {
                openWebsite(it.website, it.name)
                viewModel.saveBookingRecord(it.category, it.name)
            }
        }

        binding.btnFlights.setOnClickListener {
            val option = ticketOptions.find { it.name == "Skyscanner" }
            option?.let {
                openWebsite(it.website, it.name)
                viewModel.saveBookingRecord(it.category, it.name)
            }
        }

        binding.btnTrains.setOnClickListener {
            val option = ticketOptions.find { it.name == "IRCTC" }
            option?.let {
                openWebsite(it.website, it.name)
                viewModel.saveBookingRecord(it.category, it.name)
            }
        }

        binding.btnBus.setOnClickListener {
            val option = ticketOptions.find { it.name == "RedBus" }
            option?.let {
                openWebsite(it.website, it.name)
                viewModel.saveBookingRecord(it.category, it.name)
            }
        }
    }

    private fun openWebsite(website: String, name: String) {
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(website))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
