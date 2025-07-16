package com.hasheddev.studysmart.presentation.task

import com.hasheddev.studysmart.domain.model.Subject
import com.hasheddev.studysmart.util.Priority

sealed interface TaskEvent {

    data class OnTitleChange(val title: String): TaskEvent

    data class OnDescriptionChange(val description: String): TaskEvent

    data class OnDateChange(val millis: Long): TaskEvent

    data class OnPriorityChange(val priority: Priority): TaskEvent

    data class OnRelatedSubjectChange(val subject: Subject): TaskEvent

    data object OnIsCompleteChange: TaskEvent

    data object SaveTask: TaskEvent

    data object DeleteTask: TaskEvent
}