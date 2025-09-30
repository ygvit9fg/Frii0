package com.example.friiomain.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.friiomain.ui.components.ProfileDialog
import androidx.compose.runtime.getValue
import androidx.navigation.NavController






@Composable
fun ProfileDialogScreen(
    navController: NavController,
    onDismiss: () -> Unit
) {
    val viewModel: ProfileViewModel = hiltViewModel()

    val name by viewModel.userName.collectAsState()
    val username by viewModel.userUsername.collectAsState()
    val email by viewModel.userEmail.collectAsState()
    val preferences by viewModel.userPreferences.collectAsState()

    ProfileDialog(
        viewModel = viewModel,
        avatarBase64 = null,
        onAvatarChange = {},
        onDismiss = onDismiss,
        navController = navController
    )
}



