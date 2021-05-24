package com.example.di

import com.example.repositories.impl.CreatePostImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object MainModule {

    @Provides
    @ViewModelScoped //  this is new , old version hilt :@ActivityScoped
    fun provideCreatePostRepository(): CreatePostImpl {
        return CreatePostImpl()
    }
}