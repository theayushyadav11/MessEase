//package com.theayushyadav11.messease.utils
//
//
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.content.Context
//import android.os.Build
//
//object NotificationUtils {
//    const val CHANNEL_ID = "my_channel_id"
//
//    fun createNotificationChannel(context: Context) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = "My Channel"
//            val descriptionText = "Channel for daily notifications"
//            val importance = NotificationManager.IMPORTANCE_HIGH
//            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
//                description = descriptionText
//                enableVibration(true)
//                enableLights(true)
//                vibrationPattern = longArrayOf(0, 500, 1000)
//            }
//            val notificationManager=context.getString(NotificationReceiver::class.java)
//
//
//
//
////                    NotificationManager =
////                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
////            notificationManager.createNotificationChannel(channel)
//        }
//    }
//}
