package de.jbamberger.fhgapp.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class BindingsModule {

    @Binds
    @Singleton
    internal abstract fun bindRepository(impl: RepositoryImpl): Repository
}