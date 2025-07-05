package com.hasheddev.studysmart.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hasheddev.studysmart.domain.model.Session
import com.hasheddev.studysmart.domain.model.Subject
import com.hasheddev.studysmart.domain.model.Task

@Database(
    entities = [Subject::class, Session::class, Task::class],
    version = 1
)
@TypeConverters(ColorListConverter::class)
abstract class AppDataBase: RoomDatabase() {

    abstract fun subjectDao(): SubjectDao

    abstract fun taskDao(): TaskDao

    abstract fun sessionDao(): SessionDao
}