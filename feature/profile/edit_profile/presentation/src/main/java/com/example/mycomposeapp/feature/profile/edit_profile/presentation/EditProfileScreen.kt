package com.example.mycomposeapp.feature.profile.edit_profile.presentation

import android.Manifest
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mycomposeapp.core.ui.components.buttons.ButtonMedium
import com.example.mycomposeapp.core.ui.components.buttons.ButtonStyle
import com.example.mycomposeapp.core.ui.components.input.AppTextField
import com.example.mycomposeapp.core.ui.components.input.PasswordTextField
import com.example.mycomposeapp.core.ui.theme.AppTheme.colors
import com.example.mycomposeapp.core.ui.theme.AppTheme.spacing
import com.example.mycomposeapp.core.ui.theme.MyComposeAppTheme
import kotlinx.coroutines.flow.collectLatest
import com.example.mycomposeapp.core.ui.R as CoreUiR
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import com.example.mycomposeapp.core.domain.model.User
import com.example.mycomposeapp.core.domain.model.UserStats

@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    ){
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collectLatest { effect ->
            when (effect) {
                EditProfileContract.SideEffect.GoBack -> onBackClick()
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.onEvent(EditProfileContract.Event.OnScreenOpened)
    }

    EditProfileContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}
private fun Context.findActivity(): ComponentActivity? =
    generateSequence(this) { (it as? ContextWrapper)?.baseContext }
        .filterIsInstance<ComponentActivity>()
        .firstOrNull()
@Composable
private fun EditProfileContent(
    state: EditProfileContract.State,
    onEvent: (EditProfileContract.Event) -> Unit
) {
    val context = LocalContext.current
    val activity = remember { context.findActivity() }

    var showSettingsDialog by remember { mutableStateOf(false) }

    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    val takePictureLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                pendingCameraUri?.let { uri ->
                    onEvent(EditProfileContract.Event.OnPhotoSelected(uri.toString()))
                }
            } else {
                pendingCameraUri = null
            }
        }

    fun createTempImageUri(): Uri? {
        return try {
            val imagesDir = java.io.File(context.cacheDir, "images").apply { mkdirs() }
            val file = java.io.File.createTempFile("avatar_", ".jpg", imagesDir)

            androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            null
        }
    }

    fun startCameraFlow() {
        val uri = createTempImageUri()
        if (uri == null) {
            return
        }
        pendingCameraUri = uri
        takePictureLauncher.launch(uri)
    }

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                startCameraFlow()
            } else {
                val shouldShowRationale = activity?.let {
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        it,
                        Manifest.permission.CAMERA
                    )
                } ?: true

                if (!shouldShowRationale) showSettingsDialog = true
            }
        }

    fun handleAvatarClick() {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            startCameraFlow()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("Camera permission needed") },
            text = { Text("Enable Camera permission in Settings to change your profile photo.") },
            confirmButton = {
                TextButton(onClick = {
                    showSettingsDialog = false
                    context.startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        )
                    )
                }) { Text("Open Settings") }
            },
            dismissButton = {
                TextButton(onClick = { showSettingsDialog = false }) { Text("Cancel") }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(CoreUiR.drawable.app_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = spacing.spacing16)
        ) {
            ProfileTopBar(
                onBackClick = { onEvent(EditProfileContract.Event.OnBackClick) }
            )

            Spacer(modifier = Modifier.height(spacing.spacing16))

            ProfileAvatarCard(
                onChangePhotoClick = { handleAvatarClick() },
                photoUrl = state.user?.photoUrl
            )

            Spacer(modifier = Modifier.height(spacing.spacing16))

            Text(
                text = state.email,
                color = colors.white,
                fontSize = 20.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(spacing.spacing32))

            EditField(
                title = "Username",
                value = state.username,
                onValueChange = { onEvent(EditProfileContract.Event.OnUsernameChanged(it)) },
                error = null
            )

            Spacer(modifier = Modifier.height(spacing.spacing16))

            ButtonMedium(
                text = "Change Username",
                onClick = { onEvent(EditProfileContract.Event.OnChangeUsernameClicked) },
                style = ButtonStyle.Filled,
                enabled = true,
                modifier = Modifier.padding(vertical = spacing.spacing16)
            )

            Spacer(modifier = Modifier.height(spacing.spacing16))

            PasswordTextField(
                label = "Current Password",
                value = state.currentPassword,
                onValueChange = { onEvent(EditProfileContract.Event.OnCurrentPasswordChanged(it)) },
                error = null
            )

            Spacer(modifier = Modifier.height(spacing.spacing16))

            PasswordTextField(
                label = "New Password",
                value = state.newPassword,
                onValueChange = { onEvent(EditProfileContract.Event.OnNewPasswordChanged(it)) },
                error = null
            )

            Spacer(modifier = Modifier.height(spacing.spacing16))

            PasswordTextField(
                label = "Confirm New Password",
                value = state.confirmPassword,
                onValueChange = { onEvent(EditProfileContract.Event.OnConfirmPasswordChanged(it)) },
                error = null
            )

            Spacer(modifier = Modifier.height(spacing.spacing16))

            ButtonMedium(
                text = "Change Password",
                onClick = { onEvent(EditProfileContract.Event.OnChangePasswordClicked) },
                style = ButtonStyle.Filled,
                enabled = true,
                modifier = Modifier.padding(vertical = spacing.spacing16)
            )
        }
    }
}

@Composable
private fun EditField(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String? = null,
){
    Column(
        modifier = modifier
    ) {

        AppTextField(
            value = value,
            onValueChange = onValueChange,
            label = title,
            error = error
        )
    }


}
@Composable
private fun ProfileTopBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing.spacing12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.white
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = spacing.spacing32),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Edit Profile",
                style = TextStyle(
                    brush = colors.goldTextGradient,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

    }
}

@Composable
private fun ProfileAvatarCard(
    modifier: Modifier = Modifier,
    photoUrl: String?,
    onChangePhotoClick: () -> Unit = {},
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {

            Surface(
                shape = CircleShape,
                tonalElevation = 2.dp,
                modifier = Modifier
                    .size(120.dp)
                    .border(4.dp, colors.goldenYellow, CircleShape)
                    .clickable { onChangePhotoClick() }
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    val hasPhoto = !photoUrl.isNullOrBlank()

                    if (hasPhoto) {
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            placeholder = painterResource(CoreUiR.drawable.app_logo),
                            error = painterResource(CoreUiR.drawable.app_logo)
                        )
                    } else {
                        Image(
                            painter = painterResource(CoreUiR.drawable.app_logo),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }


                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colors.black.copy(alpha = 0.35f))
                    )

                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(CoreUiR.drawable.ic_camera),
                            contentDescription = "Change photo",
                            tint = colors.white,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "CHANGE",
                            color = colors.white,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
private fun EditProfileContentPreview() {
    MyComposeAppTheme {
        EditProfileContent(
            state = EditProfileContract.State(
                isLoading = false,
                user = User(
                    userId = "1",
                    username = "Nika",
                    photoUrl = "",
                    stats = UserStats()
                )
            ),
            onEvent = {}
        )
    }

}
