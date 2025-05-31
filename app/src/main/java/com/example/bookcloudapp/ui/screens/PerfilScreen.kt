package com.example.bookcloudapp.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.bookcloudapp.R
import com.example.bookcloudapp.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PerfilScreen(navController: NavHostController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("favoritos", Context.MODE_PRIVATE)
    val nombrePrefs = context.getSharedPreferences("bookcloud_prefs", Context.MODE_PRIVATE)
    val nombreUsuario = context.getSharedPreferences("bookcloud_prefs", Context.MODE_PRIVATE)
        .getString("usuario", "Usuario")

    var favoritos by remember { mutableStateOf(0) }
    var reservasActivas by remember { mutableStateOf(0) }
    var mostrarConfirmacionLogout by remember { mutableStateOf(false) }

    // Lista de consejos
    val consejos = listOf(
        "Lee al menos 10 páginas cada día.",
        "Lleva siempre un libro contigo.",
        "Subraya tus frases favoritas.",
        "Relee tus libros preferidos.",
        "Lee en un lugar tranquilo.",
        "Evita distracciones al leer.",
        "Apaga el móvil al menos 30 minutos al día para leer.",
        "Lee antes de dormir para descansar mejor.",
        "Hasta el infinito y más allá. — Toy Story",
        "No todo el que vaga está perdido. — J.R.R. Tolkien",
        "Las palabras son nuestra más inagotable fuente de magia. — J.K. Rowling",
        "Un libro es un sueño que tienes en tus manos. — Neil Gaiman",
        "Los libros son espejos: solo se ve en ellos lo que uno ya lleva dentro. — Carlos Ruiz Zafón",
        "Algunos libros hay que saborearlos, otros devorarlos. — Francis Bacon",
        "La lectura nos regala un lugar adonde ir cuando tenemos que quedarnos donde estamos. — Mason Cooley",
        "Somos lo que leemos. — Anónimo",
        "Un lector hoy, un líder mañana. — Margaret Fuller",
        "Leer es soñar con los ojos abiertos. — Anónimo",
        "La lectura es la fábrica de la imaginación. — Anónimo",
        "Si quieres aventuras, lee. Si quieres magia, lee más. — Anónimo",
        "Los libros son los amigos que nunca te fallan. — Anónimo",
        "Cuanto más leas, más cosas sabrás. Cuanto más sepas, más lejos llegarás. — Dr. Seuss",
        "La lectura es el viaje de los que no pueden tomar el tren. — Francis de Croisset",
        "Un hogar sin libros es un cuerpo sin alma. — Cicerón",
        "Aprende a leer, lee para aprender. — Anónimo",
        "El mundo pertenece a quienes leen. — Rick Holland",
        "Leer es resistir. — Anónimo",
        "No hay espectáculo más hermoso que la mirada de un niño que lee. — Günter Grass",
        "Leer libros buenos es como conversar con los grandes del pasado. — Descartes",
        "Lee. Todo lo demás vendrá solo. — Anónimo",
        "Leer es alimento para la mente. — Anónimo",
        "Cuando terminas un libro, no eres la misma persona que empezó. — Anónimo",
        "La lectura es el pasaporte a aventuras infinitas. — Anónimo",
        "Leer no es una opción. Es una necesidad. — Anónimo",
        "Un libro puede cambiarte la vida. Solo uno. — Anónimo",
        "Donde termina el libro, empieza tu imaginación. — Anónimo",
        "Quien lee vive mil vidas. Quien no lee, solo una. — George R.R. Martin",
        "Lee libros. ¡Son como WiFi para el cerebro!",
        "Un lector vive mil vidas. El que no lee, solo una... y es la del móvil.",
        "Leer es el único vicio que te hace parecer más inteligente.",
        "Tengo más libros pendientes que días de vida... ¡y sigo comprando!",
        "Leer no engorda. A menos que estés comiendo mientras lees.",
        "No estoy ignorándote, estoy en una escena importante.",
        "Marcar libros con lápiz es mi cardio.",
        "¿Fiesta? Yo ya tengo una con mi libro y mi mantita.",
        "¿Salir? No gracias, tengo una cita con mi protagonista favorito.",
        "Leer: la mejor forma de viajar sin moverse.",
        "Mi lista de libros por leer es un universo en expansión.",
        "Mis marcadores de libros son boletos a otros mundos.",
        "Más drama que en una telenovela: mi saga favorita.",
        "Tengo más personajes ficticios en mi corazón que exs.",
        "Leer me hace sentir más sabio... hasta que cierro el libro.",
        "Una historia al día mantiene la realidad alejada.",
        "No necesito terapia, necesito más libros.",
        "El café me mantiene despierto, los libros me mantienen vivo.",
        "Prefiero los libros a las personas. Tienen menos spoilers.",
        "Leer es mi deporte mental.",
        "Abro un libro, cierro el mundo.",
        "Leer me da superpoderes: concentración y evasión.",
        "Si un libro no me atrapa, lo cambio. No tengo contrato.",
        "Mis emociones dependen del capítulo que lea.",
        "Leer libros tristes me hace feliz. ¿Eso es normal?",
        "Me enamoro más rápido de personajes que de personas.",
        "Mi casa ideal: una habitación y 3000 libros.",
        "No estoy procrastinando, estoy cultivando el alma.",
        "Leer libros: el cardio emocional del día.",
        "Lee al menos 10 páginas cada día.",
        "Lleva siempre un libro contigo.",
        "Rellena los tiempos muertos con lectura.",
        "Subraya o resalta tus frases favoritas.",
        "Relee tus libros preferidos una vez al año.",
        "Lee en un lugar tranquilo sin interrupciones.",
        "Apaga el móvil 30 minutos al día para leer.",
        "Lee antes de dormir para desconectar.",
        "Alterna géneros para mantener el interés.",
        "Haz una lista de libros pendientes.",
        "Comparte lo que lees con amigos.",
        "Únete a un club de lectura.",
        "Lee con una bebida caliente al lado.",
        "Escoge libros cortos si estás sin tiempo.",
        "Prueba leer en voz alta para disfrutar el ritmo.",
        "Lee con luz natural siempre que puedas.",
        "Evita multitarea mientras lees.",
        "Lee lo que te gusta, no lo que está de moda.",
        "Marca tus citas favoritas en un cuaderno.",
        "Lleva un diario de lecturas.",
        "Visita bibliotecas locales con frecuencia.",
        "Aprovecha apps de lectura para organizarte.",
        "Lee entrevistas de autores que te gusten.",
        "Prueba audiolibros en trayectos largos.",
        "Cambia de libro si uno no te atrapa.",
        "Crea un rincón de lectura en casa.",
        "Deja que tus emociones conecten con la historia.",
        "Haz una pausa si pierdes el hilo.",
        "Disfruta del olor de un libro nuevo (o viejo)."
    )
    val consejoAleatorio = remember { consejos.random() }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            ApiService.obtenerLibros { listaLibros ->
                val idsValidos =
                    listaLibros.mapNotNull { it["id"]?.takeIf { it.isNotBlank() } ?: it["titulo"] }

                val editor = prefs.edit()
                var totalFavoritos = 0

                prefs.all.forEach { entry ->
                    val clave = entry.key
                    val valor = entry.value

                    val esFavorito = valor is Boolean && valor == true
                    val esLibroValido = idsValidos.contains(clave)

                    if (esFavorito && esLibroValido) {
                        totalFavoritos++
                    } else if (!esLibroValido || valor !is Boolean) {
                        editor.remove(clave)
                    }
                }

                editor.apply()
                favoritos = totalFavoritos
            }
        }

        ApiService.obtenerReservas { lista ->
            reservasActivas = lista.count { it.estado == "activa" }
        }
    }

    val imagenZorro = remember(favoritos, reservasActivas) {
        val probabilidadEasterEgg = 0.1
        val usarEasterEgg = Math.random() < probabilidadEasterEgg

        val imagenesEasterEgg = listOf(
            R.drawable.zorro_lol,
            R.drawable.zorro_creepy,
            R.drawable.zorro_jesus,
            R.drawable.zorro_duolingo,
        )

        if (usarEasterEgg) {
            imagenesEasterEgg.random()
        } else {
            when {
                favoritos == 0 && reservasActivas == 0 -> R.drawable.zorro_triste
                favoritos > 0 && reservasActivas > 0 -> R.drawable.zorro_lector
                else -> R.drawable.zorro_leyendo
            }
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fondo_bosque),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.fillMaxSize().alpha(0.8f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Perfil de $nombreUsuario",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Image(
                painter = painterResource(id = imagenZorro),
                contentDescription = "Estado del zorro",
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("📚 Libros favoritos: $favoritos", style = MaterialTheme.typography.bodyLarge)
            Text(
                "📖 Libros reservados: $reservasActivas",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Consejo de lectura:",
                style = MaterialTheme.typography.titleMedium,
                color = Color.DarkGray
            )
            Text(
                text = consejoAleatorio,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.DarkGray,
                modifier = Modifier.padding(12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    navController.popBackStack("libros", inclusive = false)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Volver a tus libros")
            }

            Button(
                onClick = { mostrarConfirmacionLogout = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = Color.White
                )
            ) {
                Text("Cerrar sesión")
            }
            if (mostrarConfirmacionLogout) {
                AlertDialog(
                    onDismissRequest = { mostrarConfirmacionLogout = false },
                    title = { Text("¿Cerrar sesión?") },
                    text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
                    confirmButton = {
                        TextButton(onClick = {
                            mostrarConfirmacionLogout = false
                            nombrePrefs.edit().clear().apply()
                            navController.navigate("login") {
                                popUpTo("perfil") { inclusive = true }
                            }
                        }) {
                            Text("Sí, cerrar sesión", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarConfirmacionLogout = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}

