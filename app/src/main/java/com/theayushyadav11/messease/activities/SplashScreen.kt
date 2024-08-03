package com.theayushyadav11.messease.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theayushyadav11.messease.R
import com.theayushyadav11.messease.utils.Mess
import com.theayushyadav11.messease.viewModels.Menu2
import com.theayushyadav11.myapplication.database.MenuDatabase
import com.theayushyadav11.myapplication.models.Menu
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var auth: FirebaseAuth

    companion object {
        var isOpened = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)


        database()

        imageView = findViewById(R.id.imageViewLogo)
        auth = FirebaseAuth.getInstance()

        val fadeAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        imageView.startAnimation(fadeAnimation)
        Handler().postDelayed(Runnable {

            checkFirst()
            if (FirebaseAuth.getInstance().currentUser?.isEmailVerified == true) {
                navigate()
            } else {
                val i = Intent(this@SplashScreen, LoginSignUp::class.java)
                FirebaseAuth.getInstance().signOut()
                startActivity(i)
                finish()
            }


        }, 1000)


    }

    fun database() {
        val db = MenuDatabase.getDatabase(this)
        val menuDao = db.menuDao()
        FirebaseDatabase.getInstance().reference.child("MainMenu")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val m = snapshot.getValue(Menu2::class.java)
                    val menu = m?.let { Menu2(id = "1", menu = it.menu) }
                    GlobalScope.launch {
                        if (menu != null) {
                            menuDao.addMenu(Menu(id = menu.id, menu = menu.menu))
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    navigate()
                }

            })


    }


    object AppPreferences {

        private const val PREF_NAME = "MyAppPreferences"
        private const val FIRST_TIME_LAUNCH = "FirstTimeLaunch"

        private fun getPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }

        fun isFirstTimeLaunch(context: Context): Boolean {
            return getPreferences(context).getBoolean(FIRST_TIME_LAUNCH, true)
        }

        fun setFirstTimeLaunch(context: Context, isFirstTime: Boolean) {
            val editor = getPreferences(context).edit()
            editor.putBoolean(FIRST_TIME_LAUNCH, isFirstTime)
            editor.apply()
        }
    }

    fun checkFirst() {
        if (AppPreferences.isFirstTimeLaunch(this)) {
            // Perform the necessary actions for the first time launch
            //Toast.makeText(this, "Welcome to the app!", Toast.LENGTH_LONG).show()

            // Set the flag to false as the app is no longer opening for the first time
            AppPreferences.setFirstTimeLaunch(this, false)
        } else {
            // Perform actions for subsequent launches
            //Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show()
        }
    }

    fun navigate() {
        FirebaseDatabase.getInstance().reference.child("Users")
            .child(auth.currentUser?.uid.toString()).child("details").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (auth.currentUser != null && snapshot.value == null) {
                    val i = Intent(this@SplashScreen, LoginSignUp::class.java)
                    i.putExtra("ll", 1)
                    startActivity(i)
                    finish()
                } else {
                    startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Mess(this@SplashScreen).toast("Network error!")
            }

        })
    }
}
