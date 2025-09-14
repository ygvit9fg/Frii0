package com.example.friiomain

import androidx.compose.material3.Text
import com.example.friiomain.ui.HomeScreen
import com.example.friiomain.ui.ProfileScreen
import com.example.friiomain.ui.LoginScreen
import com.example.friiomain.ui.RegisterScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") { LoginScreen(navController) }
                    composable("register") { RegisterScreen(navController) }
                    composable("home/{userEmail}") { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("userEmail") ?: ""
                        HomeScreen(navController, email)
                    }
                    composable("profile/{userEmail}") { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("userEmail") ?: ""
                        ProfileScreen(navController, email)
                    }
                    composable("weather") {
                        Text("Экран погоды (в разработке)")
                    }
                }

            }
        }
    }
    }




