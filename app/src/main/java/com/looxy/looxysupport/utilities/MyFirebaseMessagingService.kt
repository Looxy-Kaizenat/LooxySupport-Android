package com.looxy.looxysupport.utilities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.looxy.looxysupport.activity.MainActivity
import com.looxy.looxysupport.R
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MyFirebaseMessagingService: FirebaseMessagingService() {

    init {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("testing", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
            })
    }

    var globalValues: GlobalValues? = null

    private val channelID = "Looxy_notifications"
    private val notificationID = Random().nextInt()

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        globalValues = GlobalValues(this)
        Log.e("testing", "New Token is = $s")
        globalValues!!.put("FirebaseToken", s)
        Log.e("testing", "FirebaseToken is = " + globalValues!!.getString("FirebaseToken"))
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        globalValues = GlobalValues(this)
        if (remoteMessage.data.isNotEmpty()) {
            Log.e("testing", "Data Payload: " + remoteMessage.data.toString())
            try {
                val json = JSONObject(remoteMessage.data.toString())
                handleDataMessage(json)
            } catch (e: Exception) {
                Log.e("testing", "Exception: " + e.message)
            }
        }
        super.onMessageReceived(remoteMessage)
    }

    private fun handleDataMessage(json: JSONObject) {
        Log.e("testing", "push json: $json")
        try {
            val data = json.getJSONObject("data")
            val title = data.getString("title")
            val message = data.getString("message")
            Log.e("testing", "message: $message")
            Log.e("testing", "title: $title")
            ShowNotification(title, message)
        } catch (e: JSONException) {
            Log.e("testing", "Json Exception: " + e.message)
        } catch (e: Exception) {
            Log.e("testing", "Exception: " + e.message)
        }
    }

    // Playing notification sound
    fun playNotificationSound() {
        try {
            val alarmSound = Uri.parse(
                ContentResolver.SCHEME_ANDROID_RESOURCE
                        + "://" + this.packageName + "/raw/sound"
            )
            val r = RingtoneManager.getRingtone(this, alarmSound)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun ShowNotification(Title: String, Message: String) {
        Log.e("TAGZ", "inside notification")
        CreateNotificationChannel()
        var resultIntent: Intent? = null
        var stackBuilder: TaskStackBuilder? = null
        resultIntent = Intent(this, MainActivity::class.java)
        stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntentWithParentStack(resultIntent)
        val resultPendingIntent: PendingIntent? = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        val inboxStyle = NotificationCompat.BigTextStyle()
        inboxStyle.bigText(Message)
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.logo_notification)
            .setColor(ContextCompat.getColor(this, R.color.theme))
            .setContentTitle(Title)
            .setStyle(inboxStyle)
            .setContentText(Message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setSound(alarmSound)
            .setContentIntent(resultPendingIntent)
        val notificationManagerCompat = NotificationManagerCompat.from(this)
        notificationManagerCompat.notify(notificationID, builder.build())
        playNotificationSound()
    }

    private fun CreateNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Looxy notifications"
            val description = "include all the Looxy notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(channelID, name, importance)
            notificationChannel.description = description
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

}