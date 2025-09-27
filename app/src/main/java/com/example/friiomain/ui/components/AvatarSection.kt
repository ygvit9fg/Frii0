package com.example.friiomain.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.friiomain.utils.bitmapToBase64
import com.example.friiomain.utils.base64ToBitmap
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.asImageBitmap




@Composable

fun AvatarSection(
    name: String,
    avatarBase64: String?,                // сохранённая аватарка из базы
    onAvatarChange: (String?) -> Unit     // callback для сохранения
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var avatarBitmap by remember { mutableStateOf(base64ToBitmap(avatarBase64 ?: "")) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        if (uri != null) {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            avatarBitmap = bitmap
            onAvatarChange(bitmapToBase64(bitmap)) // сохраняем в базу
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        // аватарка в круге
        Box(
            modifier = Modifier
                .size(85.dp)
                .clip(CircleShape)
                .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            if (avatarBitmap != null) {
                Image(
                    bitmap = avatarBitmap!!.asImageBitmap(),
                    contentDescription = "Avatar",
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = name.take(2).uppercase(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { launcher.launch("image/*") },
                shape = RoundedCornerShape(11.dp)
            ) {
                Text("Change Avatar")
            }
            Button(
                onClick = {
                    avatarBitmap = null
                    imageUri = null
                    onAvatarChange(null) // очищаем в базе
                },
                shape = RoundedCornerShape(11.dp),
                enabled = avatarBitmap != null
            ) {
                Text("Remove")
            }
        }
    }
}



