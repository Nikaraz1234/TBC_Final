package com.example.mycomposeapp.feature.profile.edit_profile.presentation

import android.Manifest
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.mycomposeapp.core.domain.model.User
import com.example.mycomposeapp.core.domain.model.UserStats
import com.example.mycomposeapp.core.ui.R as CoreUiR
import com.example.mycomposeapp.core.ui.components.buttons.ButtonMedium
import com.example.mycomposeapp.core.ui.components.buttons.ButtonStyle
import com.example.mycomposeapp.core.ui.components.input.AppTextField
import com.example.mycomposeapp.core.ui.components.input.PasswordTextField
import com.example.mycomposeapp.core.ui.theme.AppTheme
import com.example.mycomposeapp.core.ui.theme.AppTheme.colors
import com.example.mycomposeapp.core.ui.theme.AppTheme.spacing
import com.example.mycomposeapp.core.ui.theme.MyComposeAppTheme
import kotlinx.coroutines.flow.collectLatest
import com.example.mycomposeapp.feature.profile.edit_profile.presentation.R as EditProfileR

@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    showSnackBar: (String) -> Unit
) {
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

private enum class AvatarPickSource { Camera, Gallery }

@Composable
private fun EditProfileContent(
    state: EditProfileContract.State,
    onEvent: (EditProfileContract.Event) -> Unit
) {
    val typography = AppTheme.typography
    val context = LocalContext.current
    val activity = remember { context.findActivity() }

    var showSettingsDialog by remember { mutableStateOf(false) }
    var settingsDialogText by remember { mutableStateOf("" to "") } // title/body

    var showPickSourceDialog by remember { mutableStateOf(false) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    val cameraPermTitle = stringResource(EditProfileR.string.camera_permission_needed_title)
    val cameraPermBody = stringResource(EditProfileR.string.camera_permission_needed_body)
    

    val takePictureLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                pendingCameraUri?.let { uri ->
                    onEvent(EditProfileContract.Event.OnPhotoSelected(uri.toString()))
                }
            }
            pendingCameraUri = null
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
        val uri = createTempImageUri() ?: return
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

                if (!shouldShowRationale) {
                    settingsDialogText = cameraPermTitle to cameraPermBody
                    showSettingsDialog = true
                }
            }
        }

    val pickPhotoPickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let { onEvent(EditProfileContract.Event.OnPhotoSelected(it.toString())) }
        }

    val legacyGetContentLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { onEvent(EditProfileContract.Event.OnPhotoSelected(it.toString())) }
        }

    fun storagePermission(): String =
        if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_IMAGES
        else Manifest.permission.READ_EXTERNAL_STORAGE

    val storagePermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                legacyGetContentLauncher.launch("image/*")
            } else {
                val perm = storagePermission()
                val shouldShowRationale = activity?.let {
                    ActivityCompat.shouldShowRequestPermissionRationale(it, perm)
                } ?: true

                if (!shouldShowRationale) {
                    settingsDialogText = cameraPermTitle to cameraPermBody
                    showSettingsDialog = true
                }
            }
        }

    fun startGalleryFlow() {
        if (Build.VERSION.SDK_INT >= 33) {
            pickPhotoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
            return
        }

        val perm = storagePermission()
        val granted = ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
        if (granted) legacyGetContentLauncher.launch("image/*")
        else storagePermissionLauncher.launch(perm)
    }

    fun handlePickSource(source: AvatarPickSource) {
        when (source) {
            AvatarPickSource.Camera -> {
                val granted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

                if (granted) startCameraFlow()
                else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }

            AvatarPickSource.Gallery -> startGalleryFlow()
        }
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text(text = settingsDialogText.first, style = typography.titleLarge) },
            text = { Text(text = settingsDialogText.second, style = typography.bodyMedium) },
            confirmButton = {
                TextButton(onClick = {
                    showSettingsDialog = false
                    context.startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        )
                    )
                }) {
                    Text(text = stringResource(EditProfileR.string.open_settings), style = typography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text(text = stringResource(EditProfileR.string.cancel), style = typography.labelLarge)
                }
            }
        )
    }

    if (showPickSourceDialog) {
        AlertDialog(
            onDismissRequest = { showPickSourceDialog = false },
            title = { Text(text = stringResource(EditProfileR.string.choose_photo_source_title), style = typography.titleLarge) },
            text = { Text(text = stringResource(EditProfileR.string.choose_photo_source_body), style = typography.bodyMedium) },
            confirmButton = {
                TextButton(onClick = {
                    showPickSourceDialog = false
                    handlePickSource(AvatarPickSource.Camera)
                }) {
                    Text(text = stringResource(EditProfileR.string.camera), style = typography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPickSourceDialog = false
                    handlePickSource(AvatarPickSource.Gallery)
                }) {
                    Text(text = stringResource(EditProfileR.string.gallery), style = typography.labelLarge)
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.spacing16)
        ) {
            ProfileTopBar(
                onBackClick = { onEvent(EditProfileContract.Event.OnBackClick) }
            )

            Spacer(modifier = Modifier.height(spacing.spacing16))

            ProfileAvatarCard(
                onChangePhotoClick = { showPickSourceDialog = true },
                photoUrl = state.user?.photoUrl
            )

            Spacer(modifier = Modifier.height(spacing.spacing16))

            Text(
                text = state.email,
                color = colors.white,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = typography.titleLarge
            )

            Spacer(modifier = Modifier.height(spacing.spacing32))

            EditField(
                title = stringResource(EditProfileR.string.username),
                value = state.username,
                onValueChange = { onEvent(EditProfileContract.Event.OnUsernameChanged(it)) },
                error = null
            )

            Spacer(modifier = Modifier.height(spacing.spacing16))

            ButtonMedium(
                text = stringResource(EditProfileR.string.change_username),
                onClick = { onEvent(EditProfileContract.Event.OnChangeUsernameClicked) },
                style = ButtonStyle.Filled,
                enabled = true,
                modifier = Modifier.padding(vertical = spacing.spacing16)
            )

            Spacer(modifier = Modifier.height(spacing.spacing16))

            PasswordTextField(
                label = stringResource(EditProfileR.string.current_password),
                value = state.currentPassword,
                onValueChange = { onEvent(EditProfileContract.Event.OnCurrentPasswordChanged(it)) },
                error = null
            )

            Spacer(modifier = Modifier.height(spacing.spacing16))

            PasswordTextField(
                label = stringResource(EditProfileR.string.new_password),
                value = state.newPassword,
                onValueChange = { onEvent(EditProfileContract.Event.OnNewPasswordChanged(it)) },
                error = null
            )

            Spacer(modifier = Modifier.height(spacing.spacing16))

            PasswordTextField(
                label = stringResource(EditProfileR.string.confirm_new_password),
                value = state.confirmPassword,
                onValueChange = { onEvent(EditProfileContract.Event.OnConfirmPasswordChanged(it)) },
                error = null
            )

            Spacer(modifier = Modifier.height(spacing.spacing16))

            ButtonMedium(
                text = stringResource(EditProfileR.string.change_password),
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
) {
    Column(modifier = modifier) {
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
    val typography = AppTheme.typography

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
                    contentDescription = stringResource(EditProfileR.string.back),
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
                text = stringResource(EditProfileR.string.edit_profile_title),
                style = typography.titleLarge.copy(
                    brush = colors.goldTextGradient,
                    fontWeight = FontWeight.SemiBold
                ),
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
    val typography = AppTheme.typography

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
                            contentDescription = stringResource(EditProfileR.string.change_photo),
                            tint = colors.white,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = stringResource(EditProfileR.string.change_upper),
                            color = colors.white,
                            style = typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = typography.labelSmall.letterSpacing
                            )
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