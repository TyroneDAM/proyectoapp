package com.example.bookcloudapp.model

data class Reserva(
    val id_reserva: String,
    val isbn: String,
    val titulo: String,
    val autor: String,
    val imagen: String,
    val estado: String
)
