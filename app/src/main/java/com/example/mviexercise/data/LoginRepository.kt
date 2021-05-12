package com.example.mviexercise.data

import com.example.mviexercise.data.model.LoggedInUser

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

interface LoginRepository {
    fun login(username: String, password: String): Result<LoggedInUser>
    fun logout()
}

class LoginRepositoryImpl(val remoteDataSource: LoginRemoteDataSource,
                          val localDataSource: LoginLocalDataSource) : LoginRepository {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    override fun login(username: String, password: String): Result<LoggedInUser> {
        // handle login
        val result = remoteDataSource.login(username, password)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    override fun logout() {
        user = null
        remoteDataSource.logout()
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}