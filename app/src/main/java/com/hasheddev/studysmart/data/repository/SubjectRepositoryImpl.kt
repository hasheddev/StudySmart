package com.hasheddev.studysmart.data.repository

import com.hasheddev.studysmart.data.local.SessionDao
import com.hasheddev.studysmart.data.local.SubjectDao
import com.hasheddev.studysmart.data.local.TaskDao
import com.hasheddev.studysmart.domain.model.Subject
import com.hasheddev.studysmart.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubjectRepositoryImpl  @Inject constructor (
    private val subjectDao: SubjectDao,
    private val taskDao: TaskDao,
    private val sessionDao: SessionDao
): SubjectRepository {
    override suspend fun upsertSubject(subject: Subject) {
        subjectDao.upsertSubject(subject)
    }

    override fun getTotalSubjectCount(): Flow<Int> {
        return subjectDao.getTotalSubjectCount()
    }

    override fun getTotalGoalHours(): Flow<Float> {
        return subjectDao.getTotalGoalHours()
    }

    override suspend fun deleteSubject(subjectId: Int) {
        taskDao.deleteTaskBySubjectById(subjectId)
        sessionDao.deleteSessionsForSubject(subjectId)
        subjectDao.deleteSubjectById(subjectId)
    }

    override suspend fun getSubjectById(subjectId: Int): Subject? {
        return subjectDao.getSubjectById(subjectId)
    }

    override fun getAllSubjects(): Flow<List<Subject>> {
        return subjectDao.getAllSubjects()
    }
}