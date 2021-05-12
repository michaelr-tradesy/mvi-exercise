package com.example.mviexercise.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mviexercise.data.LoginLocalDataSourceImpl
import com.example.mviexercise.data.LoginRemoteDataSourceImpl
import com.example.mviexercise.data.LoginRepositoryImpl
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
@InternalCoroutinesApi
class LoginViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                emailValidator = EmailValidatorImpl(),
                loginRepository = LoginRepositoryImpl(
                    localDataSource =  LoginLocalDataSourceImpl(),
                    remoteDataSource = LoginRemoteDataSourceImpl()
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}