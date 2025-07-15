package com.hasheddev.studysmart.data.repository

import com.hasheddev.studysmart.data.local.TaskDao
import com.hasheddev.studysmart.domain.model.Task
import com.hasheddev.studysmart.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor (
    private val taskDao: TaskDao
): TaskRepository {
    override suspend fun upsertTask(task: Task) {
        taskDao.upsertTask(task)
    }

    override suspend fun deleteTask(taskId: Int) {
        taskDao.deleteTask(taskId)
    }

    override suspend fun getTaskById(taskId: Int): Task? {
        return taskDao.getTaskById(taskId)
    }

    override fun getUpcomingTasksForSubject(subjectId: Int): Flow<List<Task>> {
        return taskDao.getTasksForSubject(subjectId).map { tasks ->
            tasks.filter { it.isComplete.not() } }
            .map { tasks -> tasks.sortTasks() }
    }

    override fun getCompletedTasksForSubject(subjectId: Int): Flow<List<Task>> {
        return taskDao.getTasksForSubject(subjectId).map { tasks ->
            tasks.filter { it.isComplete } }
            .map { tasks -> tasks.sortTasks() }
    }

    override fun getAllUpcomingTasks(): Flow<List<Task>> {
       return taskDao.getAllTasks().map { tasks ->
           tasks.filter { it.isComplete.not() } }
           .map { tasks -> tasks.sortTasks() }
    }
}


private fun List<Task>.sortTasks(): List<Task> {
    return this.sortedWith(
        compareBy<Task> { it.dueDate }.thenByDescending { it.priority }
    )
}