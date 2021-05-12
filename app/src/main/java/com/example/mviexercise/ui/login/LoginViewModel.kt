package com.example.mviexercise.ui.login

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mviexercise.data.LoginRepository
import com.example.mviexercise.data.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

@InternalCoroutinesApi
class LoginViewModel(
    val currentState: MutableStateFlow<LoginViewState<*>> = MutableStateFlow(LoginViewState.Idle()),
    private val loginRepository: LoginRepository,
    private val emailValidator: EmailValidator,
    private val coroutineContext: CoroutineContext = Dispatchers.IO
) : ViewModel() {

    val intents: Channel<LoginIntent> = Channel(Channel.UNLIMITED)
    private var userName = ""
    private var password = ""

    init {
        handlerIntent()
    }

    private fun handlerIntent() {
        viewModelScope.launch {
            intents.consumeAsFlow().collect { userIntent ->
                when (userIntent) {
                    is LoginIntent.UserNameChanged -> {
                        onUserNameChanged(userIntent.data)
                    }
                    is LoginIntent.PasswordChanged -> {
                        onPasswordChanged(userIntent.data)
                    }
                    is LoginIntent.LoginButtonClicked -> {
                        login()
                    }
                    is LoginIntent.FinishedProvidingUserName -> {
                        onFinishedProvidingUserName(userIntent.actionId, userIntent.keyEvent)
                    }
                    is LoginIntent.FinishedProvidingPassword -> {
                        onFinishedProvidingPassword(userIntent.actionId, userIntent.keyEvent)
                    }
                }
            }
        }
    }

    private fun onUserNameChanged(userName: String) {
        viewModelScope.launch(coroutineContext) {
            try {
                this@LoginViewModel.userName = userName
                currentState.value = LoginViewState.UserNameInProgress()
                currentState.value = when {
                    emailValidator.isValid(userName) -> {
                        LoginViewState.ReadyForPassword()
                    }
                    userName.isEmpty() -> {
                        LoginViewState.ReadyForUserName()
                    }
                    else -> {
                        LoginViewState.UserNameInvalid()
                    }
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun onPasswordChanged(password: String) {
        viewModelScope.launch(coroutineContext) {
            try {
                this@LoginViewModel.password = password
                currentState.value = LoginViewState.PasswordInProgress()
                currentState.value = when {
                    isPasswordValid(password) -> {
                        LoginViewState.ReadyToLogin()
                    }
                    password.isEmpty() -> {
                        LoginViewState.ReadyForPassword()
                    }
                    else -> {
                        LoginViewState.PasswordInvalid()
                    }
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun onFinishedProvidingUserName(actionId: Int, keyEvent: KeyEvent?) {
        viewModelScope.launch(coroutineContext) {
            try {
                if (isUserDoneEditing(actionId, keyEvent)
                    && currentState.value is LoginViewState.ReadyForPassword
                ) {
                    currentState.value = LoginViewState.FocusOnPassword()
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun onFinishedProvidingPassword(actionId: Int, keyEvent: KeyEvent?) {
        viewModelScope.launch(coroutineContext) {
            try {
                if (wasGoPressed(actionId, keyEvent)
                    && currentState.value is LoginViewState.ReadyToLogin
                ) {
                    login()
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun login() {
        viewModelScope.launch(coroutineContext) {
            try {
                currentState.value = LoginViewState.InProgress()
                delay(5000)
                // can be launched in a separate asynchronous job
                val result = loginRepository.login(userName, password)

                if (result is Result.Success) {
                    currentState.value = LoginViewState.Success(result.data.displayName)
                } else {
                    currentState.value = LoginViewState.Error(Throwable("Log In Failed"))
                }
            } catch (e: Exception) {
                currentState.value =
                    LoginViewState.Error(Throwable("Log In Failed. Unexpected Error"))
            }
        }
    }

    private fun wasGoPressed(actionId: Int, keyEvent: KeyEvent?) =
        (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE
                || (keyEvent?.action == KeyEvent.ACTION_UP
                && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER))

    private fun isUserDoneEditing(actionId: Int, keyEvent: KeyEvent?) =
        (actionId == EditorInfo.IME_ACTION_NEXT
                || (keyEvent?.action == KeyEvent.ACTION_UP
                && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER))

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}