package com.hasheddev.studysmart.presentation.dashboard

import androidx.lifecycle.ViewModel
import com.hasheddev.studysmart.domain.repository.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashBoardViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository
): ViewModel()  {
}