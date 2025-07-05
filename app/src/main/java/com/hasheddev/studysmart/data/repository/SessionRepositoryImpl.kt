package com.hasheddev.studysmart.data.repository

import com.hasheddev.studysmart.data.local.SessionDao
import com.hasheddev.studysmart.domain.model.Session
import com.hasheddev.studysmart.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao
): SessionRepository {
    override suspend fun insertSession(session: Session) {
        sessionDao.insertSession(session)
    }

    override suspend fun deleteSession(session: Session) {
        TODO("Not yet implemented")
    }

    override fun getAllSessions(): Flow<List<Session>> {
        TODO("Not yet implemented")
    }

    override fun getRecentFiveSessions(): Flow<List<Session>> {
        TODO("Not yet implemented")
    }

    override fun getRecentTenSessionsForSubject(subjectId: Int): Flow<List<Session>> {
        TODO("Not yet implemented")
    }

    override fun getTotalSessionDuration(): Flow<Long> {
        TODO("Not yet implemented")
    }

    override fun getTotalSessionDurationBySubjectId(subjectId: Int): Flow<Long> {
        TODO("Not yet implemented")
    }
}