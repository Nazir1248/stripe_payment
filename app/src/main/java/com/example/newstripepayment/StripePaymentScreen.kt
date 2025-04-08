//// StripePaymentScreen.kt
//package com.example.newstripepayment
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//
//@Composable
//fun StripePaymentScreen(onPayClick: () -> Unit) {
//    Surface(
//        modifier = Modifier.fillMaxSize(),
//        color = MaterialTheme.colorScheme.background
//    ) {
//        Box(
//            contentAlignment = Alignment.Center,
//            modifier = Modifier.fillMaxSize()
//        ) {
//            Button(
//                onClick = onPayClick,
//                modifier = Modifier
//                    .padding(16.dp)
//                    .height(50.dp)
//                    .fillMaxWidth(0.6f)
//            ) {
//                Text("Pay Now")
//            }
//        }
//    }
//}
