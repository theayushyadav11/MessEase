package com.theayushyadav11.messease.utils


import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.theayushyadav11.messease.R
import com.theayushyadav11.messease.activities.MainActivity
import com.theayushyadav11.myapplication.database.MenuDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        println("AYUSH YADAV IS GOOD")
        context?.let {
            Toast.makeText(it, "Ayush Yadav is good", Toast.LENGTH_SHORT).show()

            val targetIntent = Intent(it, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                it, 0, targetIntent, PendingIntent.FLAG_IMMUTABLE
            )
            GlobalScope.launch(Dispatchers.IO)
            {
                val database=MenuDatabase.getDatabase(context).menuDao()
                val menu=database.getMenu()
                val v=intent?.getIntExtra("type",3)
                val day= getDayOfWeek()
                val title=("Reminder for ${menu.menu.list[day][v!!].foodType}")
                val message=("Today's ${menu.menu.list[day][v].foodType} is\n${menu.menu.list[day][v].food}\nTiming: ${menu.menu.list[day][v].timing}")
                val builder = NotificationCompat.Builder(it, "DailyNotification")
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText(message)
                    )
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)

                val notificationManager = NotificationManagerCompat.from(it)

                if (ActivityCompat.checkSelfPermission(it, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    return@launch
                }

                notificationManager.notify(123, builder.build())






            }


        }
    }
    fun getDayOfWeek(): Int {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // Adjust the result to make Monday as 0
        return when (dayOfWeek) {
            Calendar.SUNDAY -> 6
            else -> dayOfWeek - 2
        }
    }
}
