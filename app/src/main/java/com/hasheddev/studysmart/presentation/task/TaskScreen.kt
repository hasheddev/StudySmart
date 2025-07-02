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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import com.hasheddev.studysmart.presentation.components.DeleteDialogue
import com.hasheddev.studysmart.presentation.components.SubjectListBottomSheet
import com.hasheddev.studysmart.presentation.components.TaskCheckBox
import com.hasheddev.studysmart.presentation.components.TaskDatePicker
import com.hasheddev.studysmart.presentation.theme.Red
import com.hasheddev.studysmart.subjects
import com.hasheddev.studysmart.util.Priority
import com.hasheddev.studysmart.util.dateMillisToString
import kotlinx.coroutines.launch
import java.time.Instant

const val bodyText = "Are you sure you want to delete this task?" +
        "This action cannot be undone"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen() {

    var  title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var  titleError by remember { mutableStateOf<String?>(null) }

    var isDeleteDialogueOpen by rememberSaveable { mutableStateOf(false) }
    var isDateDialogueOpen by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    var isBottomSheetOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    titleError = when{
        title.isBlank() -> "Please enter task title"
        title.length < 4 -> "Task title is too short"
        title.length > 35 -> "TAsk title is too long"
        else -> null
    }

    DeleteDialogue(
        isOpen = isDeleteDialogueOpen,
        title = "Delete Task",
        bodyText = bodyText,
        onDismissRequest = { isDeleteDialogueOpen = false },
        onConfirmClicked = { isDeleteDialogueOpen = false }
    )

    TaskDatePicker(
        state = datePickerState,
        isOpen = isDateDialogueOpen,
        onDismissRequest = { isDateDialogueOpen = false },
        onConfirmClicked = {
            isDateDialogueOpen = false
        }
    )

    SubjectListBottomSheet(
        sheetState = sheetState,
        isOpen = isBottomSheetOpen,
        subjects = subjects,
        onSubjectClicked = {
            scope.launch {
                sheetState.hide()
            }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    isBottomSheetOpen = false
                }
            }
        },
        onDismissRequest = {
            isBottomSheetOpen = false
        }
    )

    Scaffold(
        topBar = {
            TaskScreenTopBar(
                taskExists = true,
                isComplete = false,
                checkBoxBorderColor = Red,
                onBackButtonClick = {},
                onDeleteButtonClick = { isDeleteDialogueOpen = true },
                onCheckBoxClick = {}
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
                value = title,
                onValueChange = { title = it },
                label = { Text(text = "Title") },
                singleLine = true,
                isError = title.isNotBlank() && titleError != null,
                supportingText = {Text(text = titleError ?: "")}
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = description,
                onValueChange = { description = it },
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
                        borderColor =  if (priority == Priority.MEDIUM) Color.White else Color.Transparent,
                        labelColor =  if (priority == Priority.MEDIUM) Color.White else Color.White.copy(alpha = 0.7f),
                        onClick = {},
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
                Text(
                    text = "English",
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
                onClick = {},
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