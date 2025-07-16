package com.hasheddev.studysmart.presentation.task

import com.hasheddev.studysmart.domain.model.Subject
import com.hasheddev.studysmart.util.Priority

data class TaskState(
    val title: String = "",
    val description: String = "",
    val dueDate: Long? = null,
    val isComplete: Boolean = false,
    val priority: Priority = Priority.LOW,
    val relatedSubject: String? = null,
    val subjects: List<Subject> = emptyList(),
    val subjectId: Int? = null,
    val currentTaskId: Int? = null,
)
