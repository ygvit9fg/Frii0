package com.example.friiomain.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.friiomain.ui.components.ProfileDialog
import androidx.compose.runtime.getValue



@Composable
fun ProfileDialogScreen(onDismiss: () -> Unit) {
    val viewModel: ProfileViewModel = hiltViewModel()

    val name by viewModel.userName.collectAsState()
    val username by viewModel.userUsername.collectAsState()
    val email by viewModel.userEmail.collectAsState()
    val preferences by viewModel.userPreferences.collectAsState()

    ProfileDialog(
        name = viewModel.userName.collectAsState().value,
        usernameFlow = viewModel.userUsername, // <-- передаём Flow
        email = viewModel.userEmail.collectAsState().value,
        preferences = viewModel.userPreferences.collectAsState().value,
        onDismiss = onDismiss
    )
}


