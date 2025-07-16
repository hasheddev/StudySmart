package com.hasheddev.studysmart.presentation.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hasheddev.studysmart.R
import com.hasheddev.studysmart.domain.model.Session
import com.hasheddev.studysmart.domain.model.Subject
import com.hasheddev.studysmart.domain.model.Task
import com.hasheddev.studysmart.presentation.components.AddSubjectDialogue
import com.hasheddev.studysmart.presentation.components.CountCard
import com.hasheddev.studysmart.presentation.components.DeleteDialogue
import com.hasheddev.studysmart.presentation.components.SubjectCard
import com.hasheddev.studysmart.presentation.components.studySessionsList
import com.hasheddev.studysmart.presentation.components.taskList
import com.hasheddev.studysmart.presentation.destinations.SessionScreenRouteDestination
import com.hasheddev.studysmart.presentation.destinations.SubjectScreenRouteDestination
import com.hasheddev.studysmart.presentation.destinations.TaskScreenRouteDestination
import com.hasheddev.studysmart.presentation.subject.SubjectScreenNavArgs
import com.hasheddev.studysmart.presentation.task.TaskScreenNavArgs
import com.hasheddev.studysmart.util.Constants
import com.hasheddev.studysmart.util.Constants.EMPTY_SUBJECT_TEXT
import com.hasheddev.studysmart.util.SnackBarEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@RootNavGraph(start = true)
@Destination
@Composable
fun DashboardScreenRoute(
    navigator: DestinationsNavigator
) {

    val viewModel: DashBoardViewModel = hiltViewModel()
    val state by viewModel.dashBoardState.collectAsStateWithLifecycle()
    val sessions by viewModel.recentSessions.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()

    DashBoardScreen(
        state = state,
        sessions = sessions,
        tasks = tasks,
        snackBarEvents = viewModel.snackBarEvent,
        onSubjectCardClick= { id ->
            id?.let {
                val navArgs = SubjectScreenNavArgs(subjectId = id)
                navigator.navigate(SubjectScreenRouteDestination(navArgs = navArgs))
            }
        },
        onTaskCardClick= { id ->
            val navArgs = TaskScreenNavArgs(taskId = id, subjectId = null)
            navigator.navigate(TaskScreenRouteDestination(navArgs = navArgs))
        },
        onStartSessionClick = {
            navigator.navigate(SessionScreenRouteDestination)
        },
        onEvent = { viewModel.onEvent(it) }
    )
}
@Composable
private fun DashBoardScreen(
    state: DashBoardState,
    sessions: List<Session>,
    tasks: List<Task>,
    snackBarEvents: SharedFlow<SnackBarEvent>,
    onSubjectCardClick: (Int?) -> Unit,
    onTaskCardClick: (Int?) -> Unit,
    onStartSessionClick: () -> Unit,
    onEvent: (DashBoardEvent) -> Unit
) {

    var isAddSubjectDialogueOpen by rememberSaveable { mutableStateOf(false) }
    var isDeleteSessionDialogueOpen by rememberSaveable { mutableStateOf(false) }

    val snackBarState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(true) {
        snackBarEvents.collectLatest { event ->
            when(event) {
                is SnackBarEvent.ShowSnackBar -> {
                    snackBarState.showSnackbar(
                        message = event.message,
                        duration = event.duration
                    )
                }

                SnackBarEvent.NavigateUp -> Unit
            }
        }
    }

    AddSubjectDialogue(
        isOpen = isAddSubjectDialogueOpen,
        onDismissRequest = { isAddSubjectDialogueOpen = false },
        onConfirmClicked = {
            isAddSubjectDialogueOpen = false
            onEvent(DashBoardEvent.SaveSubject)
        },
        subjectName = state.subjectName,
        targetStudyHour = state.targetStudyHours,
        selectedColors = state.subjectCardColors,
        onColorsChange = { onEvent(DashBoardEvent.OnSubjectCardColorChange(it)) },
        onSubjectNameChange = { onEvent(DashBoardEvent.OnSubjectNameChange(it)) },
        onTargetStudyHourChange = { onEvent(DashBoardEvent.OnTagetStudyHoursChange(it))  }
    )

    DeleteDialogue(
        isOpen = isDeleteSessionDialogueOpen,
        title = "Delete Session?",
        bodyText = Constants.DELETE_SESSION_TEXT_2,
        onDismissRequest = {
            isDeleteSessionDialogueOpen = false
        },
        onConfirmClicked = {
            isDeleteSessionDialogueOpen = false
            onEvent(DashBoardEvent.DeleteSession)
        }
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarState) },
        topBar ={ DashBoardScreenTopAppBar() }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                CountCardSection(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    subjectCount = state.totalSubjectCount,
                    hoursStudied = state.totalHoursStudied.toString(),
                    goalHours = state.totalTargetStudyHours.toString()
                )
            }

            item {
                SubjectCardSection(
                    subjectList = state.subjects,
                    onAddIconClicked = {
                        isAddSubjectDialogueOpen = true
                    },
                    onSubjectCardClick = onSubjectCardClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Button(
                    onClick = onStartSessionClick,
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 48.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = "Start Study Session"
                    )
                }
            }
            taskList(
                sectionTitle = "UPCOMING TASKS",
                emptyListText = Constants.EMPTY_LIST_TEXT,
                tasks = tasks,
                onCheckBoxClicked = {onEvent(DashBoardEvent.OnTaskCompleteChanged(it))},
                onTaskCardClick = onTaskCardClick
            )

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }

            studySessionsList(
                sectionTitle = "RECENT STUDY SESSIONS",
                emptyListText = Constants.EMPTY_SESSION_TEXT,
                sessions = sessions,
                onDeleteIconClick = {
                    isDeleteSessionDialogueOpen = true
                    onEvent(DashBoardEvent.OnDeleteSession(it))
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashBoardScreenTopAppBar() {
    CenterAlignedTopAppBar(
        title = { Text(
            text = "StudySmart",
            style = MaterialTheme.typography.headlineMedium
        ) }
    )
}

@Composable
private fun CountCardSection(
    subjectCount: Int,
    hoursStudied: String,
    goalHours: String,
    modifier: Modifier = Modifier
) {

    Row(modifier = modifier) {
        CountCard(
            headingText = "SubjectCount",
            count = "$subjectCount",
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(10.dp))

        CountCard(
            headingText = "Hours Studied",
            count = hoursStudied,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(10.dp))

        CountCard(
            headingText = "Target Study Hours",
            count = goalHours,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SubjectCardSection(
    subjectList: List<Subject> = emptyList(),
    emptyListText: String = EMPTY_SUBJECT_TEXT,
    onAddIconClicked: () -> Unit,
    onSubjectCardClick: (Int?) -> Unit,
    modifier: Modifier
) {

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "SUBJECTS",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 12.dp)
            )

            IconButton(onClick = onAddIconClicked) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Subject"
                )
            }
        }

        if(subjectList.isEmpty()) {
            Image(
                modifier = Modifier.size(120.dp).align(Alignment.CenterHorizontally),
                painter = painterResource(R.drawable.img_books),
                contentDescription = emptyListText
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = emptyListText,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }

        else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp)
            ) {
                items(subjectList) {subject ->
                    SubjectCard(
                        subjectName = subject.name,
                        gradientColors = subject.colors.map { Color(it) },
                        onClick = { onSubjectCardClick(subject.subjectId) },
                        modifier = Modifier
                    )
                }
            }
        }
    }
}
