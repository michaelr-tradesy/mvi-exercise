package com.example.mviexercise.ui.login

import android.view.KeyEvent
import java.io.Serializable

sealed class LoginIntent : Serializable {
    object UserNameChanged : LoginIntent() {
        var data = ""

        fun values(value: String): LoginIntent {
            this.data = value
            return this
        }
    }

    object PasswordChanged : LoginIntent() {
        var data = ""

        fun values(value: String): LoginIntent {
            this.data = value
            return this
        }
    }

    object LoginButtonClicked : LoginIntent() {
        var data = ""
    }

    object FinishedProvidingUserName : LoginIntent() {
        var actionId: Int = 0
        var keyEvent: KeyEvent? = null

        fun values(actionId: Int, keyEvent: KeyEvent?): LoginIntent {
            this.actionId = actionId
            this.keyEvent = keyEvent
            return this
        }
    }

    object FinishedProvidingPassword : LoginIntent() {
        var actionId: Int = 0
        var keyEvent: KeyEvent? = null

        fun values(actionId: Int, keyEvent: KeyEvent?): LoginIntent {
            this.actionId = actionId
            this.keyEvent = keyEvent
            return this
        }
    }
}