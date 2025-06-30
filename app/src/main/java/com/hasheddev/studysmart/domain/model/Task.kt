package com.hasheddev.studysmart.domain.model

data class Task(
    val taskId: Int,
    val subjectId: Int,
    val title: String,
    val description: String,
    val dueDate: Long,
    val priority: Int,
    val relatedSubject: String,
    val isComplete: Boolean
)
