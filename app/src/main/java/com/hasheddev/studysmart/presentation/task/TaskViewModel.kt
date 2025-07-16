package com.hasheddev.studysmart.presentation.task

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hasheddev.studysmart.domain.model.Task
import com.hasheddev.studysmart.domain.repository.SubjectRepository
import com.hasheddev.studysmart.domain.repository.TaskRepository
import com.hasheddev.studysmart.presentation.navArgs
import com.hasheddev.studysmart.util.Priority
import com.hasheddev.studysmart.util.SnackBarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val subjetRepository: SubjectRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val navArgs: TaskScreenNavArgs = savedStateHandle.navArgs()
    private val _state = MutableStateFlow(TaskState())
    val state = combine(
        _state,
        subjetRepository.getAllSubjects()
    ){ state, subjects ->
        state.copy(subjects = subjects)
    }.onStart {
        fetchTask()
        fetchSubject()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskState()
    )

    private val _snackBarEvent = MutableSharedFlow<SnackBarEvent>()
    val snackBarEvent  = _snackBarEvent.asSharedFlow()

    fun onEvent(event: TaskEvent) {
        when(event) {
            is TaskEvent.OnDescriptionChange -> {
                _state.update {
                    it.copy(description = event.description)
                }
            }
            is TaskEvent.OnDateChange -> {
                _state.update {
                    it.copy(dueDate = event.millis)
                }
            }
            TaskEvent.OnIsCompleteChange -> {
                _state.update {
                    it.copy(isComplete = !state.value.isComplete)
                }
            }
            is TaskEvent.OnPriorityChange -> {
                _state.update {
                    it.copy(priority = event.priority)
                }
            }
            is TaskEvent.OnRelatedSubjectChange -> {
                _state.update {
                    it.copy(
                        relatedSubject = event.subject.name,
                        subjectId = event.subject.subjectId
                    )
                }
            }
            is TaskEvent.OnTitleChange -> {
                _state.update {
                    it.copy(title = event.title)
                }
            }
            TaskEvent.SaveTask -> { saveTask() }
            TaskEvent.DeleteTask -> { deleteTask() }
        }
    }

    private fun deleteTask() {
        viewModelScope.launch {
            try {
                _state.value.currentTaskId?.let {
                    taskRepository.deleteTask(it)
                    _snackBarEvent.emit(SnackBarEvent.NavigateUp)
                } ?: _snackBarEvent.emit(
                    SnackBarEvent.ShowSnackBar(message = "No task to delete")
                )
            } catch (e: Exception) {
                SnackBarEvent.ShowSnackBar(
                    message = "Task deletion Failed!! ${e.message}",
                    SnackbarDuration.Long
                )
            }
        }
    }

    private fun saveTask() {
        viewModelScope.launch {
            try {
                val state = _state.value
                if (state.relatedSubject == null || state.subjectId == null) {
                    _snackBarEvent.emit(
                        SnackBarEvent.ShowSnackBar(message = "Please select subject related to task")
                    )
                    return@launch
                }
                taskRepository.upsertTask(
                    Task(
                        title = state.title,
                        description = state.description,
                        dueDate = state.dueDate ?: Instant.now().toEpochMilli(),
                        relatedSubject = state.relatedSubject,
                        priority = state.priority.value,
                        isComplete = state.isComplete,
                        subjectId = state.subjectId,
                        taskId = state.currentTaskId
                    )
                )
                _snackBarEvent.emit(
                    SnackBarEvent.ShowSnackBar(message = "Task saved successfully")
                )
                delay(250)
                _snackBarEvent.emit(
                    SnackBarEvent.NavigateUp
                )
            } catch (e:Exception) {
                _snackBarEvent.emit(
                    SnackBarEvent.ShowSnackBar(message = "Couldn't save Task $e")
                )
            }
        }
    }

    private fun fetchTask() {
        viewModelScope.launch {
            navArgs.taskId?.let { id ->
                val task = taskRepository.getTaskById(id)
                task?.let {
                    _state.update {
                        it.copy(
                            title = task.title,
                            description = task.description,
                            dueDate = task.dueDate,
                            relatedSubject = task.relatedSubject,
                            priority = Priority.fromInt(task.priority),
                            isComplete = task.isComplete,
                            subjectId = task.subjectId,
                            currentTaskId = task.taskId
                        )
                    }
                }
            }
        }
    }

    private fun fetchSubject() {
        viewModelScope.launch {
            navArgs.subjectId?.let { id ->
                subjetRepository.getSubjectById(id)?.let {  subject ->
                    _state.update {
                        it.copy(
                            subjectId = subject.subjectId,
                            relatedSubject = subject.name
                        )
                    }
                }
            }
        }
    }
}