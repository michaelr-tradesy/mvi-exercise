package com.example.mviexercise.ui.login

import java.util.regex.Pattern

interface EmailValidator {
    fun isValid(target: String?): Boolean
}

class EmailValidatorImpl: EmailValidator {
    private val emailAddressPattern: Pattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    override fun isValid(target: String?) : Boolean {
        var output = false
        target?.let {
            output = it.isNotEmpty() && emailAddressPattern.matcher(it).matches()
        }
        return output
    }
}