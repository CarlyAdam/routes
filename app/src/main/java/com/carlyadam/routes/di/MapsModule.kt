package com.carlyadam.routes.di

import android.content.Context
import com.carlyadam.routes.data.api.ApiService
import com.carlyadam.routes.repository.MapsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
object MapsModule {

    @Provides
    @ActivityRetainedScoped
    fun githubRepository(
        apiService: ApiService,
        @ApplicationContext context: Context
    ): MapsRepository {
        return MapsRepository(apiService, context)
    }
}