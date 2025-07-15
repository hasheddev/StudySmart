package com.hasheddev.studysmart.presentation.dashboard

import androidx.compose.ui.graphics.Color
import com.hasheddev.studysmart.domain.model.Session
import com.hasheddev.studysmart.domain.model.Task

sealed interface DashBoardEvent {
    data object DeleteSession: DashBoardEvent

    data object  SaveSubject: DashBoardEvent

    data class OnDeleteSession(val session: Session): DashBoardEvent

    data class OnSubjectCardColorChange(val color: List<Color>): DashBoardEvent

    data class OnSubjectNameChange(val name: String): DashBoardEvent

    data class OnTaskCompleteChanged(val task: Task): DashBoardEvent

    data class OnTagetStudyHoursChange(val hours: String): DashBoardEvent
}