package com.prolearn.codecraftfront.ui.screens

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.scale
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.prolearn.codecraftfront.R
import com.prolearn.codecraftfront.ui.auth.AuthUiState
import com.prolearn.codecraftfront.ui.auth.PhoneAuthState
import com.prolearn.codecraftfront.ui.theme.NeonGreen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onStart: () -> Unit) {
    val transition = rememberInfiniteTransition(label = "robotPulse")
    val mascotScale by transition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "mascotScale",
    )

    LaunchedEffect(Unit) {
        delay(1700)
        onStart()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        com.prolearn.codecraftfront.ui.theme.NeonPurple.copy(alpha = 0.32f),
                        MaterialTheme.colorScheme.background,
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.Android,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.scale(mascotScale),
            )
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = stringResource(R.string.splash_loading),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun LoginScreen(
    state: AuthUiState,
    onLogin: (String, String) -> Unit,
    onGoogleToken: (String) -> Unit,
    onGoogleError: (String) -> Unit,
    onForgotPassword: (String) -> Unit,
    onDismissMessage: () -> Unit,
    onOpenRegister: () -> Unit,
    onPhoneSignIn: (String, Activity) -> Unit,
    onVerifySmsCode: (String) -> Unit,                     // просто (String) -> Unit, навигация внутри
    onSignInWithGitHub: (Activity) -> Unit,
    onGithubError: (String) -> Unit,
    onSendSignInLink: (String) -> Unit,
    onResetPhoneState: () -> Unit = {},
    onSignInAnonymously: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? Activity
    var showPhoneSheet by remember { mutableStateOf(false) }
    var showGithubSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        com.prolearn.codecraftfront.ui.theme.NeonPurple.copy(alpha = 0.20f),
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface,
                    ),
                ),
            )
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.auth_login_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.auth_login_subtitle),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(22.dp))

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.58f),
            ),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    leadingIcon = { Icon(Icons.Rounded.Mail, contentDescription = null) },
                    label = { Text(stringResource(R.string.auth_email)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    leadingIcon = { Icon(Icons.Rounded.Password, contentDescription = null) },
                    label = { Text(stringResource(R.string.auth_password)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                )

                AnimatedVisibility(visible = state.errorMessage != null) {
                    MessageChip(
                        iconRes = Icons.Rounded.ErrorOutline,
                        message = state.errorMessage.orEmpty(),
                        tint = MaterialTheme.colorScheme.error,
                        onDismiss = onDismissMessage,
                    )
                }
                AnimatedVisibility(visible = state.infoMessage != null) {
                    MessageChip(
                        iconRes = Icons.Rounded.CheckCircle,
                        message = state.infoMessage.orEmpty(),
                        tint = MaterialTheme.colorScheme.primary,
                        onDismiss = onDismissMessage,
                    )
                }

                Text(
                    text = stringResource(R.string.auth_forgot_password),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 2.dp)
                        .clickable { onForgotPassword(email) },
                )

                Button(
                    onClick = { onLogin(email, password) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    enabled = !state.isLoading,
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                    Text(stringResource(R.string.auth_login_cta), fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onOpenRegister,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text(stringResource(R.string.auth_open_register))
                }

                // Google
                GoogleSignInButton(onGoogleToken, onGoogleError)

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "More sign‑in options",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(
                        onClick = {
                            onResetPhoneState()
                            showPhoneSheet = true
                        },
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(Icons.Rounded.Phone, contentDescription = null)
                        Text("Phone")
                    }
                    OutlinedButton(
                        onClick = { showGithubSheet = true },
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(Icons.Rounded.Code, contentDescription = null)
                        Text("GitHub")
                    }
                }

                OutlinedButton(
                    onClick = {
                        if (email.isNotBlank()) {
                            // Сохраняем email перед отправкой ссылки
                            val prefs = context.getSharedPreferences("auth_prefs", android.content.Context.MODE_PRIVATE)
                            prefs.edit().putString("email_for_link", email).apply()
                            onSendSignInLink(email)
                        } else {
                            onGithubError("Enter email to receive a sign‑in link")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Rounded.Email, contentDescription = null)
                    Text("Email Link Sign‑In")
                }

                OutlinedButton(
                    onClick = onSignInAnonymously,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Rounded.PersonOutline, contentDescription = null)
                    Text("Continue as Guest")
                }
            }
        }
    }

    // Bottom sheet для телефона
    if (showPhoneSheet) {
        PhoneSignInSheet(
            state = state.phoneAuthState,
            onDismiss = { showPhoneSheet = false },
            onSendCode = { phone -> onPhoneSignIn(phone, activity!!) },
            onVerifyCode = { code -> onVerifySmsCode(code) },  // просто (String) -> Unit
        )
    }

    // Bottom sheet для GitHub (OAuth)
    if (showGithubSheet) {
        GithubSignInSheet(
            onDismiss = { showGithubSheet = false },
            onSignIn = { act -> onSignInWithGitHub(act) },
            onError = onGithubError,
        )
    }
}

