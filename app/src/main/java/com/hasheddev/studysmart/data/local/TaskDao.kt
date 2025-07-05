package com.hasheddev.studysmart.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.hasheddev.studysmart.domain.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Upsert
    suspend fun upsertTask(task: Task)

    @Query("DELETE FROM Task WHERE taskId = :taskId")
    suspend fun deleteTask(taskId: Int)

    @Query("DELETE FROM Task WHERE subjectId = :subjectId")
    suspend fun deleteTaskBySubjectById(subjectId: Int)

    @Query("SELECt * FROM Task WHERE taskId = :taskId")
    suspend fun getTaskById(taskId: Int): Task?

    @Query("SELECT * FROM Task WHERE subjectId = :subjectId")
    suspend fun getTasksForSubject(subjectId: Int): Flow<List<Task>>

    @Query("SELECT * FROM Task")
    suspend fun getAllTasks(): Flow<List<Task>>

}