package com.example.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repositories.AuthRepository
import com.example.socialmedia.R
import com.example.utils.Constants.MAX_USERNAME_LENGTH
import com.example.utils.Constants.MIN_PASSWORD_LENGTH
import com.example.utils.Constants.MIN_USERNAME_LENGTH
import com.example.utils.Event
import com.example.utils.Resource
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main

) : ViewModel() {
    private val _registerStatus = MutableLiveData<Event<Resource<AuthResult>>>()
    val registerStatus: LiveData<Event<Resource<AuthResult>>> = _registerStatus
    fun register(
        @ApplicationContext applicationContext: Context,
        email: String,
        username: String,
        password: String,
        repeatedPassword: String
    ) {
        val error = if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            applicationContext.getString(R.string.error_input_empty)
        } else if (password != repeatedPassword) {
            applicationContext.getString(R.string.error_incorrectly_repeated_password)
        } else if (username.length < MIN_USERNAME_LENGTH) {
            applicationContext.getString(R.string.error_username_too_short, MIN_USERNAME_LENGTH)
        } else if (username.length > MAX_USERNAME_LENGTH) {
            applicationContext.getString(R.string.error_username_too_long, MAX_USERNAME_LENGTH)
        } else if (password.length < MIN_PASSWORD_LENGTH) {
            applicationContext.getString(R.string.error_password_too_short)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            applicationContext.getString(R.string.error_not_a_valid_email)
        } else null

        error?.let {
            _registerStatus.postValue(Event(Resource.Error(it)))
            return
        }
        _registerStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch {
            val result = repository.register(email, username, password)
            _registerStatus.postValue(Event(result))
        }
    }

}