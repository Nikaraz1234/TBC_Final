package com.example.mycomposeapp.core.presentation.common

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.mycomposeapp.core.domain.keys.GoogleAuthConstants
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

object GoogleSignInLauncher {

    sealed interface Result {
        data class Success(val idToken: String) : Result
        data object Cancelled : Result
        data class Error(val message: String) : Result
    }

    suspend fun launch(context: Context, tag: String = "GoogleSignIn"): Result {
        return try {
            val credentialManager = CredentialManager.create(context)
            val signInOption = GetSignInWithGoogleOption.Builder(GoogleAuthConstants.WEB_CLIENT_ID)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInOption)
                .build()

            val result = credentialManager.getCredential(context, request)
            val credential = result.credential
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            Result.Success(googleIdTokenCredential.idToken)
        } catch (e: GetCredentialCancellationException) {
            Result.Cancelled
        } catch (e: NoCredentialException) {
            Log.e(tag, "No credentials available", e)
            Result.Error("Google Sign-In is not available. Please check app configuration.")
        } catch (e: GetCredentialException) {
            Log.e(tag, "Google sign-in failed", e)
            Result.Error("Google sign-in failed: ${e.message}")
        }
    }
}
