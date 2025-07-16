package com.hasheddev.studysmart.presentation.session

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.hasheddev.studysmart.util.Constants.ACTION_SERVICE_CANCEL
import com.hasheddev.studysmart.util.Constants.ACTION_SERVICE_START
import com.hasheddev.studysmart.util.Constants.ACTION_SERVICE_STOP
import com.hasheddev.studysmart.util.Constants.NOTIFICATION_CHANNEL_ID
import com.hasheddev.studysmart.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.hasheddev.studysmart.util.Constants.NOTIFICATION_ID
import com.hasheddev.studysmart.util.pad
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class StudySessionTimerService: Service() {

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    private val binder = StudySessionServiceBinder()

    private lateinit var timer: Timer

    var duration = Duration.ZERO
        private set

    private var _seconds = MutableStateFlow("00")
    val seconds = _seconds.asStateFlow()

    private var _minutes = MutableStateFlow("00")
    val minutes: StateFlow<String> = _minutes

    private var _hours = MutableStateFlow("00")
    val hours = _hours.asStateFlow()

    private var _currentTimerState = MutableStateFlow(TimerState.IDLE)
    val currentTimerState: StateFlow<TimerState> = _currentTimerState

    private var _subjectId = MutableStateFlow<Int?>(null)
    val subjectId: StateFlow<Int?> = _subjectId

    override fun onBind(p0: Intent?): IBinder = binder

    fun updateSubjectId(id: Int) {
        _subjectId.update { id }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let {
            when(it) {
                ACTION_SERVICE_START -> {
                    startForegroundService()
                    startTimer { h, m, s ->
                        updateNotification(h, m, s)
                    }
                }
                ACTION_SERVICE_STOP -> {
                    stopTimer()
                }
                ACTION_SERVICE_CANCEL-> {
                    stopTimer()
                    cancelTimer()
                    stopForegroundService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun updateNotification(hours: String, minutes: String, seconds: String) {
        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder.setContentText("$hours:$minutes:$seconds").build()
        )
    }

    @SuppressLint("ForegroundServiceType")
    private fun startForegroundService() {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun stopForegroundService() {
        notificationManager.cancel(NOTIFICATION_ID)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
        stopSelf()
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

    }

    private fun startTimer(
        onTick: (h: String, m: String, s: String) -> Unit
    ) {
        _currentTimerState.update { TimerState.STARTED }
        timer = fixedRateTimer(initialDelay = 1000L, period = 1000L) {
            duration = duration.plus(1.seconds)
            updateTimeUnits()
            onTick(_hours.value, _minutes.value, _seconds.value)
        }
    }

    private fun stopTimer() {
        if(this::timer.isInitialized) {
            timer.cancel()
        }
        _currentTimerState.update { TimerState.STOPPED }
    }

    private fun cancelTimer() {
        duration = Duration.ZERO
        updateTimeUnits()
        _currentTimerState.update { TimerState.IDLE }
    }

    private fun updateTimeUnits() {
        duration.toComponents { hours, minutes, seconds, _  ->
            this._hours.update { hours.pad() }
            this._minutes.update { minutes.pad() }
            this._seconds.update { seconds.pad() }
        }
    }

    inner class StudySessionServiceBinder: Binder() {
        fun getService(): StudySessionTimerService = this@StudySessionTimerService
    }

}

enum class TimerState {
    IDLE,
    STARTED,
    STOPPED
}