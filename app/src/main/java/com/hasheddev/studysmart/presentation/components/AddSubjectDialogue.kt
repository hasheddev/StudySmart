package com.hasheddev.studysmart.presentation.components

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hasheddev.studysmart.domain.model.Subject

@Composable
fun AddSubjectDialogue(
    isOpen: Boolean,
    title: String = "Add/Update Subject",
    subjectName: String,
    targetStudyHour: String,
    onDismissRequest: () -> Unit,
    onConfirmClicked: () -> Unit,
    selectedColors: List<Color>,
    onColorsChange: (List<Color>) -> Unit,
    onSubjectNameChange: (String) -> Unit,
    onTargetStudyHourChange: (String) -> Unit,
) {
    var targetHourError by rememberSaveable { mutableStateOf<String?>(null) }
    var subjectNameError by rememberSaveable { mutableStateOf<String?>(null) }

    subjectNameError = when {
        subjectName.isBlank() -> "Please enter subject name"
        subjectName.length < 2 -> "Subject name is too short"
        subjectName.length > 20 ->  "Subject name is too long"
        else -> null
    }

    targetHourError = when {
        targetStudyHour.isBlank() -> "Please enter target hour"
        targetStudyHour.toFloatOrNull() == null -> "Please enter a valid number"
        targetStudyHour.toFloat() < 1f ->  "Please set at least 1 hour"
        targetStudyHour.toFloat() > 100f ->  "Please set at maximum 100 hour"
        else -> null
    }

    if (isOpen)
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(
                text = title,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            ) },
            text = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Subject.subjectCardColors.forEach { colors ->
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 2.dp,
                                        color = if (colors == selectedColors) Color.Black else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .background(brush = Brush.verticalGradient(colors))
                                    .clickable{ onColorsChange(colors) }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = subjectName,
                        onValueChange = onSubjectNameChange,
                        label = { Text(text = "Subject Name") },
                        singleLine = true,
                        isError = subjectNameError != null && subjectName.isNotBlank(),
                        supportingText = {Text(text = subjectNameError.orEmpty())}
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = targetStudyHour,
                        onValueChange = onTargetStudyHourChange,
                        label = { Text(text = "Study Hours Target") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = targetHourError != null && targetStudyHour.isNotBlank(),
                        supportingText = {Text(text = targetHourError.orEmpty())}
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = "Cancel")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirmClicked,
                    enabled = subjectNameError == null && targetHourError == null
                ) {
                    Text(text = "save")
                }
            }
        )
}