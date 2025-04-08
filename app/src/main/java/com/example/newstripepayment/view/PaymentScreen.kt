//package com.example.newstripepayment.view
//
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.unit.dp
//import com.example.newstripepayment.R
//import com.example.newstripepayment.api.ApiUtilities
//import com.stripe.android.paymentsheet.PaymentSheet
//import com.stripe.android.paymentsheet.PaymentSheetResult
//import kotlinx.coroutines.launch
//
//
//
//@Composable
//fun PaymentScreen(onPaymentSuccess: (Double) -> Unit) {
//    val context = LocalContext.current
//    val coroutineScope = rememberCoroutineScope()
//    val paymentSheet = remember { PaymentSheet(context, ::onPaymentSheetResult) }
//
//    var paymentState by remember { mutableStateOf<PaymentState>(PaymentState.Loading) }
//    val amount = 1099 // cents (€10.99)
//    val amountInEuros = amount.toDouble() / 100
//
//    LaunchedEffect(Unit) {
//        initializePayment()
//    }
//
//    fun initializePayment() {
//        paymentState = PaymentState.Loading
//        coroutineScope.launch {
//            try {
//                // 1. Create customer
//                val customerResponse = ApiUtilities.getApiInterface().getCustomer()
//                val customerId = customerResponse.body()?.id ?: throw Exception("No customer ID")
//
//                // 2. Get ephemeral key
//                val keyResponse = ApiUtilities.getApiInterface().getEphemeralKey(customerId)
//                val ephemeralKey = keyResponse.body()?.secret ?: throw Exception("No ephemeral key")
//
//                // 3. Create payment intent
//                val intentResponse = ApiUtilities.getApiInterface().getPaymentIntent(
//                    customer = customerId,
//                    amount = amount.toString(),
//                    currency = "eur"
//                )
//                val clientSecret = intentResponse.body()?.client_secret ?: throw Exception("No client secret")
//
//                paymentState = PaymentState.Ready(
//                    customerId = customerId,
//                    ephemeralKey = ephemeralKey,
//                    clientSecret = clientSecret
//                )
//            } catch (e: Exception) {
//                paymentState = PaymentState.Error(e.localizedMessage ?: "Unknown error")
//            }
//        }
//    }
//
//    fun onPaymentSheetResult(result: PaymentSheetResult) {
//        when (result) {
//            is PaymentSheetResult.Completed -> {
//                onPaymentSuccess(amountInEuros)
//                initializePayment() // Prepare for next payment
//            }
//            is PaymentSheetResult.Canceled -> {
//                paymentState = PaymentState.Error("Payment canceled")
//            }
//            is PaymentSheetResult.Failed -> {
//                paymentState = PaymentState.Error("Payment failed: ${result.error.message}")
//            }
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        when (val state = paymentState) {
//            is PaymentState.Loading -> {
//                CircularProgressIndicator()
//            }
//            is PaymentState.Ready -> {
//                Button(
//                    onClick = {
//                        paymentSheet.presentWithPaymentIntent(
//                            state.clientSecret,
//                            PaymentSheet.Configuration(
//                                merchantDisplayName = "My Store",
//                                customer = PaymentSheet.CustomerConfiguration(
//                                    state.customerId,
//                                    state.ephemeralKey
//                                )
//                            )
//                        )
//                    }
//                ) {
//                    Text("Pay ${"%.2f".format(amountInEuros)} €")
//                }
//            }
//            is PaymentState.Error -> {
//                Text(
//                    text = state.message,
//                    color = MaterialTheme.colorScheme.error,
//                    modifier = Modifier.padding(16.dp)
//                )
//                Button(onClick = { initializePayment() }) {
//                    Text("Retry")
//                }
//            }
//        }
//    }
//}
//
//sealed class PaymentState {
//    object Loading : PaymentState()
//    data class Ready(
//        val customerId: String,
//        val ephemeralKey: String,
//        val clientSecret: String
//    ) : PaymentState()
//    data class Error(val message: String) : PaymentState()
//}