@Composable
fun RegisterScreen(
    state: AuthUiState,
    onRegister: (String, String, String) -> Unit,
    onDismissMessage: () -> Unit,
    onOpenLogin: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        com.prolearn.codecraftfront.ui.theme.NeonPurple.copy(alpha = 0.20f),
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface,
                    ),
                ),
            )
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.auth_register_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.auth_register_subtitle),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(22.dp))

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.58f),
            ),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var confirmPassword by remember { mutableStateOf("") }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    leadingIcon = { Icon(Icons.Rounded.Mail, contentDescription = null) },
                    label = { Text(stringResource(R.string.auth_email)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    leadingIcon = { Icon(Icons.Rounded.Password, contentDescription = null) },
                    label = { Text(stringResource(R.string.auth_password)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    leadingIcon = { Icon(Icons.Rounded.Password, contentDescription = null) },
                    label = { Text(stringResource(R.string.auth_confirm_password)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                )

                AnimatedVisibility(visible = state.errorMessage != null) {
                    MessageChip(
                        iconRes = Icons.Rounded.ErrorOutline,
                        message = state.errorMessage.orEmpty(),
                        tint = MaterialTheme.colorScheme.error,
                        onDismiss = onDismissMessage,
                    )
                }
                AnimatedVisibility(visible = state.infoMessage != null) {
                    MessageChip(
                        iconRes = Icons.Rounded.CheckCircle,
                        message = state.infoMessage.orEmpty(),
                        tint = MaterialTheme.colorScheme.primary,
                        onDismiss = onDismissMessage,
                    )
                }

                Button(
                    onClick = { onRegister(email, password, confirmPassword) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    enabled = !state.isLoading,
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                    Text(stringResource(R.string.auth_register_cta), fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onOpenLogin,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text(stringResource(R.string.auth_open_login))
                }
            }
        }
    }
}

// --- Вспомогательные компоненты ---

@Composable
private fun GoogleSignInButton(
    onGoogleToken: (String) -> Unit,
    onGoogleError: (String) -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    account.idToken?.let { onGoogleToken(it) }
                        ?: onGoogleError("Нет ID token. Проверьте Google Sign-In в Firebase Console.")
                } catch (e: ApiException) {
                    val hint = when (e.statusCode) {
                        10 -> "Ошибка конфигурации Google (SHA-1 / Web Client ID в Firebase)"
                        12501 -> "Вход отменён"
                        else -> e.localizedMessage
                    }
                    onGoogleError(hint ?: "Google sign-in failed")
                }
            }
            Activity.RESULT_CANCELED -> { /* пользователь закрыл диалог */ }
            else -> onGoogleError("Google sign-in failed (code ${result.resultCode})")
        }
    }

    Text(
        text = stringResource(R.string.auth_google),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val act = activity ?: return@clickable
                val webClientId = context.getString(R.string.default_web_client_id)
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(webClientId)
                    .requestEmail()
                    .build()
                val client = GoogleSignIn.getClient(act, gso)
                // Выходим из предыдущей сессии, чтобы заставить диалог выбора аккаунта
                client.signOut().addOnCompleteListener {
                    launcher.launch(client.signInIntent)
                }
            }
            .padding(top = 4.dp),
        textAlign = TextAlign.Center
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhoneSignInSheet(
    state: PhoneAuthState,
    onDismiss: () -> Unit,
    onSendCode: (String) -> Unit,
    onVerifyCode: (String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var phoneNumber by remember { mutableStateOf("") }
    var smsCode by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Sign in with phone", fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone number (+7...)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            )
            Button(
                onClick = { onSendCode(phoneNumber) },
                modifier = Modifier.fillMaxWidth(),
                enabled = phoneNumber.isNotBlank() && state !is PhoneAuthState.CodeSent,
            ) {
                Text("Send SMS")
            }

            if (state is PhoneAuthState.CodeSent) {
                OutlinedTextField(
                    value = smsCode,
                    onValueChange = { smsCode = it },
                    label = { Text("SMS code") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                Button(
                    onClick = { onVerifyCode(smsCode) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Verify")
                }
            }

            when (state) {
                is PhoneAuthState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
                PhoneAuthState.Verified -> Text("Verified!", color = NeonGreen)
                else -> {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GithubSignInSheet(
    onDismiss: () -> Unit,
    onSignIn: (Activity) -> Unit,
    onError: (String) -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Sign in with GitHub", fontWeight = FontWeight.Bold)
            Text(
                "You'll be redirected to GitHub to authorize CodeQuest.",
                style = MaterialTheme.typography.bodySmall,
            )
            Button(
                onClick = {
                    activity?.let { onSignIn(it) } ?: onError("Activity not available")
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Rounded.Code, contentDescription = null)
                Text("Continue with GitHub")
            }
        }
    }
}

@Composable
private fun MessageChip(
    iconRes: androidx.compose.ui.graphics.vector.ImageVector,
    message: String,
    tint: androidx.compose.ui.graphics.Color,
    onDismiss: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = tint.copy(alpha = 0.12f),
                shape = RoundedCornerShape(12.dp),
            )
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .clickable { onDismiss() },
    ) {
        Icon(imageVector = iconRes, contentDescription = null, tint = tint)
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}