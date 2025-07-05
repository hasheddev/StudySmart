package com.hasheddev.studysmart.data.local

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.hasheddev.studysmart.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionDao {

    @Insert
    suspend fun insertSession(session: Session)

    @Delete
    suspend fun deleteSession(session: Session)

    @Query("SELECt * FROM Session")
    fun getAllSessions(): Flow<List<Session>>

    @Query("SELECT * FROM Session WHERE sessionSubjectId = :subjectId")
    fun getRecentSessionsSorSubject(subjectId: Int): Flow<List<Session>>

    @Query("SELECT SUM(duration) FROM Session")
    fun getTotalSessionDuration(): Flow<Long>

    @Query("SELECT SUM(duration) FROM Session WHERE sessionSubjectId = :subjectId")
    fun getTotalSessionDurationBySubjectId(subjectId: Int): Flow<Long>

    @Query("DELETE FROM Session WHERE sessionSubjectId = :subjectId")
    suspend fun deleteSessionBySubjectId(subjectId: Int)
}