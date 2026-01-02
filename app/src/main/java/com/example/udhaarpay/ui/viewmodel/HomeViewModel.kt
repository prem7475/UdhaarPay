package com.example.udhaarpay.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udhaarpay.data.model.BankCard
import com.example.udhaarpay.data.model.User
import com.example.udhaarpay.data.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val cardRepository: CardRepository,
    private val bankRepository: BankRepository,
    private val transactionRepository: TransactionRepository,
    private val offerRepository: OfferRepository,
    private val serviceRepository: ServiceRepository,
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _cards = MutableStateFlow<List<BankCard>>(emptyList())
    val cards: StateFlow<List<BankCard>> = _cards.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun clearError() {
        _error.value = null
    }

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // Load current user
                val user = authRepository.getCurrentUser()
                _currentUser.value = user
                
                // Load user's cards
                if (user != null) {
                    // Use numeric DB primary key `userId` for DAO queries
                    cardRepository.getCardsByUser(user.userId).collect { cardList ->
                        _cards.value = cardList
                    }
                }
                
                _error.value = null
            } catch (e: Exception) {
                Timber.e(e, "Error loading home data")
                _error.value = e.message ?: "Failed to load data"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshData() {
        loadUserData()
    }
}

@HiltViewModel
class CardViewModel @Inject constructor(
    private val cardRepository: CardRepository
) : ViewModel() {

    private val _selectedCard = MutableStateFlow<BankCard?>(null)
    val selectedCard: StateFlow<BankCard?> = _selectedCard.asStateFlow()

    private val _balance = MutableStateFlow(0.0)
    val balance: StateFlow<Double> = _balance.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun selectCard(card: BankCard) {
        _selectedCard.value = card
    }

    fun fetchCardBalance(cardId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = cardRepository.getCardBalance(cardId)
            result.onSuccess { balance ->
                _balance.value = balance
            }.onFailure { e ->
                Timber.e(e, "Error fetching balance")
            }
            _isLoading.value = false
        }
    }
}

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val cardRepository: CardRepository,
    private val bankRepository: BankRepository
) : ViewModel() {

    private val _cards = MutableStateFlow<List<BankCard>>(emptyList())
    val cards: StateFlow<List<BankCard>> = _cards.asStateFlow()

    private val _bankAccounts = MutableStateFlow<List<com.example.udhaarpay.data.model.BankAccount>>(emptyList())
    val bankAccounts: StateFlow<List<com.example.udhaarpay.data.model.BankAccount>> = _bankAccounts.asStateFlow()

    fun loadCards(userId: Int) {
        viewModelScope.launch {
            cardRepository.getCardsByUser(userId).collect { cardList ->
                _cards.value = cardList
            }
        }
    }

    fun loadBankAccounts(userId: Int) {
        viewModelScope.launch {
            bankRepository.getBankAccounts(userId).collect { accounts ->
                _bankAccounts.value = accounts
            }
        }
    }
}



@HiltViewModel
class OfferViewModel @Inject constructor(
    private val offerRepository: OfferRepository
) : ViewModel() {

    private val _allOffers = MutableStateFlow<List<com.example.udhaarpay.data.model.Offer>>(emptyList())
    val allOffers: StateFlow<List<com.example.udhaarpay.data.model.Offer>> = _allOffers.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val result = offerRepository.getOfferCategories()
            result.onSuccess { cats ->
                _categories.value = listOf("All") + cats
            }
        }
    }

    fun loadOffersByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            offerRepository.getOffersByCategory(category).collect { offers ->
                _allOffers.value = offers
            }
            _isLoading.value = false
        }
    }

    fun loadAllOffers() {
        viewModelScope.launch {
            _isLoading.value = true
            offerRepository.getAllOffers().collect { offers ->
                _allOffers.value = offers
            }
            _isLoading.value = false
        }
    }
}

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    private val _services = MutableStateFlow<List<com.example.udhaarpay.data.model.Service>>(emptyList())
    val services: StateFlow<List<com.example.udhaarpay.data.model.Service>> = _services.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun clearError() {
        _error.value = null
    }

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val result = serviceRepository.getServiceCategories()
            result.onSuccess { cats ->
                _categories.value = cats
            }
        }
    }

    fun loadServicesByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            serviceRepository.getServicesByCategory(category).collect { services ->
                _services.value = services
            }
            _isLoading.value = false
        }
    }

    fun loadAllServices() {
        viewModelScope.launch {
            _isLoading.value = true
            serviceRepository.getAllActiveServices().collect { services ->
                _services.value = services
            }
            _isLoading.value = false
        }
    }
}

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {

    private val _categoryStats = MutableStateFlow<List<com.example.udhaarpay.data.model.TransactionCategory>>(emptyList())
    val categoryStats: StateFlow<List<com.example.udhaarpay.data.model.TransactionCategory>> = _categoryStats.asStateFlow()

    private val _monthlyAnalytics = MutableStateFlow<List<com.example.udhaarpay.data.model.SpendingAnalytics>>(emptyList())
    val monthlyAnalytics: StateFlow<List<com.example.udhaarpay.data.model.SpendingAnalytics>> = _monthlyAnalytics.asStateFlow()

    fun loadCategoryStats(userId: Int) {
        viewModelScope.launch {
            analyticsRepository.getCategoryStats(userId).collect { stats ->
                _categoryStats.value = stats
            }
        }
    }

    fun loadMonthlyAnalytics(userId: Int) {
        viewModelScope.launch {
            analyticsRepository.getLast12MonthsAnalytics(userId).collect { analytics ->
                _monthlyAnalytics.value = analytics
            }
        }
    }
}
