package com.hasheddev.studysmart.presentation.subject

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasheddev.studysmart.domain.model.Subject
import com.hasheddev.studysmart.domain.model.Task
import com.hasheddev.studysmart.domain.repository.SessionRepository
import com.hasheddev.studysmart.domain.repository.SubjectRepository
import com.hasheddev.studysmart.domain.repository.TaskRepository
import com.hasheddev.studysmart.presentation.navArgs
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
class SubjectViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val sessionRepository: SessionRepository,
    private val taskRepository: TaskRepository,
    savedStateHandle: SavedStateHandle
): ViewModel()  {
    private val navArgs: SubjectScreenNavArgs = savedStateHandle.navArgs()
    private val _state = MutableStateFlow(SubjectState())

    val state = combine(
        _state,
        taskRepository.getUpcomingTasksForSubject(navArgs.subjectId),
        taskRepository.getCompletedTasksForSubject(navArgs.subjectId),
        sessionRepository.getRecentTenSessionsForSubject(navArgs.subjectId),
        sessionRepository.getTotalSessionDurationForSubject(navArgs.subjectId)
    ) { state, upcomingTasks, completedTasks, recentSubjectSessions, totalSubjectSessionDuration ->
        state.copy(
            upcomingTasks = upcomingTasks,
            completedTasks = completedTasks,
            recentSessions = recentSubjectSessions,
            hoursStudied =  totalSubjectSessionDuration.toHours()
        )
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SubjectState()
        )

    private val _snackBarEvent = MutableSharedFlow<SnackBarEvent>()
    val snackBarEvent  = _snackBarEvent.asSharedFlow()

    init {
        fetchSubject()
    }

    fun onEvent(subjectEvent: SubjectEvent) {
        when(subjectEvent) {
            SubjectEvent.DeleteSession -> { deleteSession() }
            is SubjectEvent.DeleteSubject -> { deleteSubject() }
            is SubjectEvent.OnDeleteSessionClick -> {
                _state.update {
                    it.copy(
                        session = subjectEvent.session,
                    )
                }
            }
            is SubjectEvent.OnSubjectCardColorChange -> {
                _state.update {
                    it.copy(
                        subjectCardColors = subjectEvent.color
                    )
                }
            }
            is SubjectEvent.OnSubjectNameChange -> {
                _state.update {
                    it.copy(
                        subjectName = subjectEvent.name
                    )
                }
            }
            is SubjectEvent.OnTargetStudyHourChange -> {
                _state.update {
                    it.copy(
                        targetStudyHours = subjectEvent.hours
                    )
                }
            }
            is SubjectEvent.OnTaskCompletedStateChange -> {
                updateTask(subjectEvent.task)
            }
            SubjectEvent.UpdateSubject -> { updateSubject() }
            SubjectEvent.UpdateProgress -> {
                val targetStudyHours = state.value.targetStudyHours.toFloatOrNull() ?: 1f
                _state.update {
                    it.copy(
                       progress = (state.value.hoursStudied / targetStudyHours).coerceIn(0f, 1f)
                    )
                }
            }
        }
    }

    private fun updateSubject() {
        viewModelScope.launch {
            try {
                subjectRepository.upsertSubject(
                    subject = Subject(
                        subjectId = state.value.currentSubjectId,
                        name = state.value.subjectName,
                        goalHours = state.value.targetStudyHours.toFloatOrNull() ?: 1f,
                        colors = state.value.subjectCardColors.map { it.toArgb() }
                    )
                )
                _snackBarEvent.emit(
                    SnackBarEvent.ShowSnackBar(message = "Subject updated successfully")
                )
            } catch (e: Exception) {
                SnackBarEvent.ShowSnackBar(
                    message = "Subject update Failed!! ${e.message}",
                    SnackbarDuration.Long
                )
            }
        }
    }

    private fun deleteSubject() {
        viewModelScope.launch {
            try {
                _state.value.currentSubjectId?.let {
                    subjectRepository.deleteSubject(subjectId = it)
                    _snackBarEvent.emit(SnackBarEvent.NavigateUp)
                } ?: _snackBarEvent.emit(
                    SnackBarEvent.ShowSnackBar(message = "No subject to delete")
                )
            } catch (e: Exception) {
                SnackBarEvent.ShowSnackBar(
                    message = "Subject deletion Failed!! ${e.message}",
                    SnackbarDuration.Long
                )
            }
        }
    }

    private fun fetchSubject() {
        viewModelScope.launch {
            subjectRepository.getSubjectById(
                navArgs.subjectId
            )?.let { subject ->
                _state.update {
                    it.copy(
                        subjectName = subject.name,
                        targetStudyHours = subject.goalHours.toString(),
                        subjectCardColors = subject.colors.map { color -> Color(color) },
                        currentSubjectId = subject.subjectId
                    )
                }
            }
        }
    }

    private fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.upsertTask(
                    task = task.copy(isComplete = !task.isComplete)
                )
                val status = if (task.isComplete) "upcoming" else "completed"
                val message = "Task saved in" + status +  "tasks"
                _snackBarEvent.emit(
                    SnackBarEvent.ShowSnackBar(message = message)
                )
            } catch (e: Exception) {
                _snackBarEvent.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Couldn't update Task ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun deleteSession() {
        viewModelScope.launch {
            try {
                state.value.session?.let {
                    sessionRepository.deleteSession(it)
                    _snackBarEvent.emit(
                        SnackBarEvent.ShowSnackBar(message = "Session deleted successfully")
                    )
                } ?: _snackBarEvent.emit(
                    SnackBarEvent.ShowSnackBar(message = "No session to delete")
                )
            } catch (e: Exception) {
                _snackBarEvent.emit(
                    SnackBarEvent.ShowSnackBar(message = "Failed to delete session ${e.message}")
                )
            }
        }
    }
}