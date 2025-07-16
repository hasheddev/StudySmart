package com.hasheddev.studysmart.util

object Constants {
    const val ACTION_SERVICE_START = "ACTION_SERVICE_START"
    const val ACTION_SERVICE_STOP = "ACTION_SERVICE_STOP"
    const val ACTION_SERVICE_CANCEL = "ACTION_SERVICE_CANCEL"

    const val NOTIFICATION_CHANNEL_NAME = "STUDY_SMART_TIMER_CHANNEL"
    const val NOTIFICATION_CHANNEL_ID = "STUDY_SMART_TIMER_CHANNEL_ID"
    const val  NOTIFICATION_ID = 10

    const val REQUEST_CODE = 1000
    const val SCREEN_URI = "study_smart://dashboard/session"

    const val EMPTY_LIST_TEXT = "You don't have any upcoming tasks.\n Click the + button in subject screen button to add new tasks."
    const val EMPTY_SESSION_TEXT = "You don't have any recent study sessions.\n Start a study session to begin recording your progress."
    const val DELETE_SESSION_TEXT = "Are you sure you want to delete this session?" +
            "This action cannot be undone"
    const val DELETE_SESSION_TEXT_2 = "Are you sure you want to delete this session? Your Study hours will be reduced\" +\n" +
            "                \"by this session time. This action cannot be undone.\","
    const val EMPTY_SUBJECT_TEXT = "You don't have any subjects.\n Click the + button to add new subjects."
    const val TASK_DELETION_TEXT = "Are you sure you want to delete this task?" +
            "This action cannot be undone"
    const val EMPTY_COMPLETED_TASKS = "You don't have anu completed tasks.\n" +
            "Click the check box on task completion"
    const val SUBJECT_DELETION_TEXT = "Are you sure you want to delete this subject? All related tasks and study sessions" +
            "will be permanently removed. This action cannot be undone."

}