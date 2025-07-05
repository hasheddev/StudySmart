package com.hasheddev.studysmart.domain.repository

import com.hasheddev.studysmart.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun upsertTask(task: Task)

    suspend fun deleteTask(taskId: Int)

    suspend fun getTaskById(taskId: Int): Task?

    suspend fun getUpcomingTasksForSubject(subjectId: Int): Flow<List<Task>>

    suspend fun getCompletedTasksForSubject(subjectId: Int): Flow<List<Task>>

    suspend fun getAllUpcomingTasksForSubject(subjectId: Int): Flow<List<Task>>
}