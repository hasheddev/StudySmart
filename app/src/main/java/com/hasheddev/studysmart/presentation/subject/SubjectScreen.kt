package com.hasheddev.studysmart.presentation.subject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hasheddev.studysmart.domain.model.Subject
import com.hasheddev.studysmart.emptyListText
import com.hasheddev.studysmart.emptySessionText
import com.hasheddev.studysmart.presentation.components.AddSubjectDialogue
import com.hasheddev.studysmart.presentation.components.CountCard
import com.hasheddev.studysmart.presentation.components.DeleteDialogue
import com.hasheddev.studysmart.presentation.components.studySessionsList
import com.hasheddev.studysmart.presentation.components.taskList
import com.hasheddev.studysmart.sessions
import com.hasheddev.studysmart.taskList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectScreen() {

    val listState = rememberLazyListState()

    val isFabExpanded by remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0 }
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var isDeleteSubjectDialogueOpen by rememberSaveable { mutableStateOf(false) }
    var isEditSubjectDialogueOpen by rememberSaveable { mutableStateOf(false) }
    var isDeleteSessionDialogueOpen by rememberSaveable { mutableStateOf(false) }
    var subjectName by remember { mutableStateOf("") }
    var targetStudyHours by remember { mutableStateOf("") }
    var selectedColors by remember { mutableStateOf(Subject.subjectCardColors.random()) }


    AddSubjectDialogue(
        isOpen = isEditSubjectDialogueOpen,
        onDismissRequest = { isEditSubjectDialogueOpen = false },
        onConfirmClicked = {
            isEditSubjectDialogueOpen = false
        },
        subjectName = subjectName,
        targetStudyHour = targetStudyHours,
        selectedColors = selectedColors,
        onColorsChange = { selectedColors = it },
        onSubjectNameChange = { subjectName = it },
        onTargetStudyHourChange = { targetStudyHours = it }
    )

    DeleteDialogue(
        isOpen = isDeleteSessionDialogueOpen,
        title = "Delete Session?",
        bodyText = "Are you sure you want to delete this session? Your Study hours will be reduced" +
                "by this session time. This action cannot be undone.",
        onDismissRequest = {
            isDeleteSessionDialogueOpen = false
        },
        onConfirmClicked = {
            isDeleteSessionDialogueOpen = false
        }
    )

    DeleteDialogue(
        isOpen = isDeleteSubjectDialogueOpen,
        title = "Delete Subject?",
        bodyText = "Are you sure you want to delete this subject? All related tasks and study sessions" +
                "will be permanently removed. This action cannot be undone.",
        onDismissRequest = {
            isDeleteSubjectDialogueOpen = false
        },
        onConfirmClicked = {
            isDeleteSubjectDialogueOpen = false
        }
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SubjectScreenTopBar(
                title = "Science",
                onBackButtonClick = {},
                onDeleteButtonClick = { isDeleteSubjectDialogueOpen = true },
                onEditButtonClick = { isEditSubjectDialogueOpen = true },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                expanded = isFabExpanded  ,
                onClick = {},
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add, contentDescription = "Add"
                    )
                },
                text = { Text(text = "Add Task") }
            )
        }
    ) {
        LazyColumn(
            state =  listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            item {
                SubjectOverview(
                    hoursStudied = "12",
                    targetHours = "15",
                    progress = .75f,
                    modifier = Modifier.fillMaxWidth().padding(12.dp)
                )
            }

            taskList(
                sectionTitle = "UPCOMING TASKS",
                emptyListText = emptyListText,
                tasks = taskList,
                onCheckBoxClicked = {},
                onTaskCardClick = {}
            )

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }

            taskList(
                sectionTitle = "Completed TASKS",
                emptyListText = "You don't have anu completed tasks.\n" +
                        "Click the check box on task completion",
                tasks = taskList,
                onCheckBoxClicked = {},
                onTaskCardClick = {}
            )


            item {
                Spacer(modifier = Modifier.height(20.dp))
            }

            studySessionsList(
                sectionTitle = "RECENT STUDY SESSIONS",
                emptyListText = emptySessionText,
                sessions = sessions,
                onDeleteIconClick = { isDeleteSessionDialogueOpen = true }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubjectScreenTopBar(
    title: String,
    onBackButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    onEditButtonClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    LargeTopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackButtonClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "navigate Back"
                )
            }
        },
        actions = {
            IconButton(onClick = onDeleteButtonClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Subject"
                )
            }

            IconButton(onClick = onEditButtonClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Subject"
                )
            }
        }
    )
}

@Composable
private fun SubjectOverview(
    hoursStudied: String,
    targetHours: String,
    progress: Float,
    modifier: Modifier
) {
    val percentageProgress = remember(key1 = progress) {
        (progress * 100).toInt().coerceIn(0, 100)
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Target Study Hours",
            count = targetHours
        )
        Spacer(Modifier.width(10.dp))
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Hours Studied",
            count = hoursStudied
        )
        Spacer(Modifier.width(10.dp))

        Box(
            modifier = Modifier.size(76.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                progress = 1f,
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round,
                color = MaterialTheme.colorScheme.surfaceVariant
            )
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                progress = progress,
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round,
            )
            Text(
                text = "${percentageProgress}%"
            )
        }

    }
}