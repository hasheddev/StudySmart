package com.hasheddev.studysmart.presentation.session

import com.hasheddev.studysmart.domain.model.Session
import com.hasheddev.studysmart.domain.model.Subject


sealed interface SessionEvent {
    data class OnRelatedSubjectChange(val subject: Subject): SessionEvent

    data class OnSaveSession(val duration: Long): SessionEvent

    data class OnDeleteSessionClick(val session: Session): SessionEvent

    data class OnUpdateSubjectIdAndRelatedSubject(
        val subjectId: Int?,
        val relatedSubject: String?
    ): SessionEvent

    data object DeleteSession: SessionEvent

    data object NotifyRelatedSubjectSelection: SessionEvent
}