package com.hasheddev.studysmart.presentation.subject

import androidx.compose.ui.graphics.Color
import com.hasheddev.studysmart.domain.model.Session
import com.hasheddev.studysmart.domain.model.Subject
import com.hasheddev.studysmart.domain.model.Task

data class SubjectState(
    val currentSubjectId: Int? = null,
    val subjectName: String = "",
    val targetStudyHours: String = "",
    val hoursStudied: Float = 0f,
    val progress: Float = 0f,
    val subjectCardColors: List<Color> = Subject.subjectCardColors.random(),
    val recentSessions: List<Session> = emptyList(),
    val upcomingTasks: List<Task> = emptyList(),
    val completedTasks: List<Task> = emptyList(),
    val session: Session? = null,
)
