package com.naozumi.izinboss.model.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

object FormValidator {

    fun validateEmail(email: Flow<String>): Flow<Boolean> {
        return email.map {
            android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()
        }
    }

    fun validatePassword(password: Flow<String>): Flow<Boolean> {
        return password.map {
            it.isNotEmpty() && it.length >= 8
        }
    }

    fun validateConfirmPassword(confirmPassword: Flow<String>): Flow<Boolean> {
        return confirmPassword.map {
            it.isNotEmpty() && it.length >= 8
        }
    }

    fun validateForm(confirmPassword: Flow<String>, email: Flow<String>, password: Flow<String>): Flow<Boolean> {
        return combine(validateEmail(email), validatePassword(password), validateConfirmPassword(confirmPassword)) { confirmPasswordIsValid, emailIsValid, passwordIsValid ->
            emailIsValid && passwordIsValid && confirmPasswordIsValid
        }
    }
}