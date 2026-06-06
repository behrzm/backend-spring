package com.prolearn.codecraftfront.ui.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val email: String? = null,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val phoneAuthState: PhoneAuthState = PhoneAuthState.Idle
)

sealed class PhoneAuthState {
    object Idle : PhoneAuthState()
    data class CodeSent(val verificationId: String, val phoneNumber: String) : PhoneAuthState()
    object Verified : PhoneAuthState()
    data class Error(val message: String) : PhoneAuthState()
}

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(
        AuthUiState(
            isAuthenticated = auth.currentUser != null,
            email = auth.currentUser?.email,
        ),
    )
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private var storedVerificationId: String = ""

    fun refreshAuthState() {
        val user = auth.currentUser
        _uiState.update { it.copy(isAuthenticated = user != null, email = user?.email) }
    }

    fun signOut(onSignedOut: () -> Unit) {
        auth.signOut()
        _uiState.update {
            it.copy(
                isAuthenticated = false,
                email = null,
                errorMessage = null,
                infoMessage = null,
            )
        }
        onSignedOut()
    }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter email and password.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
            runCatching {
                auth.signInWithEmailAndPassword(email.trim(), password).await()
            }.onSuccess {
                _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
                onSuccess()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Unable to sign in.",
                    )
                }
            }
        }
    }

    fun register(email: String, password: String, confirmPassword: String, onSuccess: () -> Unit) {
        when {
            email.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Please fill in all fields.") }
                return
            }
            password != confirmPassword -> {
                _uiState.update { it.copy(errorMessage = "Passwords do not match.") }
                return
            }
            password.length < 6 -> {
                _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters.") }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
            runCatching {
                auth.createUserWithEmailAndPassword(email.trim(), password).await()
            }.onSuccess { authResult ->
                authResult.user?.sendEmailVerification()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        infoMessage = "Verification email sent. Check your inbox."
                    )
                }
                onSuccess()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Unable to register.",
                    )
                }
            }
        }
    }

    fun sendPasswordReset(email: String) {
        if (email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Enter email to reset password.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
            runCatching {
                auth.sendPasswordResetEmail(email.trim()).await()
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        infoMessage = "Password reset email sent.",
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Unable to send reset email.",
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, infoMessage = null) }
    }

    fun setError(message: String) {
        _uiState.update { it.copy(errorMessage = message, infoMessage = null) }
    }

    fun loginWithGoogle(idToken: String, onSuccess: () -> Unit) {
        if (idToken.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Google token is empty.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
            runCatching {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential).await()
            }.onSuccess {
                val user = auth.currentUser
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        email = user?.email,
                    )
                }
                onSuccess()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Google sign-in failed.",
                    )
                }
            }
        }
    }

    // ---------- Email Verification ----------
    fun sendEmailVerification() {
        val user = auth.currentUser ?: return
        viewModelScope.launch {
            runCatching { user.sendEmailVerification().await() }
                .onSuccess {
                    _uiState.update { it.copy(infoMessage = "Verification email sent. Check inbox.") }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(errorMessage = e.localizedMessage) }
                }
        }
    }

    // ---------- Phone Auth ----------
    fun startPhoneNumberVerification(phoneNumber: String, activity: Activity) {
        if (phoneNumber.isBlank()) {
            _uiState.update { it.copy(phoneAuthState = PhoneAuthState.Error("Enter phone number")) }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneCredential(credential) {}
            }

            override fun onVerificationFailed(e: FirebaseException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        phoneAuthState = PhoneAuthState.Error(e.localizedMessage ?: "SMS verification failed")
                    )
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                storedVerificationId = verificationId
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        phoneAuthState = PhoneAuthState.CodeSent(verificationId, phoneNumber)
                    )
                }
            }
        }
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60L, TimeUnit.SECONDS,
            activity,
            callbacks
        )
    }

    fun verifySmsCode(code: String, onSuccess: () -> Unit) {
        val verificationId = storedVerificationId
        if (verificationId.isBlank() || code.isBlank()) {
            _uiState.update { it.copy(phoneAuthState = PhoneAuthState.Error("Enter SMS code")) }
            return
        }
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneCredential(credential, onSuccess)
    }

    private fun signInWithPhoneCredential(credential: PhoneAuthCredential, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { auth.signInWithCredential(credential).await() }
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            phoneAuthState = PhoneAuthState.Verified
                        )
                    }
                    onSuccess()
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            phoneAuthState = PhoneAuthState.Error(e.localizedMessage ?: "Phone sign-in failed")
                        )
                    }
                }
        }
    }

    // ---------- GitHub OAuth ----------
    fun loginWithGitHub(activity: Activity, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val provider = OAuthProvider.newBuilder("github.com")
            .addCustomParameter("allow_signup", "true")
            .build()

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        auth.startActivityForSignInWithProvider(activity, provider)
            .addOnSuccessListener {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        email = auth.currentUser?.email,
                    )
                }
                onSuccess()
            }
            .addOnFailureListener { e ->
                _uiState.update { it.copy(isLoading = false) }
                onError(e.localizedMessage ?: "GitHub sign-in failed")
            }
    }

    // ---------- Passwordless Email Sign-In ----------
    fun sendSignInLink(email: String, onSent: () -> Unit = {}) {
        if (email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Enter email to receive sign-in link") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
            val actionCodeSettings = ActionCodeSettings.newBuilder()
                .setUrl("https://codequestdiplomaproject.firebaseapp.com/email-auth")// ← Firebase Hosting
                .setHandleCodeInApp(true)
                .setAndroidPackageName("com.prolearn.codecraftfront", true, null)
                .build()
            runCatching {
                auth.sendSignInLinkToEmail(email.trim(), actionCodeSettings).await()
            }.onSuccess {
                _uiState.update { it.copy(isLoading = false, infoMessage = "Sign-in link sent! Check your email.") }
                onSent()
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage ?: "Failed to send link") }
            }
        }
    }
    fun signInWithEmailLink(email: String, link: String, onSuccess: () -> Unit) {
        if (auth.isSignInWithEmailLink(link)) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                runCatching {
                    auth.signInWithEmailLink(email, link).await()
                }.onSuccess {
                    _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
                    onSuccess()
                }.onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
                }
            }
        } else {
            _uiState.update { it.copy(errorMessage = "Invalid sign-in link") }
        }
    }

    fun resetPhoneAuthState() {
        _uiState.update { it.copy(phoneAuthState = PhoneAuthState.Idle) }
    }

    fun signInAnonymously(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { auth.signInAnonymously().await() }
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
                    onSuccess()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
                }
        }
    }
}