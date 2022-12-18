package com.feduss.pomodoro.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.wear.ongoing.OngoingActivity
import androidx.wear.ongoing.Status
import com.feduss.pomodoro.MainActivityViewController
import com.feduss.pomodoro.enums.Consts
import java.util.concurrent.TimeUnit
import com.feduss.pomodoro.R


class NotificationUtils {

    companion object {

        fun setNotification(context: Context, timerName: String, timerSecondsRemaining: Long) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val appIntent = Intent(context, MainActivityViewController::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 1, appIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val runStartTime = SystemClock.elapsedRealtime() + TimeUnit.SECONDS.toMillis(timerSecondsRemaining)

            //The creation of notification channel is mandatory since Api 26 (Oreo)
            val channel = NotificationChannel(
                Consts.MainChannelId.value,
                Consts.MainNotificationVisibleChannel.value,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel);

            val notificationBuilder = NotificationCompat.Builder(
                context,
                Consts.MainChannelId.value
            )
            .setContentTitle(timerName)
            .setSmallIcon(R.drawable.ic_timer_24dp_test)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)

            val ongoingActivityStatus = Status.Builder()
                // Sets the text used across various surfaces.
                .addTemplate("#timerType#: #time#")
                .addPart("timerType", Status.TextPart(timerName))
                .addPart("time", Status.StopwatchPart(runStartTime))
                .build()

            val ongoingActivity =
                OngoingActivity.Builder(
                    context.applicationContext,
                    Consts.MainNotificationId.value.toInt(),
                    notificationBuilder
                )
                    .setStaticIcon(R.drawable.ic_timer_24dp_test)
                    .setTouchIntent(pendingIntent)
                    .setStatus(ongoingActivityStatus)
                    .build()

            ongoingActivity.apply(context.applicationContext)

            notificationManager.notify(
                Consts.MainNotificationId.value.toInt(),
                notificationBuilder.build()
            )
        }

        fun removeNotification(context: Context) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(Consts.MainNotificationId.value.toInt())
        }
    }
}