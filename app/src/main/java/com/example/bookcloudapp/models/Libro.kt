package com.example.bookcloudapp.models

data class Libro(
    val isbn: String,
    val titulo: String,
    val autor: String,
    val editorial: String,
    val fecha_publicacion: String,
    val portada: String,
    val descripcion: String,
    val unidades: Int,
    val genero: String
)
