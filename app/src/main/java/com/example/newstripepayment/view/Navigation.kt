//package com.example.newstripepayment.view
//
//
//import androidx.compose.runtime.Composable
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//
//@Composable
//fun StripePaymentApp() {
//    val navController = rememberNavController()
//    NavHost(
//        navController = navController,
//        startDestination = "payment"
//    ) {
//        composable("payment") {
//            PaymentScreen(
//                onPaymentSuccess = { amount ->
//                    navController.navigate("success/$amount")
//                }
//            )
//        }
//        composable("success/{amount}") { backStackEntry ->
//            val amount = backStackEntry.arguments?.getString("amount")?.toDouble() ?: 0.0
//            PaymentSuccessScreen(
//                amountPaid = amount,
//                onDone = { navController.popBackStack() }
//            )
//        }
//    }
//}