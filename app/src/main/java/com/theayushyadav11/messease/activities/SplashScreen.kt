package com.theayushyadav11.messease.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theayushyadav11.messease.R
import com.theayushyadav11.myapplication.database.MenuDatabase
import com.theayushyadav11.myapplication.models.DayMenu
import com.theayushyadav11.myapplication.models.Menu
import com.theayushyadav11.myapplication.models.Particulars
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var auth:FirebaseAuth
    companion object{
        var isOpened=0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)


        database()

        imageView = findViewById(R.id.imageViewLogo)
        auth=FirebaseAuth.getInstance()

        val fadeAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        imageView.startAnimation(fadeAnimation)
               Handler().postDelayed(Runnable {

            checkFirst()
            if(FirebaseAuth.getInstance().currentUser?.isEmailVerified == true)
            {
              navigate()
            }
            else
            {
                val i = Intent(this@SplashScreen, LoginSignUp::class.java)
                FirebaseAuth.getInstance().signOut()
                startActivity(i)
                finish()
            }



        }, 1000)


    }
    fun database()
    {
        val db = MenuDatabase.getDatabase(this)
        val menuDao = db.menuDao()

// Example DayMenu
        val dayMenu = DayMenu(
            Monday = listOf(
                Particulars("Breakfast", "Pav Bhaji ,Daliya,Milk. Tea Banana /Egg -Ipc Bread Butter", "8:30 A.M. to 10:00 A.M."),
                Particulars("Lunch", "Chhola, Poori,Curd,Jeera Rice", "12:30 P.M. to 2:30 P.M."),
                Particulars("Snacks", "Dry Maggie\n" +
                        "Tea", "5:00 P.M. to 6:00 P.M."),
                Particulars("Dinner", "Masoor Dal\n" +
                        "Fry,Lauki\n" +
                        "masala,Roti,Rice", "7:30 P.M. to 9:30 P.M.")
            ),
            Tuesday = listOf(
                Particulars("Breakfast", "Medu vada, coconut\n" +
                        "chutney, sambhar,\n" +
                        "milk, tea, Banana,\n" +
                        "Bread, butter/jam", "8:30 A.M. to 10:00 A.M."),
                Particulars("Lunch", "Rajma Masala,\n" +
                        "Onion Raita, Roti,\n" +
                        "Jeera Rice", "12:30 P.M. to 2:30 P.M."),
                Particulars("Snacks", "Macaroni, Tea", "5:00 P.M. to 6:00 P.M."),
                Particulars("Dinner", "Aloo kurma,Veg\n" +
                        "pulao,Roti", "7:30 P.M. to 9:30 P.M.")
            ),
            Wednesday  = listOf(
                Particulars("Breakfast", "Aloo paratha,\n" +
                        "curd, milk, tea,\n" +
                        "Banana/egg,\n" +
                        "Bread, Butter/jam", "8:30 A.M. to 10:00 A.M."),
                Particulars("Lunch", "Roti, Rice, Black chana\n" +
                        "masala,aloo cabbage\n" +
                        "matar", "12:30 P.M. to 2:30 P.M."),
                Particulars("Snacks", "Chilly Idli,Tea", "5:00 P.M. to 6:00 P.M."),
                Particulars("Dinner", "paneer butter\n" +
                        "jilebiDal makhani", "7:30 P.M. to 9:30 P.M.")
            ),
            Thursday = listOf(
                Particulars("Breakfast", "poha,green\n" +
                        "chutney , Banana\n" +
                        "legg,Milk,Tea,Br\n" +
                        "ead,Butteer/Jam", "8:30 A.M. to 10:00 A.M."),
                Particulars("Lunch", "Crunchy Jeera Aloo\n" +
                        "Kadhi Pyaaz Pakora\n" +
                        "Roti, Rice", "12:30 P.M. to 2:30 P.M."),
                Particulars("Snacks", "Chole Samosa,\n" +
                        "Tea", "5:00 P.M. to 6:00 P.M."),
                Particulars("Dinner", "pyaaz\n" +
                        "kofta,Rice,Roti,\n" +
                        "Dhaba dal", "7:30 P.M. to 9:30 P.M.")
            ),
            Friday = listOf(
                Particulars("Breakfast", "Paratha, white\n" +
                        "matar sabji, Milk,\n" +
                        "Tea, Banana/egg,\n" +
                        "Bread, butter/jam", "8:30 A.M. to 10:00 A.M."),
                Particulars("Lunch", "Bhindi Fry,Masoor dal Fry,\n" +
                        "Roti,Rice", "12:30 P.M. to 2:30 P.M."),
                Particulars("Snacks", "Peri peri fries,\n" +
                        "ketchup, tea", "5:00 P.M. to 6:00 P.M."),
                Particulars("Dinner", "Aloo tomato,\n" +
                        "Vegetable Tehri, roti,\n" +
                        "raita", "7:30 P.M. to 9:30 P.M.")
            ),
            Saturday = listOf(
                Particulars("Breakfast", "Utappam, sambhar,\n" +
                        "coconut chutney,\n" +
                        "Milk, tea,\n" +
                        "banana/egg, Bread,\n" +
                        "butter/jam", "8:30 A.M. to 10:00 A.M."),
                Particulars("Lunch", "Pindi Choley\n" +
                        "Bathure\n" +
                        "Rice\n" +
                        "Boondi Raita", "12:30 P.M. to 2:30 P.M."),
                Particulars("Snacks", "Biscuits(5pc),\n" +
                        "Tea", "5:00 P.M. to 6:00 P.M."),
                Particulars("Dinner", "Veg\n" +
                        "Jalfrezi,Arhar\n" +
                        "Dal Fry\n" +
                        "Roti, JeeraRice", "7:30 P.M. to 9:30 P.M.")
            ),
            Sunday = listOf(
                Particulars("Breakfast", "Aloo Poori,\n" +
                        "I pc-Banana/\n" +
                        "Egg-I pc\n" +
                        "bread\n" +
                        "Butter jam", "8:30 A.M. to 10:00 A.M."),
                Particulars("Lunch", "Vegetable Biryani Dal\n" +
                        "Makhani Tawa Paratha,\n" +
                        "Raita", "12:30 P.M. to 2:30 P.M."),
                Particulars("Snacks", "Punugulu, Toma\n" +
                        "to Chutney,\n" +
                        "Tea", "5:00 P.M. to 6:00 P.M."),
                Particulars("Dinner", "Gulab Jamun/lce\n" +
                        "cream,Jeera\n" +
                        "Rice, Roti,Kadai\n" +
                        "Paneer,Punjabi\n" +
                        "Dal Tadka", "7:30 P.M. to 9:30 P.M.")
            ),

            )
        //        val textView: TextView = binding.textGallery
//        galleryViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

// Create a Menu object
        val menu = Menu(id = "1", menu = dayMenu)

// Insert the menu into the database
        GlobalScope.launch {
            menuDao.addMenu(menu)

            // Retrieve the menu from the database
            val retrievedMenu = menuDao.getMenu()
            Log.d("Menu", retrievedMenu.toString())
        }

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
    fun checkFirst()
    {
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
    fun navigate()
    {
        FirebaseDatabase.getInstance().reference.child("Users").child(auth.currentUser?.uid.toString()).child("details").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(auth.currentUser!=null&&snapshot.value==null) {
                    val i =Intent(this@SplashScreen,LoginSignUp::class.java)
                    i.putExtra("ll",1)
                    startActivity(i)
                    finish()
                }
                else{
                    startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                  finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}
