package com.example.utils

class Event<out T>(private val content: T) {
    var hashBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (!hashBeenHandled) {
            hashBeenHandled = true
            content
        } else null
    }

    fun peekContent() = content
}