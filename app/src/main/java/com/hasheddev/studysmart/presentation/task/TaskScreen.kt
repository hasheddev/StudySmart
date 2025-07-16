package com.hasheddev.studysmart.presentation.task

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hasheddev.studysmart.presentation.components.DeleteDialogue
import com.hasheddev.studysmart.presentation.components.SubjectListBottomSheet
import com.hasheddev.studysmart.presentation.components.TaskCheckBox
import com.hasheddev.studysmart.presentation.components.TaskDatePicker
import com.hasheddev.studysmart.util.Constants.TASK_DELETION_TEXT
import com.hasheddev.studysmart.util.Priority
import com.hasheddev.studysmart.util.SnackBarEvent
import com.hasheddev.studysmart.util.dateMillisToString
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Instant

data class TaskScreenNavArgs(
    val subjectId: Int?,
    val taskId: Int?
)


@Destination(navArgsDelegate = TaskScreenNavArgs::class)
@Composable
fun TaskScreenRoute(
    navigator: DestinationsNavigator
) {
    val viewModel: TaskViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    TaskScreen(
        state = state,
        snackBarEvent = viewModel.snackBarEvent,
        onEvent = viewModel::onEvent,
        onBackButtonClick = { navigator.navigateUp() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskScreen(
    state: TaskState,
    snackBarEvent: SharedFlow<SnackBarEvent>,
    onEvent: (TaskEvent) -> Unit,
    onBackButtonClick: () -> Unit
) {

    var  titleError by remember { mutableStateOf<String?>(null) }

    var isDeleteDialogueOpen by rememberSaveable { mutableStateOf(false) }
    var isDateDialogueOpen by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    var isBottomSheetOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val snackBarState = remember {
        SnackbarHostState()
    }

    titleError = when{
        state.title.isBlank() -> "Please enter task title"
        state.title.length < 4 -> "Task title is too short"
        state.title.length > 35 -> "TAsk title is too long"
        else -> null
    }

    LaunchedEffect(true) {
        snackBarEvent.collectLatest { event ->
            when(event) {
                is SnackBarEvent.ShowSnackBar -> {
                    snackBarState.showSnackbar(
                        message = event.message,
                        duration = event.duration
                    )
                }

                SnackBarEvent.NavigateUp -> onBackButtonClick()
            }
        }
    }

    DeleteDialogue(
        isOpen = isDeleteDialogueOpen,
        title = "Delete Task",
        bodyText = TASK_DELETION_TEXT,
        onDismissRequest = { isDeleteDialogueOpen = false },
        onConfirmClicked = {
            isDeleteDialogueOpen = false
            onEvent(TaskEvent.DeleteTask)
        }
    )

    TaskDatePicker(
        state = datePickerState,
        isOpen = isDateDialogueOpen,
        onDismissRequest = { isDateDialogueOpen = false },
        onConfirmClicked = {
            onEvent(TaskEvent.OnDateChange(millis = datePickerState.selectedDateMillis!!))
            isDateDialogueOpen = false
        }
    )

    SubjectListBottomSheet(
        sheetState = sheetState,
        isOpen = isBottomSheetOpen,
        subjects = state.subjects,
        onSubjectClicked = { subject ->
            scope.launch {
                sheetState.hide()
            }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    isBottomSheetOpen = false
                }
                onEvent(TaskEvent.OnRelatedSubjectChange(subject))
            }
        },
        onDismissRequest = {
            isBottomSheetOpen = false
        }
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarState) },
        topBar = {
            TaskScreenTopBar(
                taskExists = state.currentTaskId != null,
                isComplete = state.isComplete,
                checkBoxBorderColor = state.priority.color,
                onBackButtonClick = onBackButtonClick,
                onDeleteButtonClick = { isDeleteDialogueOpen = true },
                onCheckBoxClick = { onEvent(TaskEvent.OnIsCompleteChange) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp)
                .verticalScroll(state = rememberScrollState())
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.title,
                onValueChange = { onEvent(TaskEvent.OnTitleChange(it)) },
                label = { Text(text = "Title") },
                singleLine = true,
                isError = state.title.isNotBlank() && titleError != null,
                supportingText = {Text(text = titleError ?: "")}
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.description,
                onValueChange = { onEvent(TaskEvent.OnDescriptionChange(it)) },
                label = { Text(text = "Description") },
            )
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Due Date",
                style = MaterialTheme.typography.bodySmall
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = datePickerState.selectedDateMillis.dateMillisToString(),
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(onClick = { isDateDialogueOpen = true }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select Due Date"
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Priority",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Priority.entries.forEach { priority ->
                    PriorityButton(
                        label = priority.title,
                        backgroundColor = priority.color,
                        borderColor =  if (priority == state.priority) Color.White else Color.Transparent,
                        labelColor =  if (priority == state.priority) Color.White else Color.White.copy(alpha = 0.7f),
                        onClick = { onEvent(TaskEvent.OnPriorityChange(priority)) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Related to subject",
                style = MaterialTheme.typography.bodySmall
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val firstSubject = state.subjects.firstOrNull()?.name ?: ""
                Text(
                    text = state.relatedSubject ?: firstSubject,
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(onClick = { isBottomSheetOpen = true }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Subject"
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                enabled = titleError == null,
                onClick = { onEvent(TaskEvent.SaveTask) },
                modifier = Modifier.fillMaxWidth().padding(12.dp)
            ) {
                Text(text = "Save")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskScreenTopBar(
    taskExists: Boolean,
    isComplete: Boolean,
    checkBoxBorderColor: Color,
    onBackButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    onCheckBoxClick: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackButtonClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "navigate Back"
                )
            }
        },
        title = { Text(
            text = "Task",
            style = MaterialTheme.typography.headlineSmall
        ) },
        modifier = Modifier,
        actions = {
            if (taskExists) {
                TaskCheckBox(
                    isComplete = isComplete,
                    borderColor = checkBoxBorderColor,
                    onCheckBoxClicked = onCheckBoxClick
                )
                IconButton(onClick = onDeleteButtonClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "DeleteTask"
                    )
                }
            }
        },
    )
}

@Composable
private fun PriorityButton(
    label: String,
    backgroundColor: Color,
    borderColor: Color,
    labelColor: Color,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(5.dp)
            .border(1.dp, borderColor, RoundedCornerShape(5.dp))
            .padding(5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = labelColor)
    }
}