package com.example.mviexercise.data

import com.example.mviexercise.data.model.LoggedInUser
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
interface LoginLocalDataSource {
    fun login(username: String, password: String): Result<LoggedInUser>
    fun logout()
}

class LoginLocalDataSourceImpl: LoginLocalDataSource {

    override fun login(username: String, password: String): Result<LoggedInUser> {
        return try {
            // TODO: handle loggedInUser authentication
            val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe")
            Result.Success(fakeUser)
        } catch (e: Throwable) {
            Result.Error(IOException("Error logging in", e))
        }
    }

    override fun logout() {
        // TODO: revoke authentication
    }
}