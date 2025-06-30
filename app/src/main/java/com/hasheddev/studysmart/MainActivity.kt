package com.hasheddev.studysmart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hasheddev.studysmart.presentation.dashboard.DashBoardScreen
import com.hasheddev.studysmart.presentation.theme.StudySmartTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudySmartTheme {
                    DashBoardScreen()
            }
        }
    }
}