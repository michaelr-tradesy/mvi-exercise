package com.example.mviexercise.ui.login

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation.INFINITE
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.mviexercise.R
import com.example.mviexercise.databinding.ActivityLoginBinding
import com.example.mviexercise.scope.AppCoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

@InternalCoroutinesApi
class LoginActivity : AppCompatActivity() {

    private var job: Deferred<Unit?>? = null
    private val appCoroutineScope = AppCoroutineScope()
    private lateinit var lifeCycleOwner: LifecycleOwner
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private lateinit var username: AppCompatEditText
    private lateinit var password: AppCompatEditText
    private lateinit var login: AppCompatButton
    private lateinit var loading: ProgressBar
    private lateinit var userNameTextWatcher: AppTextWatcher
    private lateinit var passwordTextWatcher: AppTextWatcher


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)
        lifeCycleOwner = this@LoginActivity
        bindUI()
        observeState()
    }

    override fun onStart() {
        super.onStart()
        initUserName()
        initPassword()
    }

    override fun onDestroy() {
        job?.cancel()
        username.removeTextChangedListener(userNameTextWatcher)
        password.removeTextChangedListener(passwordTextWatcher)
        super.onDestroy()
    }

    private fun bindUI() {
        username = binding.username
        password = binding.password
        login = binding.login
        loading = binding.loading

        login.setThrottlingClickListener {
            lifecycleScope.launch {
                loginViewModel.intents.send(LoginIntent.LoginButtonClicked)
            }
        }
    }

    private fun initPassword() {
        val passwordEditorActionListener =
            TextView.OnEditorActionListener { _, actionId, keyEvent ->
                lifecycleScope.launch {
                    loginViewModel.intents.send(LoginIntent.FinishedProvidingPassword.values(actionId, keyEvent))
                }
                return@OnEditorActionListener true
            }
        passwordTextWatcher = AppTextWatcher(
            actionBlock = {
                lifecycleScope.launch {
                    loginViewModel.intents.send(LoginIntent.PasswordChanged.values(password.text.toString()))
                }
            }
        )
        password.addTextChangedListener(passwordTextWatcher)
        password.setOnEditorActionListener(passwordEditorActionListener)
    }

    private fun initUserName() {
        val userNameEditorActionListener =
            TextView.OnEditorActionListener { _, actionId, keyEvent ->
                lifecycleScope.launch {
                    loginViewModel.intents.send(LoginIntent.FinishedProvidingUserName.values(actionId, keyEvent))
                }
                return@OnEditorActionListener true
            }
        userNameTextWatcher = AppTextWatcher(
            actionBlock = {
                lifecycleScope.launch {
                    loginViewModel.intents.send(LoginIntent.UserNameChanged.values(username.text.toString()))
                }
            }
        )
        username.addTextChangedListener(userNameTextWatcher)
        username.setOnEditorActionListener(userNameEditorActionListener)
    }

    private fun observeState() {
        job = appCoroutineScope.async {
            loginViewModel.currentState.collect(object : FlowCollector<LoginViewState<*>> {
                override suspend fun emit(value: LoginViewState<*>) {
                    runOnUiThread {
                        println("value=[$value]")
                        when (value) {
                            is LoginViewState.Idle -> {
                                onIdle()
                            }
                            is LoginViewState.ReadyForUserName -> {
                                onReadyForUserName()
                            }
                            is LoginViewState.UserNameInvalid -> {
                                onUserNameInvalid()
                            }
                            is LoginViewState.FocusOnUserName -> {
                                onFocusOnUserName()
                            }
                            is LoginViewState.ReadyForPassword -> {
                                onReadyForPassword()
                            }
                            is LoginViewState.PasswordInvalid -> {
                                onPasswordInvalid()
                            }
                            is LoginViewState.FocusOnPassword -> {
                                onFocusOnPassword()
                            }
                            is LoginViewState.ReadyToLogin -> {
                                onReadyToLogin()
                            }
                            is LoginViewState.InProgress -> {
                                onInProgress()
                            }
                            is LoginViewState.Success -> {
                                onSuccess(value.data)
                            }
                            is LoginViewState.ApiError -> {
                                onError(value.data)
                            }
                            is LoginViewState.Error -> {
                                onError(value.throwable?.message)
                            }
                            else -> { }
                        }
                    }
                }
            })
        }
    }

    private fun onReadyForUserName() {
        clearUserNameError()
        disablePassword()
        disableLogin()
    }

    private fun onIdle() {
        enableUserName()
        disablePassword()
        disableLogin()
    }

    private fun onUserNameInvalid() {
        disablePassword()
        disableLogin()
        displayUserNameError()
    }

    private fun onFocusOnUserName() {
        username.requestFocus()
    }

    private fun displayUserNameError() {
        username.error = getString(R.string.invalid_username)
    }

    private fun clearUserNameError() {
        username.error = null
    }

    private fun onReadyForPassword() {
        enablePassword()
        clearUserNameError()
    }

    private fun onPasswordInvalid() {
        disableLogin()
        displayPasswordError()
    }

    private fun displayPasswordError() {
        password.error = getString(R.string.invalid_password)
    }

    private fun clearPasswordError() {
        password.error = null
    }

    private fun onFocusOnPassword() {
        password.requestFocus()
    }

    private fun onReadyToLogin() {
        enableLogin()
        clearPasswordError()
    }

    private fun onInProgress() {
        enableProgress()
        disableUserName()
        disablePassword()
        disableLogin()
    }

    private fun enableProgress() {
        loading.visibility = View.VISIBLE
        val animation = ProgressBarAnimation(loading, 0f, 1f)
        animation.duration = 2000
        animation.repeatMode = INFINITE
        loading.startAnimation(animation)
    }

    private fun enableUserName() {
        username.isEnabled = true
    }

    private fun enablePassword() {
        password.isEnabled = true
    }

    private fun enableLogin() {
        login.isEnabled = true
    }

    private fun disableProgress() {
        loading.visibility = View.GONE
    }

    private fun disableUserName() {
        username.isEnabled = false
    }

    private fun disablePassword() {
        password.isEnabled = false
    }

    private fun disableLogin() {
        login.isEnabled = false
    }

    private fun onError(message: String?) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
        disableProgress()
    }

    private fun onSuccess(displayName: String) {
        val welcome = getString(R.string.welcome)

        enableProgress()
        // TODO : initiate successful logged in experience
        Toast.makeText(applicationContext, "$welcome $displayName", Toast.LENGTH_LONG).show()

        setResult(Activity.RESULT_OK)

        //Complete and destroy login activity once successful
        finish()
    }
}
