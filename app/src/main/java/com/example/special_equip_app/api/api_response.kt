package com.example.special_equip_app.api


data class loginResponse(
    val message:String,
    val user_id: Int
)

data class signinResponse(
    val message: String
)

data class all_equip(
    val pk: Int,
    val name: String,
    val description: String,
    val rental_price: Int,
    val count_available: Int,
    val photo: String
)



data class Saved_orders(
    val order_id: Int,
    val user_id: Int,
    val rental_date: String,
    val rental_time: Int,
    val total_price: Int,
    val order_status: String,
    val type_works: String,
    val equipment: all_equip,
)
