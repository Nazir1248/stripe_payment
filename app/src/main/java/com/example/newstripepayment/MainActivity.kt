// MainActivity.kt
package com.example.newstripepayment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.newstripepayment.Utils.PUBLISHABLE_KEY
import com.example.newstripepayment.api.ApiUtilities
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {

    private lateinit var paymentSheet: PaymentSheet
    private lateinit var customerId: String
    private var ephemeralKey: String = ""
    private var clientSecret: String = ""
    private var paymentCurrency: String = "eur"
    private var showSuccess by mutableStateOf(false)
    private var paymentAmount by mutableStateOf("")
    private var paidAmount by mutableStateOf(0.0)

    private val apiInterface = ApiUtilities.getApiInterface()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PaymentConfiguration.init(this, PUBLISHABLE_KEY)
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
        getCustomerId()

        setContent {
            if (showSuccess) {
                PaymentSuccessScreen(amount = paidAmount)
            } else {
                PaymentInputScreen(
                    amount = paymentAmount,
                    onAmountChange = { paymentAmount = it },
                    onPayClick = {
                        if (customerId.isNotEmpty() && ephemeralKey.isNotEmpty()) {
                            initiatePayment()
                        } else {
                            showToast("Payment not ready yet. Please wait...")
                        }
                    }
                )
            }
        }
    }

    private fun initiatePayment() {
        try {
            val amountInCents = (paymentAmount.toDouble() * 100).toInt()
            paidAmount = paymentAmount.toDouble()

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val response = apiInterface.getPaymentIntent(
                        customer = customerId,
                        amount = amountInCents.toString(),
                        currency = paymentCurrency
                    )
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful && response.body() != null) {
                            clientSecret = response.body()!!.client_secret
                            paymentFlow()
                        } else {
                            showError("Failed to create payment intent: ${response.message()}")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
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

    private fun getCustomerId() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiInterface.getCustomer()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        customerId = response.body()!!.id
                        getEphemeralKey()
                    } else {
                        showError("Failed to create customer: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
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
                    if (response.isSuccessful && response.body() != null) {
                        ephemeralKey = response.body()!!.secret
                    } else {
                        showError("Failed to get ephemeral key: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
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

@Composable
fun PaymentInputScreen(
    amount: String,
    onAmountChange: (String) -> Unit,
    onPayClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = amount,
            onValueChange = onAmountChange,
            label = { Text("Enter Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onPayClick,
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(),
            enabled = amount.isNotEmpty()
        ) {
            Text("Pay Now")
        }
    }
}

@Composable
fun PaymentSuccessScreen(amount: Double) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("MMM d, yyyy, h:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date())

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(20.dp)){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Payment Successful",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Amount: €${"%.2f".format(amount)}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(32.dp))

            Divider(modifier = Modifier.fillMaxWidth(0.8f))

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = formattedDate,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(24.dp))

        }
    }
}

// Keep all other files (ApiInterface.kt, ApiUtilities.kt, Models, Utils.kt) the same as in your original code

@Preview(showBackground = true)
@Composable
fun PreviewPaymentSuccessScreen() {
    PaymentSuccessScreen(amount = 99.99)
}
