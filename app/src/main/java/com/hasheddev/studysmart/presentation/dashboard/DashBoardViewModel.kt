package com.hasheddev.studysmart.presentation.dashboard

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasheddev.studysmart.domain.model.Subject
import com.hasheddev.studysmart.domain.repository.SessionRepository
import com.hasheddev.studysmart.domain.repository.SubjectRepository
import com.hasheddev.studysmart.domain.repository.TaskRepository
import com.hasheddev.studysmart.util.SnackBarEvent
import com.hasheddev.studysmart.util.toHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashBoardViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    sessionRepository: SessionRepository,
    taskRepository: TaskRepository
): ViewModel()  {
    private val _state = MutableStateFlow(DashBoardState())

    val dashBoardState =  combine(
        _state,
        subjectRepository.getTotalSubjectCount(),
        subjectRepository.getTotalGoalHours(),
        subjectRepository.getAllSubjects(),
        sessionRepository.getTotalSessionDuration()
    ) { state, subjectCount, targetStudyHours, subjects, totalSessionDuration ->
        state.copy(
            totalSubjectCount = subjectCount,
            totalTargetStudyHours = targetStudyHours,
            subjects = subjects,
            totalHoursStudied = totalSessionDuration.toHours()
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DashBoardState()
    )

    val tasks = taskRepository.getAllUpcomingTasks()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val recentSessions = sessionRepository.getRecentFiveSessions()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _snackBarEvent = MutableSharedFlow<SnackBarEvent>()
    val snackBarEvent  = _snackBarEvent.asSharedFlow()

    fun onEvent(event: DashBoardEvent) {
        when(event) {
            DashBoardEvent.DeleteSession -> deleteSubject()
            is DashBoardEvent.OnDeleteSession -> {
                _state.update { it.copy(session = event.session) }
            }
            is DashBoardEvent.OnSubjectCardColorChange ->{
                _state.update { it.copy(subjectCardColors = event.color) }
            }
            is DashBoardEvent.OnSubjectNameChange -> {
                _state.update { it.copy(subjectName = event.name) }
            }
            is DashBoardEvent.OnTagetStudyHoursChange -> {
                _state.update { it.copy(targetStudyHours = event.hours) }
            }
            is DashBoardEvent.OnTaskCompleteChanged -> {}
            DashBoardEvent.SaveSubject -> {
                saveSubject()
            }
        }
    }

    private fun deleteSubject() {

    }

    private fun saveSubject() {
        viewModelScope.launch {
            try {
                subjectRepository.upsertSubject(
                    Subject(
                        name = dashBoardState.value.subjectName,
                        goalHours = dashBoardState.value.targetStudyHours.toFloatOrNull() ?: 1f,
                        colors = dashBoardState.value.subjectCardColors.map { it.toArgb() }
                    )
                )
                _state.update {
                    it.copy(
                        subjectName = "",
                        targetStudyHours = "",
                        subjectCardColors = Subject.subjectCardColors.random()
                    )
                }
                _snackBarEvent.emit(SnackBarEvent.ShowSnackBar("Subject saved successfully!"))
            } catch (e: Exception) {
                _snackBarEvent.emit(SnackBarEvent.ShowSnackBar(
                    "Couldn't save subject ${e.message}",
                    SnackbarDuration.Long
                ))
            }

        }
    }
}