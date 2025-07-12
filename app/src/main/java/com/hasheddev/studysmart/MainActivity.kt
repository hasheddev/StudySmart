package com.hasheddev.studysmart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import com.hasheddev.studysmart.domain.model.Session
import com.hasheddev.studysmart.domain.model.Subject
import com.hasheddev.studysmart.domain.model.Task
import com.hasheddev.studysmart.presentation.NavGraphs
import com.hasheddev.studysmart.presentation.theme.StudySmartTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudySmartTheme {
                DestinationsNavHost(
                    navGraph = NavGraphs.root
                )
            }
        }
    }
}

val subjects = listOf(
    Subject(subjectId = 0, name = "English", goalHours = 10f, colors = Subject.subjectCardColors[0].map { it.toArgb() }),
    Subject(subjectId = 0, name = "Physics", goalHours = 1f, colors = Subject.subjectCardColors[1].map { it.toArgb() }),
    Subject(subjectId = 0, name = "Maths", goalHours = 12f, colors = Subject.subjectCardColors[2].map { it.toArgb() }),
    Subject(subjectId = 0, name = "Geology", goalHours = 0.3f, colors = Subject.subjectCardColors[3].map { it.toArgb() }),
    Subject(subjectId = 0, name = "Fine Arts", goalHours = 9f, colors = Subject.subjectCardColors[4].map { it.toArgb() })
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
    )
)
const val emptyListText = "You don't have any upcoming tasks.\n Click the + button in subject screen button to add new tasks."
const val emptySessionText = "You don't have any recent study sessions.\n Start a study session to begin recording your progress."
