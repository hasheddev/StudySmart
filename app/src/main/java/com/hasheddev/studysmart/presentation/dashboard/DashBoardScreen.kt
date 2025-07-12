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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.hasheddev.studysmart.R
import com.hasheddev.studysmart.domain.model.Subject
import com.hasheddev.studysmart.emptyListText
import com.hasheddev.studysmart.emptySessionText
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
import com.hasheddev.studysmart.sessions
import com.hasheddev.studysmart.subjects
import com.hasheddev.studysmart.taskList
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination(start = true)
@Composable
fun DashboardScreenRoute(
    navigator: DestinationsNavigator
) {

    val viewModel: DashBoardViewModel = hiltViewModel()
    DashBoardScreen(
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
        }
    )
}
@Composable
private fun DashBoardScreen(
    onSubjectCardClick: (Int?) -> Unit,
    onTaskCardClick: (Int?) -> Unit,
    onStartSessionClick: () -> Unit
) {

    var isAddSubjectDialogueOpen by rememberSaveable { mutableStateOf(false) }
    var isDeleteSessionDialogueOpen by rememberSaveable { mutableStateOf(false) }
    var subjectName by remember { mutableStateOf("") }
    var targetStudyHours by remember { mutableStateOf("") }
    var selectedColors by remember { mutableStateOf(Subject.subjectCardColors.random()) }


    AddSubjectDialogue(
        isOpen = isAddSubjectDialogueOpen,
        onDismissRequest = { isAddSubjectDialogueOpen = false },
        onConfirmClicked = {
            isAddSubjectDialogueOpen = false
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

    Scaffold(
        topBar ={ DashBoardScreenTopAppBar() }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                CountCardSection(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    subjectCount = 5,
                    hoursStudied = "10",
                    goalHours = "15"
                )
            }

            item {
                SubjectCardSection(
                    subjectList = subjects,
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
                emptyListText = emptyListText,
                tasks = taskList,
                onCheckBoxClicked = {},
                onTaskCardClick = onTaskCardClick
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

const val empty = "You don't have any subjects.\n Click the + button to add new subjects."
@Composable
private fun SubjectCardSection(
    subjectList: List<Subject> = emptyList(),
    emptyListText: String = empty,
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
