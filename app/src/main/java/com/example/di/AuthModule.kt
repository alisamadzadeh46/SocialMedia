package com.example.di

import com.example.repositories.impl.AuthImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object AuthModule {

    @Provides
    @ViewModelScoped //  this is new , old version hilt :@ActivityScoped
    fun provideAuthRepository(): AuthImpl {
        return AuthImpl()
    }

}