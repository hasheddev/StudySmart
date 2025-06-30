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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hasheddev.studysmart.R
import com.hasheddev.studysmart.domain.model.Subject
import com.hasheddev.studysmart.presentation.components.CountCard
import com.hasheddev.studysmart.domain.model.Session
import com.hasheddev.studysmart.domain.model.Task
import com.hasheddev.studysmart.presentation.components.SubjectCard
import com.hasheddev.studysmart.presentation.components.studySessionsList
import com.hasheddev.studysmart.presentation.components.taskList


private const val emptyListText = "You don't have any upcoming tasks.\n Click the + button in subject screen button to add new tasks."
private const val emptySessionText = "You don't have any recent study sessions.\n Start a study session to begin recording your progress."
@Composable
fun DashBoardScreen() {
    val subjects = listOf(
        Subject(subjectId = 0, name = "English", goalHours = 10f, colors = Subject.subjectCardColors[0]),
        Subject(subjectId = 0, name = "Physics", goalHours = 1f, colors = Subject.subjectCardColors[1]),
        Subject(subjectId = 0, name = "Maths", goalHours = 12f, colors = Subject.subjectCardColors[2]),
        Subject(subjectId = 0, name = "Geology", goalHours = 0.3f, colors = Subject.subjectCardColors[3]),
        Subject(subjectId = 0, name = "Fine Arts", goalHours = 9f, colors = Subject.subjectCardColors[4])
    )
    val taskList = listOf(
        Task(taskId = 1, subjectId = 0, title = "Prepare Notes", description = "", dueDate = 0L, priority = 1, relatedSubject = "", isComplete = false),
        Task(taskId = 1, subjectId = 0, title = "Homework", description = "", dueDate = 0L, priority = 0, relatedSubject = "", isComplete = false),
        Task(taskId = 1, subjectId = 0, title = "Coaching", description = "", dueDate = 0L, priority = 2, relatedSubject = "", isComplete = true),
        Task(taskId = 1, subjectId = 0, title = "Assignment", description = "", dueDate = 0L, priority = 5, relatedSubject = "", isComplete = false),
        Task(taskId = 1, subjectId = 0, title = "Prepare Notes", description = "", dueDate = 0L, priority = 1, relatedSubject = "", isComplete = true)
    )

    val sessions = listOf(
        Session(
            relatedSubject = "English",
            date = 0L,
            duration = 2,
            sessionId = 2,
            sessionSubjectId = 1
        ),
        Session(
            relatedSubject = "Maths",
            date = 1L,
            duration = 0,
            sessionId = 3,
            sessionSubjectId = 6
        ),
        Session(
            relatedSubject = "Geo",
            date = 1L,
            duration = 0,
            sessionId = 3,
            sessionSubjectId = 6
        ),
        Session(
            relatedSubject = "Maths",
            date = 1L,
            duration = 0,
            sessionId = 3,
            sessionSubjectId = 6
        ),
        Session(
            relatedSubject = "Maths",
            date = 1L,
            duration = 0,
            sessionId = 3,
            sessionSubjectId = 6
        ),

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
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    subjectCount = 5,
                    hoursStudied = "10",
                    goalHours = "15"
                )
            }

            item {
                SubjectCardSection(
                    subjectList = subjects,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Button(
                    onClick = {},
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
                onTaskCardClick = {}
            )

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }

            studySessionsList(
                sectionTitle = "RECENT STUDY SESSIONS",
                emptyListText = emptySessionText,
                sessions = sessions,
                onDeleteIconClick = {}
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
private const val empty = "You don't have any subjects.\n Click the + button to add new subjects."

@Composable
private fun SubjectCardSection(
    subjectList: List<Subject> = emptyList(),
    emptyListText: String = empty,
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

            IconButton(onClick = {}) {
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
                        gradientColors = subject.colors,
                        onClick = {},
                        modifier = Modifier
                    )
                }
            }
        }
    }
}
