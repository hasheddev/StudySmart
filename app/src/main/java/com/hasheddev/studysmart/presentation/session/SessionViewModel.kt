package com.hasheddev.studysmart.presentation.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasheddev.studysmart.domain.model.Session
import com.hasheddev.studysmart.domain.repository.SessionRepository
import com.hasheddev.studysmart.domain.repository.SubjectRepository
import com.hasheddev.studysmart.util.SnackBarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    subjectRepository: SubjectRepository
): ViewModel()  {

    private val _state = MutableStateFlow(SessionState())

    val state = combine(
        _state,
        subjectRepository.getAllSubjects(),
        sessionRepository.getAllSessions()
    ) { state, subjects, sessions ->
        state.copy(
            sessions = sessions,
            subjects = subjects
        )
    }.onStart {

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SessionState()
    )

    private val _snackBarEvent = MutableSharedFlow<SnackBarEvent>()
    val snackBarEvent  = _snackBarEvent.asSharedFlow()

    fun onEvent(event: SessionEvent) {
        when(event) {
            SessionEvent.NotifyRelatedSubjectSelection -> {
                viewModelScope.launch {
                    if(state.value.relatedSubject == null || state.value.subjectId == null) {
                        _snackBarEvent.emit(
                            SnackBarEvent.ShowSnackBar(
                                message = "Please select subject related to this session"
                            )
                        )
                    }
                }
            }
            SessionEvent.DeleteSession -> {  deleteSession() }
            is SessionEvent.OnDeleteSessionClick -> {
                _state.update {
                    it.copy(
                        session = event.session,
                    )
                }
            }
            is SessionEvent.OnRelatedSubjectChange -> {
                _state.update {
                    it.copy(
                        relatedSubject = event.subject.name,
                        subjectId = event.subject.subjectId
                    )
                }
            }
            is SessionEvent.OnSaveSession -> { saveSession(event.duration) }
            is SessionEvent.OnUpdateSubjectIdAndRelatedSubject -> {
                _state.update {
                    it.copy(
                        relatedSubject = event.relatedSubject,
                        subjectId = event.subjectId
                    )
                }
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

    private fun saveSession(duration: Long) {
        viewModelScope.launch {
            if (duration < 60) {
                _snackBarEvent.emit(
                    SnackBarEvent.ShowSnackBar(message = "Single Session cannot be less than 1 minute")
                )
                return@launch
            }
            try {
                val state = state.value
                if(state.relatedSubject == null || state.subjectId == null) {
                    _snackBarEvent.emit(
                        SnackBarEvent.ShowSnackBar(message = "Please select subject related to this session")
                    )
                    return@launch
                }
                sessionRepository.insertSession(
                    Session(
                        sessionSubjectId = state.subjectId,
                        relatedSubject = state.relatedSubject,
                        date = Instant.now().toEpochMilli(),
                        duration = duration
                    )
                )

                _snackBarEvent.emit(
                    SnackBarEvent.ShowSnackBar(message = "Session saved successfully")
                )
            } catch (e: Exception) {
                _snackBarEvent.emit(
                    SnackBarEvent.ShowSnackBar(message = "Failed to save session ${e.message}")
                )
            }
        }
    }
}