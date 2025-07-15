package com.hasheddev.studysmart

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import com.hasheddev.studysmart.domain.model.Session
import com.hasheddev.studysmart.domain.model.Subject
import com.hasheddev.studysmart.presentation.NavGraphs
import com.hasheddev.studysmart.presentation.destinations.SessionScreenRouteDestination
import com.hasheddev.studysmart.presentation.session.StudySessionTimerService
import com.hasheddev.studysmart.presentation.theme.StudySmartTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var isBound by mutableStateOf(false)
    private lateinit var timerService: StudySessionTimerService

    private val connection = object: ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            val binder = service as StudySessionTimerService.StudySessionServiceBinder
            timerService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, StudySessionTimerService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudySmartTheme {
                if (isBound)
                    DestinationsNavHost(
                        navGraph = NavGraphs.root,
                        dependenciesContainerBuilder = {
                            dependency(SessionScreenRouteDestination) {
                                timerService
                            }
                        }
                    )
            }
            requestPermissions()
        }
    }
    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.POST_NOTIFICATIONS,
                    android.Manifest.permission.FOREGROUND_SERVICE
                ),
                0
            )
        }
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(connection)
            isBound = false
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
