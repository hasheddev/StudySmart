package com.hasheddev.studysmart.domain.model

data class Session(
    val sessionId: Int,
    val relatedSubject: String,
    val date: Long,
    val duration: Long,
    val sessionSubjectId: Int
)
