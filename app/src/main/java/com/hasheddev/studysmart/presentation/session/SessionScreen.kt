package com.hasheddev.studysmart.presentation.session

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hasheddev.studysmart.presentation.components.DeleteDialogue
import com.hasheddev.studysmart.presentation.components.SubjectListBottomSheet
import com.hasheddev.studysmart.presentation.components.studySessionsList
import com.hasheddev.studysmart.presentation.theme.Red
import com.hasheddev.studysmart.util.Constants
import com.hasheddev.studysmart.util.Constants.DELETE_SESSION_TEXT
import com.hasheddev.studysmart.util.SnackBarEvent
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.time.DurationUnit

@Destination(
    deepLinks = [
        DeepLink(
            action = Intent.ACTION_VIEW,
            uriPattern = Constants.SCREEN_URI
        )
    ]
)
@Composable
fun SessionScreenRoute(
    navigator: DestinationsNavigator,
    timerService: StudySessionTimerService
) {
    val viewModel: SessionViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    SessionScreen(
        timerService = timerService,
        state = state,
        snackBarEvents = viewModel.snackBarEvent,
        onBackButtonClick = { navigator.navigateUp() },
        onEvent = viewModel::onEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionScreen(
    state: SessionState,
    timerService: StudySessionTimerService,
    snackBarEvents: SharedFlow<SnackBarEvent>,
    onBackButtonClick: () -> Unit,
    onEvent: (SessionEvent) -> Unit
) {

    val hours by timerService.hours.collectAsStateWithLifecycle()
    val minutes by timerService.minutes.collectAsStateWithLifecycle()
    val seconds by timerService.seconds.collectAsStateWithLifecycle()
    val timerState by timerService.currentTimerState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var isDeleteDialogueOpen by rememberSaveable { mutableStateOf(false) }
    var isBottomSheetOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

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

    LaunchedEffect(state.subjects) {
        val subjectId = timerService.subjectId.value
        onEvent(
            SessionEvent.OnUpdateSubjectIdAndRelatedSubject(
                subjectId =  subjectId,
                relatedSubject = state.subjects.find { it.subjectId == subjectId }?.name
            )
        )
    }

    DeleteDialogue(
        isOpen = isDeleteDialogueOpen,
        title = "Delete Session",
        bodyText = DELETE_SESSION_TEXT,
        onDismissRequest = { isDeleteDialogueOpen = false },
        onConfirmClicked = {
            isDeleteDialogueOpen = false
            onEvent(SessionEvent.DeleteSession)
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
                onEvent(SessionEvent.OnRelatedSubjectChange(subject))
            }
        },
        onDismissRequest = {
            isBottomSheetOpen = false
        }
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarState) },
        topBar = {
            SessionTopBar { onBackButtonClick() }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                TimerSection(
                    hours = hours,
                    minutes = minutes,
                    seconds = seconds,
                    modifier = Modifier.fillMaxWidth()
                        .aspectRatio(1f)
                )
            }

            item {
                RelatedSubjects(
                    seconds = seconds,
                    minutes = minutes,
                    hours = hours,
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    relatedSubject = state.relatedSubject ?: "",
                    selectSubjectButtonClick = {
                        isBottomSheetOpen = true
                    }
                )
            }

            item {
                ButtonSection(
                    onStartButtonClick = {
                        if(state.subjectId != null && state.relatedSubject != null) {
                            ServiceHelper.triggerForegroundService(
                                context = context,
                                action = if (timerState == TimerState.STARTED) Constants.ACTION_SERVICE_STOP
                                else Constants.ACTION_SERVICE_START
                            )
                            timerService.updateSubjectId(state.subjectId)
                        } else {
                            onEvent(SessionEvent.NotifyRelatedSubjectSelection)
                        }

                    },
                    onCancelButtonClick = {
                        ServiceHelper.triggerForegroundService(
                            context = context,
                            action = Constants.ACTION_SERVICE_CANCEL
                        )
                    },
                    onFinishButtonClick = {
                        val duration =  timerService.duration.toLong(DurationUnit.SECONDS)
                        onEvent(SessionEvent.OnSaveSession(
                            duration = duration
                        ))
                        if (duration >= 60) {
                            ServiceHelper.triggerForegroundService(
                                context = context,
                                action = Constants.ACTION_SERVICE_CANCEL
                            )
                        }
                    },
                    timerState = timerState,
                    seconds = seconds,
                    minutes = minutes,
                    hours = hours,
                    modifier = Modifier.fillMaxWidth().padding(12.dp)
                )
            }

            studySessionsList(
                sectionTitle = "STUDY SESSIONS HISTORY",
                emptyListText = Constants.EMPTY_SESSION_TEXT,
                sessions = state.sessions,
                onDeleteIconClick = {
                    isDeleteDialogueOpen = true
                    onEvent(SessionEvent.OnDeleteSessionClick(it))
                }
            )
        }
    }
}

