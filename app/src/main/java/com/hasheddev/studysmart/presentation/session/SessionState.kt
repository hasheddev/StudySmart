package com.hasheddev.studysmart.presentation.session

import com.hasheddev.studysmart.domain.model.Session
import com.hasheddev.studysmart.domain.model.Subject

data class SessionState(
    val sessions: List<Session> = emptyList(),
    val subjects: List<Subject> = emptyList(),
    val relatedSubject: String? = null,
    val subjectId: Int? = null,
    val session: Session? = null
)
