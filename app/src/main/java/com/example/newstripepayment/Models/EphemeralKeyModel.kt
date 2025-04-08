package com.example.newstripepayment.Models


data class EphemeralKeyModel(
    val id: String,
    val associated_objects: List<AssociatedObject>,
    val created: Long,
    val expires: Long,
    val livemode: Boolean,
    val secret: String
) {
    data class AssociatedObject(
        val id: String,
        val type: String
    )
}
