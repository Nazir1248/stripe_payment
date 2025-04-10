//
//
//package com.example.newstripepayment
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.lifecycleScope
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.newstripepayment.Utils.PUBLISHABLE_KEY
//import com.example.newstripepayment.api.ApiUtilities
//import com.stripe.android.PaymentConfiguration
//import com.stripe.android.paymentsheet.PaymentSheet
//import com.stripe.android.paymentsheet.PaymentSheetResult
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import java.text.SimpleDateFormat
//import java.util.*
//
//
//class MainActivity : ComponentActivity() {
//
//    private lateinit var paymentSheet: PaymentSheet
//    private var customerId by mutableStateOf("")
//    private var ephemeralKey by mutableStateOf("")
//    private var clientSecret by mutableStateOf("")
//    private var paymentCurrency: String = "eur"
//    private var showSuccess by mutableStateOf(false)
//    private var paidAmount by mutableStateOf(0.0)
//    private var isLoading by mutableStateOf(false)
//
//    private val apiInterface = ApiUtilities.getApiInterface()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        PaymentConfiguration.init(this, PUBLISHABLE_KEY)
//        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
//
//        setContent {
//            val authViewModel: AuthViewModel = viewModel()
//            val uiState by authViewModel.uiState.collectAsState()
//
//            if (isLoading) {
//                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                    CircularProgressIndicator()
//                }
//            } else if (showSuccess) {
//                PaymentSuccessScreen(
//                    amount = paidAmount,
//                    authViewModel = authViewModel,
//                    onBackToPayment = {
//                        showSuccess = false
//                        authViewModel.setPaymentAmount("")
//                    }
//                )
//            } else if (uiState.isAuthenticated) {
//                PaymentFlowScreen(
//                    authViewModel = authViewModel,
//                    onPaymentInitiated = {
//                        if (customerId.isNotEmpty() && ephemeralKey.isNotEmpty()) {
//                            initiatePayment(authViewModel.paymentAmount.value)
//                        } else {
//                            showToast("Payment not ready yet. Please wait...")
//                            // Try to get customer ID and ephemeral key again
//                            getCustomerId(authViewModel.uiState.value.selectedEmail)
//                        }
//                    }
//                )
//            } else {
//                AuthScreen(
//                    viewModel = authViewModel,
//                    onAuthenticated = { email ->
//                        getCustomerId(email)
//                    }
//                )
//            }
//        }
//    }
//
//    private fun initiatePayment(amount: String) {
//        try {
//            val amountInCents = (amount.toDouble() * 100).toInt()
//            if (amountInCents < 50) { // Minimum amount check (50 cents)
//                showToast("Amount must be at least €0.50")
//                return
//            }
//            paidAmount = amount.toDouble()
//            isLoading = true
//
//            lifecycleScope.launch(Dispatchers.IO) {
//                try {
//                    val response = apiInterface.getPaymentIntent(
//                        customer = customerId,
//                        amount = amountInCents.toString(),
//                        currency = paymentCurrency
//                    )
//                    withContext(Dispatchers.Main) {
//                        isLoading = false
//                        if (response.isSuccessful && response.body() != null) {
//                            clientSecret = response.body()!!.client_secret
//                            if (clientSecret.isNotEmpty()) {
//                                paymentFlow()
//                            } else {
//                                showError("Empty client secret received")
//                            }
//                        } else {
//                            showError("Failed to create payment intent: ${response.message()}")
//                        }
//                    }
//                } catch (e: Exception) {
//                    withContext(Dispatchers.Main) {
//                        isLoading = false
//                        showError("Payment intent error: ${e.localizedMessage}")
//                    }
//                }
//            }
//        } catch (e: NumberFormatException) {
//            showToast("Please enter a valid amount")
//        }
//    }
//
//    private fun paymentFlow() {
//        paymentSheet.presentWithPaymentIntent(
//            clientSecret,
//            PaymentSheet.Configuration(
//                merchantDisplayName = "My Store",
//                customer = PaymentSheet.CustomerConfiguration(customerId, ephemeralKey)
//            )
//        )
//    }
//
//
//    private fun getCustomerId(email: String) {
//        isLoading = true
//        lifecycleScope.launch(Dispatchers.IO) {
//            try {
//                val response = apiInterface.getCustomer(email)
//                withContext(Dispatchers.Main) {
//                    if (response.isSuccessful && response.body() != null) {
//                        customerId = response.body()!!.id
//                        getEphemeralKey()
//                    } else {
//                        isLoading = false
//                        showError("Failed to create customer: ${response.message()}")
//                    }
//                }
//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    isLoading = false
//                    showError("Customer error: ${e.localizedMessage}")
//                }
//            }
//        }
//    }
//
//    private fun getEphemeralKey() {
//        lifecycleScope.launch(Dispatchers.IO) {
//            try {
//                val response = apiInterface.getEphemeralKey(customerId)
//                withContext(Dispatchers.Main) {
//                    isLoading = false
//                    if (response.isSuccessful && response.body() != null) {
//                        ephemeralKey = response.body()!!.secret
//                    } else {
//                        showError("Failed to get ephemeral key: ${response.message()}")
//                    }
//                }
//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    isLoading = false
//                    showError("Ephemeral key error: ${e.localizedMessage}")
//                }
//            }
//        }
//    }
//    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
//        when (paymentSheetResult) {
//            is PaymentSheetResult.Completed -> {
//                showSuccess = true
//            }
//            is PaymentSheetResult.Canceled -> {
//                showToast("Payment Canceled")
//            }
//            is PaymentSheetResult.Failed -> {
//                showToast("Payment Failed: ${paymentSheetResult.error.message}")
//            }
//        }
//    }
//
//    private fun showError(message: String) {
//        showToast(message)
//    }
//
//    private fun showToast(message: String) {
//        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_LONG).show()
//    }
//}
//
//
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AuthScreen(
//    viewModel: AuthViewModel,
//    onAuthenticated: (String) -> Unit  // Now accepts the email as parameter
//) {
//    val uiState by viewModel.uiState.collectAsState()
//
//    if (uiState.isAuthenticated) {
//        onAuthenticated(uiState.selectedEmail)  // Pass the selected email
//    } else {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(32.dp),
//            verticalArrangement = Arrangement.Center
//        ) {
//            var expanded by remember { mutableStateOf(false) }
//            var selectedEmail by remember { mutableStateOf("") }
//
//            Text("Select your email:", style = MaterialTheme.typography.titleMedium)
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            ExposedDropdownMenuBox(
//                expanded = expanded,
//                onExpandedChange = { expanded = !expanded }
//            ) {
//                OutlinedTextField(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .menuAnchor(),
//                    readOnly = true,
//                    value = selectedEmail,
//                    onValueChange = {},
//                    label = { Text("Email") },
//                    trailingIcon = {
//                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
//                    },
//                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
//                )
//
//                ExposedDropdownMenu(
//                    expanded = expanded,
//                    onDismissRequest = { expanded = false }
//                ) {
//                    uiState.availableEmails.forEach { email ->
//                        DropdownMenuItem(
//                            text = { Text(email) },
//                            onClick = {
//                                selectedEmail = email
//                                expanded = false
//                                viewModel.selectEmail(email)
//                            }
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun PaymentFlowScreen(
//    authViewModel: AuthViewModel,
//    onPaymentInitiated: () -> Unit
//) {
//    var paymentAmount by remember { mutableStateOf("") }
//    val uiState by authViewModel.uiState.collectAsState()
//    val email = uiState.selectedEmail
//
//    LaunchedEffect(paymentAmount) {
//        authViewModel.setPaymentAmount(paymentAmount)
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(32.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = "Logged in as: $email",
//            style = MaterialTheme.typography.bodyMedium,
//            modifier = Modifier.padding(bottom = 16.dp)
//        )
//
//        OutlinedTextField(
//            value = paymentAmount,
//            onValueChange = { paymentAmount = it },
//            label = { Text("Enter Amount") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        Button(
//            onClick = onPaymentInitiated,
//            modifier = Modifier
//                .height(50.dp)
//                .fillMaxWidth(),
//            enabled = paymentAmount.isNotEmpty()
//        ) {
//            Text("Pay Now")
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        TextButton(
//            onClick = { authViewModel.logout() },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Logout")
//        }
//    }
//}
//
//@Composable
//fun PaymentSuccessScreen(
//    amount: Double,
//    authViewModel: AuthViewModel,
//    onBackToPayment: () -> Unit
//) {
//    val dateFormat = SimpleDateFormat("MMM d, yyyy, h:mm a", Locale.getDefault())
//    val formattedDate = dateFormat.format(Date())
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(24.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text(
//            text = "Payment Successful",
//            style = MaterialTheme.typography.headlineSmall,
//            color = MaterialTheme.colorScheme.primary
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Text(
//            text = "Amount: €${"%.2f".format(amount)}",
//            style = MaterialTheme.typography.bodyLarge
//        )
//
//        Spacer(modifier = Modifier.height(32.dp))
//
//        Divider(modifier = Modifier.fillMaxWidth(0.8f))
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        Text(
//            text = formattedDate,
//            style = MaterialTheme.typography.bodyMedium,
//            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
//        )
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        Button(
//            onClick = {
//                authViewModel.logout()
//                onBackToPayment()
//            }
//        ) {
//            Text("Back to Payment")
//        }
//    }
//}
//





package com.example.newstripepayment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.newstripepayment.Component.Auth.AuthScreen
import com.example.newstripepayment.Component.Payment.PaymentFlowScreen
import com.example.newstripepayment.Component.Payment.PaymentSuccessScreen
import com.example.newstripepayment.Utils.PUBLISHABLE_KEY
import com.example.newstripepayment.ViewModel.AuthViewModel
import com.example.newstripepayment.api.ApiUtilities
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class MainActivity : ComponentActivity() {

    private lateinit var paymentSheet: PaymentSheet
    private var customerId by mutableStateOf("")
    private var ephemeralKey by mutableStateOf("")
    private var clientSecret by mutableStateOf("")
    private var paymentCurrency: String = "eur"
    private var showSuccess by mutableStateOf(false)
    private var paidAmount by mutableStateOf(0.0)
    private var isLoading by mutableStateOf(false)

    private val apiInterface = ApiUtilities.getApiInterface()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PaymentConfiguration.init(this, PUBLISHABLE_KEY)
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)

        setContent {
            val authViewModel: AuthViewModel = viewModel()
            val uiState by authViewModel.uiState.collectAsState()

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (showSuccess) {
                PaymentSuccessScreen(
                    amount = paidAmount,
                    authViewModel = authViewModel,
                    onBackToPayment = {
                        showSuccess = false
                        authViewModel.setPaymentAmount("")
                    }
                )
            } else if (uiState.isAuthenticated) {
                PaymentFlowScreen(
                    authViewModel = authViewModel,
                    onPaymentInitiated = {
                        if (customerId.isNotEmpty() && ephemeralKey.isNotEmpty()) {
                            initiatePayment(authViewModel.paymentAmount.value)
                        } else {
                            showToast("Payment not ready yet. Please wait...")
                            // Try to get customer ID and ephemeral key again
                            getCustomerId(authViewModel.uiState.value.selectedEmail)
                        }
                    }
                )
            } else {
                AuthScreen(
                    viewModel = authViewModel,
                    onAuthenticated = { email ->
                        getCustomerId(email)
                    }
                )
            }
        }
    }

    private fun initiatePayment(amount: String) {
        try {
            val amountInCents = (amount.toDouble() * 100).toInt()
            if (amountInCents < 50) { // Minimum amount check (50 cents)
                showToast("Amount must be at least €0.50")
                return
            }
            paidAmount = amount.toDouble()
            isLoading = true

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val response = apiInterface.getPaymentIntent(
                        customer = customerId,
                        amount = amountInCents.toString(),
                        currency = paymentCurrency
                    )
                    withContext(Dispatchers.Main) {
                        isLoading = false
                        if (response.isSuccessful && response.body() != null) {
                            clientSecret = response.body()!!.client_secret
                            if (clientSecret.isNotEmpty()) {
                                paymentFlow()
                            } else {
                                showError("Empty client secret received")
                            }
                        } else {
                            showError("Failed to create payment intent: ${response.message()}")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        isLoading = false
                        showError("Payment intent error: ${e.localizedMessage}")
                    }
                }
            }
        } catch (e: NumberFormatException) {
            showToast("Please enter a valid amount")
        }
    }

    private fun paymentFlow() {
        paymentSheet.presentWithPaymentIntent(
            clientSecret,
            PaymentSheet.Configuration(
                merchantDisplayName = "My Store",
                customer = PaymentSheet.CustomerConfiguration(customerId, ephemeralKey)
            )
        )
    }


    private fun getCustomerId(email: String) {
        isLoading = true
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiInterface.getCustomer(email)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        customerId = response.body()!!.id
                        getEphemeralKey()
                    } else {
                        isLoading = false
                        showError("Failed to create customer: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    showError("Customer error: ${e.localizedMessage}")
                }
            }
        }
    }

    private fun getEphemeralKey() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiInterface.getEphemeralKey(customerId)
                withContext(Dispatchers.Main) {
                    isLoading = false
                    if (response.isSuccessful && response.body() != null) {
                        ephemeralKey = response.body()!!.secret
                    } else {
                        showError("Failed to get ephemeral key: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    showError("Ephemeral key error: ${e.localizedMessage}")
                }
            }
        }
    }
    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Completed -> {
                showSuccess = true
            }
            is PaymentSheetResult.Canceled -> {
                showToast("Payment Canceled")
            }
            is PaymentSheetResult.Failed -> {
                showToast("Payment Failed: ${paymentSheetResult.error.message}")
            }
        }
    }

    private fun showError(message: String) {
        showToast(message)
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_LONG).show()
    }
}


