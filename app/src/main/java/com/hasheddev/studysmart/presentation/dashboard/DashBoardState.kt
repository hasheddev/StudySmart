package com.hasheddev.studysmart.presentation.dashboard

import androidx.compose.ui.graphics.Color
import com.hasheddev.studysmart.domain.model.Session
import com.hasheddev.studysmart.domain.model.Subject

data class DashBoardState(
    val totalSubjectCount: Int = 0,
    val totalHoursStudied: Float = 0f,
    val totalTargetStudyHours: Float = 0f,
    val subjects: List<Subject> = emptyList(),
    val subjectName: String = "",
    val targetStudyHours: String = "",
    val subjectCardColors: List<Color> = Subject.subjectCardColors.random(),
    val session: Session? = null
)
