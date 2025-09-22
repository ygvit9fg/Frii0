package com.example.friiomain.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.friiomain.data.WeatherRepository
import com.example.friiomain.data.WeatherResponse
import com.example.friiomain.ui.components.HomeTopBar
import com.example.friiomain.ui.components.ProfileDialog
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.location.Location

@Composable
fun HomeScreen(navController: NavController, email: String, user: String) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var weather by remember { mutableStateOf<WeatherResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    var showProfileDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            coroutineScope.launch(Dispatchers.IO) {
                weather = loadWeather(context)
                isLoading = false
            }
        } else {
            Toast.makeText(context, "Нет доступа к геолокации", Toast.LENGTH_LONG).show()
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            coroutineScope.launch(Dispatchers.IO) {
                weather = loadWeather(context)
                isLoading = false
            }
        } else {
            locationPermissionLauncher.launch(permission)
        }
    }

    // Диалоги
    if (showProfileDialog) {
        ProfileDialog(
            name = "Test",
            username = "user123",
            email = "test@mail.com",
            preferences = listOf("Coffee lover", "Early bird"),
            onDismiss = { showProfileDialog = false }
        )
    }

    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            confirmButton = {},
            title = { Text("Уведомления") },
            text = { Text("Пока пусто 🚀") }
        )
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            confirmButton = {},
            title = { Text("Настройки") },
            text = { Text("Раздел в разработке ⚙️") }
        )
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        HomeTopBar(
            user = user,
            onProfileClick = { showProfileDialog = true },
            onNotificationsClick = { showNotificationsDialog = true },
            onSettingsClick = { showSettingsDialog = true }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Погода
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Thermostat, contentDescription = "Температура")
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Place,
                                contentDescription = "Локация",
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Город (заглушка)", fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = weather?.weather?.get(0)?.description ?: "Нет данных",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "${weather?.main?.temp ?: "--"}°C",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Здесь можно добавить блоки "Weather Matches", "Friends" и "Climate Impact" аналогично твоему коду
    }
}

// Функция для получения погоды
suspend fun loadWeather(context: Context): WeatherResponse? {
    return try {
        // Проверяем разрешение прямо здесь
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            return null // Пользователь не дал разрешение
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val location = try {
            fusedLocationClient.lastLocation.await()
        } catch (e: SecurityException) {
            e.printStackTrace()
            null
        }

        location?.let {
            val repo = WeatherRepository("4731afa59235bbee6a194fc02cff4f8b")
            repo.getWeather(it.latitude, it.longitude)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}





