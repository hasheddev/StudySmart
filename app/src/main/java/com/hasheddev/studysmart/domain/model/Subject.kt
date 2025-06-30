package com.hasheddev.studysmart.domain.model

import androidx.compose.ui.graphics.Color
import com.hasheddev.studysmart.presentation.theme.gradient1
import com.hasheddev.studysmart.presentation.theme.gradient2
import com.hasheddev.studysmart.presentation.theme.gradient3
import com.hasheddev.studysmart.presentation.theme.gradient4
import com.hasheddev.studysmart.presentation.theme.gradient5

data class Subject(
    val subjectId: Int,
    val name: String,
    val goalHours: Float,
    val colors: List<Color>
) {
    companion object {
        val subjectCardColors = listOf(
            gradient1, gradient2, gradient3, gradient4, gradient5
        )
    }
}
