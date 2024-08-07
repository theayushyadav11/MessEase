package com.theayushyadav11.messease.activities

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theayushyadav11.messease.R
import com.theayushyadav11.messease.databinding.ActivityMainBinding
import com.theayushyadav11.messease.utils.AlarmReceiver
import com.theayushyadav11.messease.utils.Mess
import com.theayushyadav11.messease.viewModels.Menu2
import com.theayushyadav11.myapplication.database.MenuDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var alarmManager: AlarmManager
    private val REQUEST_CODE_POST_NOTIFICATIONS = 1
    private val REQUEST_CODE_SCHEDULE_EXACT_ALARM = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        savedatabase()
        createNotificationChannel()
        askForNotificationPermission()


    }
    fun savedatabase() {
        val db = MenuDatabase.getDatabase(this)
        val menuDao = db.menuDao()
        FirebaseDatabase.getInstance().reference.child("MainMenu")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val m = snapshot.getValue(Menu2::class.java)
                    val menu = m?.let { Menu2(id = "1", menu = it.menu) }
                    GlobalScope.launch {
                        if (menu != null) {
                            menuDao.addMenu(
                                com.theayushyadav11.myapplication.models.Menu(
                                    id = menu.id,
                                    menu = menu.menu
                                )
                            )
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })


    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {

                Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.action_review -> {

                Toast.makeText(this, "Write Review clicked", Toast.LENGTH_SHORT).show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun init() {
        setSupportActionBar(binding.appBarMain.toolbar)
        drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val headerView: View = navView.getHeaderView(0)
        val  layout: LinearLayout = headerView.findViewById(R.id.navMain)
        val requestOptions = RequestOptions.circleCropTransform()
        Glide.with(this)
            .load(FirebaseAuth.getInstance().currentUser?.photoUrl)
            .apply(requestOptions)
            .into( layout.findViewById<ImageView>(R.id.propic))
        layout.findViewById<TextView>(R.id.name).text = FirebaseAuth.getInstance().currentUser?.displayName
        layout.findViewById<TextView>(R.id.email).text = FirebaseAuth.getInstance().currentUser?.email









        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val menu: Menu = navView.menu
        val menuItem1 = menu.findItem(R.id.nav_messCommitteeActivity)
        val menuItem2 = menu.findItem(R.id.loggedin)
        val menuItem3 = menu.findItem(R.id.nav_admin)
        if (FirebaseAuth.getInstance().currentUser?.email == "lit2023049@iiitl.ac.in")
        {
            menuItem3.isVisible = true
        }

        if (FirebaseAuth.getInstance().currentUser == null) {
            menuItem1.isVisible = true
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_home,
                    R.id.nav_messCommitteeActivity,
                    R.id.nav_slideshow,
                    R.id.nav_admin,
                    R.id.nav_logout
                ), drawerLayout
            )
        } else {
            menuItem2.isVisible = true
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_home,
                    R.id.loggedin,
                    R.id.nav_slideshow,
                    R.id.nav_admin,
                    R.id.nav_logout
                ), drawerLayout
            )
        }

        setSupportActionBar(binding.appBarMain.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    signOut()

                    true
                }
                R.id.nav_download->{

                    FirebaseDatabase.getInstance().reference.child("ayush").addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val pdfUrl = snapshot.value.toString()
                            val uri = (Uri.parse(pdfUrl))
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }

                        override fun onCancelled(error: DatabaseError) {
                           Mess(this@MainActivity).toast(error.message)
                        }

                    })


                    true
                }
                else -> {
                    navController.navigate(menuItem.itemId)
                    drawerLayout.closeDrawers()
                    true
                }
            }
        }
    }

    fun signOut() {
        var mAuth = FirebaseAuth.getInstance()


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mAuth.signOut()

        mGoogleSignInClient.signOut().addOnCompleteListener(this) {

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginSignUp::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()

        }
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "getString(R.string.channel_name)"
            val descriptionText = "getString(R.string.channel_description)"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("DailyNotification", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setAlarm() {
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this@MainActivity, AlarmReceiver::class.java)

        val times = listOf("08:00","12:00","16:30","19:30")
        for (i in 0 until times.size) {

            val calendar = Calendar.getInstance()
            val c = Calendar.getInstance()
            val timeParts = times[i].split(":")
            calendar.set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
            calendar.set(Calendar.MINUTE,timeParts[1].toInt())
            calendar.set(Calendar.SECOND, 0)
            if (calendar.timeInMillis < System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
            intent.putExtra("type", i)
            val pendingIntent = PendingIntent.getBroadcast(
                this@MainActivity,
                times[i].hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )


            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }

    }

    private fun askForNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_POST_NOTIFICATIONS)
            } else {
                askForExactAlarmPermission()
            }
        } else {
            askForExactAlarmPermission()
        }
    }

    private fun askForExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent().apply {
                    action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    data = Uri.parse("package:$packageName")
                }
                startActivityForResult(intent, REQUEST_CODE_SCHEDULE_EXACT_ALARM)
            } else {
                setAlarm()
            }
        } else {
            setAlarm()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_POST_NOTIFICATIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    askForExactAlarmPermission()
                } else {
                    Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SCHEDULE_EXACT_ALARM) {
            // Check if the permission is granted
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (alarmManager.canScheduleExactAlarms()) {
                setAlarm()
            } else {
                Toast.makeText(this, "Exact alarm permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}