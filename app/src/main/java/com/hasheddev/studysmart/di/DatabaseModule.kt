package com.hasheddev.studysmart.di

import android.app.Application
import androidx.room.Room
import com.hasheddev.studysmart.data.local.AppDataBase
import com.hasheddev.studysmart.data.local.SessionDao
import com.hasheddev.studysmart.data.local.SubjectDao
import com.hasheddev.studysmart.data.local.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule{

    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Application): AppDataBase {
        return Room
            .databaseBuilder(
                context,
                AppDataBase::class.java,
                "StudySmart.db"
            ).build()
    }

    @Provides
    @Singleton
    fun providesSubjectDao(database: AppDataBase): SubjectDao = database.subjectDao()

    @Provides
    @Singleton
    fun providesSessionDao(database: AppDataBase): SessionDao = database.sessionDao()

    @Provides
    @Singleton
    fun providesTaskDao(database: AppDataBase): TaskDao = database.taskDao()
}