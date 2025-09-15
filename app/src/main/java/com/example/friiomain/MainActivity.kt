package com.example.friiomain

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.friiomain.ui.FriendsScreen
import com.example.friiomain.ui.HomeScreen
import com.example.friiomain.ui.ProfileScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()

                NavHost(navController, startDestination = "home/{email}") {
                    // HomeScreen
                    composable("home/{email}") { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("email") ?: ""
                        HomeScreen(navController, email)
                    }

                    // FriendsScreen
                    composable("friends/{email}") { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("email") ?: ""
                        FriendsScreen(navController, email)
                    }

                    // ProfileScreen
                    composable("profile/{email}") { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("email") ?: ""
                        ProfileScreen(navController, email)
                    }
                }
            }
        }
    }
}







