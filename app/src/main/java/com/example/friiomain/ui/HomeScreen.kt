package com.example.friiomain.ui

import android.annotation.SuppressLint
import android.location.Location
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.friiomain.data.WeatherRepository
import com.example.friiomain.data.WeatherResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@SuppressLint("MissingPermission")
@Composable
fun HomeScreen(navController: NavController, email: String) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var weather by remember { mutableStateOf<WeatherResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val fusedLocationClient: FusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient(context)

                val location: Location? = fusedLocationClient.lastLocation.await()
                if (location != null) {
                    val repo = WeatherRepository("4731afa59235bbee6a194fc02cff4f8b")
                    val result = repo.getWeather(location.latitude, location.longitude)
                    weather = result
                } else {
                    launch(Dispatchers.Main) {
                        Toast.makeText(context, "Не удалось получить локацию", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                launch(Dispatchers.Main) {
                    Toast.makeText(context, "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                isLoading = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when {
            isLoading -> {
                CircularProgressIndicator()
            }
            weather != null -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Привет, $email", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Температура: ${weather!!.main.temp}°C", style = MaterialTheme.typography.headlineMedium)
                    Text("Описание: ${weather!!.weather[0].description}", style = MaterialTheme.typography.bodyLarge)

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { navController.navigate("friends/$email") }) {
                        Text("Мои друзья")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { navController.navigate("addFriend/$email") }) {
                        Text("Добавить друга")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { navController.navigate("qrScanner") }) {
                        Text("Сканировать QR")
                    }
                }
            }
            else -> {
                Text("Нет данных о погоде")
            }
        }
    }
}



