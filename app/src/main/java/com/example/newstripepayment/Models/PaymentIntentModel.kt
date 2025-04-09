data class PaymentIntentModel(
    val id: String,
    val client_secret: String,
    val customer: String?
)