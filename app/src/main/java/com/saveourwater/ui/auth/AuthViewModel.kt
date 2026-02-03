package com.saveourwater.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saveourwater.data.remote.SupabaseModule
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch

/**
 * AuthViewModel - Handles authentication logic with Supabase
 */
class AuthViewModel : ViewModel() {

    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState

    /**
     * Login with email and password
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            try {
                if (!SupabaseModule.isConfigured) {
                    _authState.value = AuthState.Error("Supabase not configured")
                    return@launch
                }

                SupabaseModule.client.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }

                _authState.value = AuthState.Success("Login successful!")

            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    /**
     * Sign up with email and password
     */
    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            try {
                if (!SupabaseModule.isConfigured) {
                    _authState.value = AuthState.Error("Supabase not configured")
                    return@launch
                }

                SupabaseModule.client.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }

                _authState.value = AuthState.Success("Account created! Please check your email to verify.")

            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Sign up failed")
            }
        }
    }

    /**
     * Sign out current user
     */
    fun signOut() {
        viewModelScope.launch {
            try {
                SupabaseModule.client.auth.signOut()
                _authState.value = AuthState.Idle
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Sign out failed")
            }
        }
    }

    /**
     * Check if user is currently logged in
     */
    fun isLoggedIn(): Boolean {
        return try {
            SupabaseModule.client.auth.currentUserOrNull() != null
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get current user email
     */
    fun getCurrentUserEmail(): String? {
        return try {
            SupabaseModule.client.auth.currentUserOrNull()?.email
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * Sealed class representing authentication states
 */
sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