@Composable
private fun TimerSection(
    hours: String,
    minutes: String,
    seconds: String,
    modifier: Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(250.dp)
                .border(
                    5.dp,
                    MaterialTheme.colorScheme.surfaceVariant,
                    CircleShape
                )
        )
        Row {
            AnimatedContent(
                targetState = hours,
                label = hours,
                transitionSpec = { timerTextAnimation() }
            ) { hours ->
                Text(
                    text = "$hours:",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
                )
            }

            AnimatedContent(
                targetState = "$minutes:",
                label = minutes,
                transitionSpec = { timerTextAnimation() }
            ) { minutes ->
                Text(
                    text = minutes,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
                )
            }

            AnimatedContent(
                targetState = seconds,
                label = seconds,
                transitionSpec = { timerTextAnimation() }
            ) { seconds ->
                Text(
                    text = seconds,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionTopBar(
    onBackButtonClicked: () -> Unit
) {
    TopAppBar(
        title = {
            Text(text = "Study Sessions", style = MaterialTheme.typography.headlineSmall)
        },
        navigationIcon = {
            IconButton(
                onClick = onBackButtonClicked
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Navigate to Previous Screen"
                )
            }
        }
    )
}

@Composable
private fun RelatedSubjects(
    relatedSubject: String,
    hours: String,
    minutes: String,
    seconds: String,
    selectSubjectButtonClick: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier
    ) {
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
                text = relatedSubject,
                style = MaterialTheme.typography.bodyLarge
            )
            IconButton(
                onClick = selectSubjectButtonClick,
                enabled = (seconds == "00" && minutes == "00" && hours == "00")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select Subject"
                )
            }
        }
    }

}

@Composable
private fun ButtonSection(
    onStartButtonClick: () -> Unit,
    onCancelButtonClick: () -> Unit,
    onFinishButtonClick: () -> Unit,
    timerState: TimerState,
    seconds: String,
    minutes: String,
    hours: String,
    modifier: Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick  = onCancelButtonClick,
            enabled = (seconds != "00" || minutes != "00" || hours != "00")
                            && timerState != TimerState.STARTED
        ) {
            Text(
                text = "Cancel",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }

        Button(
            onClick  = onStartButtonClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (timerState == TimerState.STARTED) Red else
                                        MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {
            Text(
                text = when(timerState) {
                    TimerState.STARTED -> "Stop"
                    TimerState.STOPPED -> "Resume"
                    else -> "Start"
                },
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }

        Button(
            onClick  = onFinishButtonClick,
            enabled = (seconds != "00" || minutes != "00" || hours != "00")
                            && timerState != TimerState.STARTED
        ) {
            Text(
                text = "Finish",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }
    }
}

private fun timerTextAnimation(duration: Int = 600): ContentTransform {
    return  slideInVertically(animationSpec = tween(duration)) { fullHeight ->  fullHeight} +
                fadeIn(animationSpec = tween(duration)) togetherWith
                slideOutVertically(animationSpec = tween(duration)) { fullHeight ->  -fullHeight} +
                fadeOut(animationSpec = tween(duration))
}