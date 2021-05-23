package com.example.di

import com.example.repositories.AuthRepository
import com.example.repositories.impl.AuthImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object AuthModule {

    @ActivityScoped
    @Provides
    fun provideAuthRepository() = AuthImpl() as AuthRepository
}