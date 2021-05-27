package com.example.di

import com.example.repositories.CommentRepository
import com.example.repositories.impl.*
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


    @Provides
    @ViewModelScoped
    fun providePostRepository(): PostsImpl {
        return PostsImpl()
    }

    @Provides
    @ViewModelScoped
    fun provideSearchUserRepository(): SearchUserImpl {
        return SearchUserImpl()
    }

    @Provides
    @ViewModelScoped
    fun provideUserRepository(): UsersImpl {
        return UsersImpl()
    }

    @Provides
    @ViewModelScoped
    fun provideCommentRepository(): CommentImpl {
        return CommentImpl()
    }

    @Provides
    @ViewModelScoped
    fun provideUpdateProfileRepository(): ProfileUpdateImpl {
        return ProfileUpdateImpl()
    }
}