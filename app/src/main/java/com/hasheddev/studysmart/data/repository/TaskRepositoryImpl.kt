package com.hasheddev.studysmart.data.repository

import com.hasheddev.studysmart.data.local.TaskDao
import com.hasheddev.studysmart.domain.model.Task
import com.hasheddev.studysmart.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor (
    private val taskDao: TaskDao
): TaskRepository {
    override suspend fun upsertTask(task: Task) {
        taskDao.upsertTask(task)
    }

    override suspend fun deleteTask(taskId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getTaskById(taskId: Int): Task? {
        TODO("Not yet implemented")
    }

    override suspend fun getUpcomingTasksForSubject(subjectId: Int): Flow<List<Task>> {
        TODO("Not yet implemented")
    }

    override suspend fun getCompletedTasksForSubject(subjectId: Int): Flow<List<Task>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllUpcomingTasksForSubject(subjectId: Int): Flow<List<Task>> {
        TODO("Not yet implemented")
    }
}