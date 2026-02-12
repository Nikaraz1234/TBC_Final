package com.example.mycomposeapp.core.data.repository

import com.example.mycomposeapp.core.data.common.HandleResponse
import com.example.mycomposeapp.core.domain.Resource
import com.example.mycomposeapp.core.domain.repository.AuthRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val handleResponse: HandleResponse
) : AuthRepository {

    override suspend fun login(email: String, password: String): Resource<String> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                Resource.Success(user.uid)
            } ?: Resource.Error("Login failed. Please try again.")
        } catch (e: FirebaseAuthInvalidUserException) {
            Resource.Error("No account found with this email")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Resource.Error("Invalid email or password")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Login failed. Please try again.")
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        displayName: String
    ): Resource<String> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                val profileUpdates = userProfileChangeRequest {
                    this.displayName = displayName
                }
                user.updateProfile(profileUpdates).await()
                Resource.Success(user.uid)
            } ?: Resource.Error("Registration failed. Please try again.")
        } catch (e: FirebaseAuthUserCollisionException) {
            Resource.Error("An account with this email already exists")
        } catch (e: FirebaseAuthWeakPasswordException) {
            Resource.Error("Password is too weak")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Resource.Error("Invalid email format")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Registration failed. Please try again.")
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Resource<String> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            result.user?.let { Resource.Success(it.uid) }
                ?: Resource.Error("Google sign-in failed")
        } catch (e: FirebaseAuthUserCollisionException) {
            Resource.Error("An account already exists with this email")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Google sign-in failed")
        }
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }
    override fun getCurrentUserEmail(): String? {
        return firebaseAuth.currentUser?.email
    }
    override fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Flow<Resource<Unit>> =
        handleResponse.safeApiCall {
            val user = firebaseAuth.currentUser ?: throw Exception("Not logged in")
            val email = user.email ?: throw Exception("No email associated with account")
            val credential = EmailAuthProvider.getCredential(email, currentPassword)

            user.reauthenticate(credential).await()
            user.updatePassword(newPassword).await()
            Unit
        }

    override fun changeEmail(
        currentPassword: String,
        newEmail: String
    ): Flow<Resource<Unit>> =
        handleResponse.safeApiCall {
            val user = firebaseAuth.currentUser ?: throw Exception("Not logged in")
            val email = user.email ?: throw Exception("No email associated with account")
            val credential = EmailAuthProvider.getCredential(email, currentPassword)

            user.reauthenticate(credential).await()
            user.updateEmail(newEmail).await()
            Unit
        }


}
