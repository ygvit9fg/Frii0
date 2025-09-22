package com.example.friiomain.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.friiomain.ui.components.ProfileDialog


@Composable
fun ProfileDialogScreen(onDismiss: () -> Unit) {
    val viewModel: ProfileViewModel = hiltViewModel()

    ProfileDialog(
        name = viewModel.userName.collectAsState().value,
        username = viewModel.userUsername.collectAsState().value,
        email = viewModel.userEmail.collectAsState().value,
        preferences = viewModel.userPreferences.collectAsState().value,
        onDismiss = onDismiss
    )
}
