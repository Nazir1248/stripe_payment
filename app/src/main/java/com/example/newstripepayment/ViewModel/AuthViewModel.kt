package com.example.newstripepayment.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    private val _paymentAmount = MutableStateFlow("")
    val paymentAmount: StateFlow<String> = _paymentAmount

    fun selectEmail(email: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                selectedEmail = email,
                isAuthenticated = true
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                selectedEmail = "",
                isAuthenticated = false
            )
            _paymentAmount.value = ""
        }
    }

    fun setPaymentAmount(amount: String) {
        viewModelScope.launch {
            _paymentAmount.value = amount
        }
    }
}

data class AuthUiState(
    val availableEmails: List<String> = listOf(
        "user1@example.com",
        "user2@example.com",
        "user3@example.com",
        "user4@example.com",
        "user5@example.com"
    ),
    val selectedEmail: String = "",
    val isAuthenticated: Boolean = false
)