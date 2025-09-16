package com.example.friiomain.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.friiomain.data.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, currentUserEmail: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var weatherText by remember { mutableStateOf("Погода: Загрузка...") }
    val weatherRepo = remember { WeatherRepository() }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    weatherRepo.getWeather("Moscow")
                }
                weatherText =
                    "Погода: ${response.main.temp}°C, ${response.weather.firstOrNull()?.description ?: ""}"
            } catch (e: Exception) {
                weatherText = "Ошибка загрузки погоды"
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Погода
            Text(
                text = weatherText,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(24.dp))


            Button(
                onClick = { navController.navigate("friends/$currentUserEmail") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Мои друзья")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка "Добавить в друзья"
            Button(
                onClick = { navController.navigate("addFriend/$currentUserEmail") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Добавить в друзья")
            }
        }


        FloatingActionButton(
            onClick = {
                Toast.makeText(context, "Открываем сканер QR (заглушка)", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .size(80.dp),
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                Icons.Rounded.QrCode,
                contentDescription = "Сканировать QR",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}





