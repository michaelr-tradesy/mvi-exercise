package com.example.mviexercise.ui.login

import java.io.Serializable

sealed class LoginViewState<out T : Any> : Serializable {
    data class Idle(val data: String? = null) : LoginViewState<Nothing>()
    data class ReadyForUserName(val data: String? = null) : LoginViewState<Nothing>()
    data class UserNameInProgress(val data: String? = null) : LoginViewState<Nothing>()
    data class UserNameInvalid(val data: String? = null) : LoginViewState<Nothing>()
    data class FocusOnUserName(val data: String? = null) : LoginViewState<Nothing>()
    data class ReadyForPassword(val data: String? = null) : LoginViewState<Nothing>()
    data class PasswordInProgress(val data: String? = null) : LoginViewState<Nothing>()
    data class PasswordInvalid(val data: String? = null) : LoginViewState<Nothing>()
    data class FocusOnPassword(val data: String? = null) : LoginViewState<Nothing>()
    data class ReadyToLogin(val data: String? = null) : LoginViewState<Nothing>()
    data class InProgress(val data: String? = null) : LoginViewState<Nothing>()
    data class Success(val data: String) : LoginViewState<Nothing>()
    data class ApiError(val data: String? = null) : LoginViewState<Nothing>()
    data class Error(val throwable: Throwable? = null) : LoginViewState<Nothing>()
}