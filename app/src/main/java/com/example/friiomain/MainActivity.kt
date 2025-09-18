package com.example.friiomain

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.friiomain.data.DataStoreManager
import com.example.friiomain.ui.*
import com.example.friiomain.ui.theme.FriioMainTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val CAMERA_PERMISSION_CODE = 100
    private val LOCATION_PERMISSION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestCameraPermission()
        requestLocationPermission()

        val dataStoreManager = DataStoreManager(this)

        setContent {
            FriioMainTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    val userEmail by dataStoreManager.userEmail.collectAsState(initial = null)

                    NavHost(
                        navController = navController,
                        startDestination = if (userEmail == null) "login" else "home/$userEmail"
                    ) {
                        composable("login") {
                            LoginScreen(
                                navController = navController,
                                onLoginSuccess = { email ->
                                    lifecycleScope.launch {
                                        dataStoreManager.saveUserEmail(email)
                                    }
                                    navController.navigate("home/$email") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("qrScanner") {
                            QrScannerScreen(navController) { result ->
                                // обработка результата сканирования QR
                            }
                        }

                        composable(
                            "home/{email}",
                            arguments = listOf(navArgument("email") {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            HomeScreen(navController = navController, email = email)
                        }

                        composable(
                            "friends/{email}",
                            arguments = listOf(navArgument("email") {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            FriendsScreen(
                                navController = navController,
                                currentUserEmail = email
                            )
                        }

                        composable(
                            "addFriend/{email}",
                            arguments = listOf(navArgument("email") {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            AddFriendScreen(
                                navController = navController,
                                currentUserEmail = email
                            )
                        }
                    }
                }
            }
        }
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_CODE
            )
        }
    }
}





