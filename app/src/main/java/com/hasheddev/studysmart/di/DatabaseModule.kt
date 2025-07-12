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
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {


    @Provides
    @Singleton
    fun provideDatabase(
        application: Application
    ): AppDataBase {
        return Room
            .databaseBuilder(
                application,
                AppDataBase::class.java,
                "StudySmart.db"
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideSubjectDao(database: AppDataBase): SubjectDao {
        return database.subjectDao()
    }

    @Provides
    @Singleton
    fun provideTaskDaoDao(database: AppDataBase): TaskDao {
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun provideSessionDao(database: AppDataBase): SessionDao {
        return database.sessionDao()
    }
}