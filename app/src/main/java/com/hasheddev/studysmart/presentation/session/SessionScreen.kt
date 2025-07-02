package com.hasheddev.studysmart.presentation.session

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hasheddev.studysmart.emptySessionText
import com.hasheddev.studysmart.presentation.components.DeleteDialogue
import com.hasheddev.studysmart.presentation.components.SubjectListBottomSheet
import com.hasheddev.studysmart.presentation.components.studySessionsList
import com.hasheddev.studysmart.sessions
import com.hasheddev.studysmart.subjects
import kotlinx.coroutines.launch

const val bodyText = "Are you sure you want to delete this session?" +
        "This action cannot be undone"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen() {

    var isDeleteDialogueOpen by rememberSaveable { mutableStateOf(false) }
    var isBottomSheetOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    DeleteDialogue(
        isOpen = isDeleteDialogueOpen,
        title = "Delete Session",
        bodyText = bodyText,
        onDismissRequest = { isDeleteDialogueOpen = false },
        onConfirmClicked = { isDeleteDialogueOpen = false }
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
            SessionTopBar {  }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                TimerSection(
                    modifier = Modifier.fillMaxWidth()
                        .aspectRatio(1f)
                )
            }

            item {
                RelatedSubjects(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    relatedSubject = "Biology",
                    selectSubjectButtonClick = {
                        isBottomSheetOpen = true
                    }
                )
            }

            item {
                ButtonSection(
                    onStartButtonClick = {},
                    onCancelButtonClick = {},
                    onFinishButtonClick = {},
                    modifier = Modifier.fillMaxWidth().padding(12.dp)
                )
            }

            studySessionsList(
                sectionTitle = "STUDY SESSIONS HISTORY",
                emptyListText = emptySessionText,
                sessions = sessions,
                onDeleteIconClick = { isDeleteDialogueOpen = true }
            )
        }
    }
}

@Composable
private fun TimerSection(
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
        Text(
            text = "00:05:32",
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
        )
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
                text = "English",
                style = MaterialTheme.typography.bodyLarge
            )
            IconButton(onClick = selectSubjectButtonClick) {
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
    modifier: Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick  = onCancelButtonClick) {
            Text(
                text = "Cancel",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }

        Button(onClick  = onStartButtonClick) {
            Text(
                text = "Start",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }

        Button(onClick  = onFinishButtonClick) {
            Text(
                text = "Finish",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }
    }
}