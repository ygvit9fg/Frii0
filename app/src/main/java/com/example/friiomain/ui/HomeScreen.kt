package com.example.friiomain.ui

import android.annotation.SuppressLint
import android.location.Location
import android.widget.Toast
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
fun HomeScreen(navController: NavController, email: String, user: String) {
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

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween // чтобы все равномерно уместилось
    ) {
        // --- Блок "Приветствие" ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Привет, $user 👋",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ready for walk?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray
                )
            }
            Row {
                IconButton(onClick = { /* TODO: Settings */ }) {
                    Icon(Icons.Default.Settings, contentDescription = "Настройки")
                }
                IconButton(onClick = { /* TODO: Notifications */ }) {
                    Icon(Icons.Default.Notifications, contentDescription = "Уведомления")
                }
            }
        }

        // --- Блок "Погода" ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
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
                            Icon(Icons.Default.Place, contentDescription = "Локация", tint = Color.Gray)
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

        // --- Блок "Weather Matches" ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Weather Matches", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    "Friends who also love this weather!",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(12.dp))

                repeat(2) { index -> // только 2 друга
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color.Gray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "AB",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Имя Друга ${index + 1}")
                            }
                            Button(onClick = { /* TODO: Invite */ }) {
                                Text("Invite")
                            }
                        }
                    }
                }
            }
        }

        // --- Блок "Friends" ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = "Friends")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Friends (2)", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { navController.navigate("addFriend/$email") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text("+", color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(2) {
                        Card(
                            modifier = Modifier.size(80.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("AB", fontWeight = FontWeight.Bold)
                                Text("Ник", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }

        // --- Блок "Climate Impact" ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🌱", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Your Climate Impact", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("12 km", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                        Text("km walked this week", fontSize = 12.sp, color = Color.Gray)
                    }
                    Column {
                        Text("3.5 kg", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                        Text("kg CO2 saved", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { navController.navigate("qrScanner/$email") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).padding(end = 4.dp)
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = "QR")
                Spacer(modifier = Modifier.width(4.dp))
                Text("QR Code")
            }

            Button(
                onClick = { /* TODO: Find Walks */ },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).padding(start = 4.dp)
            ) {
                Icon(Icons.Default.Place, contentDescription = "Find Walks")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Find Walks")
            }
        }
    }
}


