package com.hasheddev.studysmart.presentation.subject

import androidx.compose.ui.graphics.Color
import com.hasheddev.studysmart.domain.model.Session
import com.hasheddev.studysmart.domain.model.Task

sealed interface SubjectEvent {
    data object UpdateSubject: SubjectEvent

    data object DeleteSubject: SubjectEvent

    data object DeleteSession: SubjectEvent

    data object UpdateProgress: SubjectEvent

    data class OnTaskCompletedStateChange(val task: Task): SubjectEvent

    data class OnSubjectCardColorChange(val color: List<Color>): SubjectEvent

    data class OnSubjectNameChange(val name: String): SubjectEvent

    data class OnTargetStudyHourChange(val hours: String): SubjectEvent

    data class OnDeleteSessionClick(val session: Session): SubjectEvent
}