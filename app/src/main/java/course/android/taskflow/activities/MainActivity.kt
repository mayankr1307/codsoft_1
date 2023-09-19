package course.android.taskflow.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import course.android.taskflow.R
import course.android.taskflow.constants.DateConstant
import course.android.taskflow.constants.Quotes
import course.android.taskflow.database.DailyDatabaseHandler
import course.android.taskflow.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private var quotes = Quotes.motivationalQuotes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val sharedPreferences = applicationContext.getSharedPreferences(
            DateConstant.PREFS_NAME, Context.MODE_PRIVATE)

        val flag = sharedPreferences.getBoolean(DateConstant.FIRST_TIME, true)

        if(flag) {
            storeDate()
        }else {
            updateDate()
        }

        setupQuote()

        binding?.flStart?.setOnClickListener {
            val intent = Intent(this@MainActivity, ShowTasksActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateDate() {
        val sharedPreferences = applicationContext.getSharedPreferences(
            DateConstant.PREFS_NAME, Context.MODE_PRIVATE)

        val date = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateString = dateFormat.format(date)

        val savedDate = sharedPreferences.getString(DateConstant.KEY_DATE, "")

        if(savedDate!!.isNotEmpty() && savedDate != dateString) {
            sharedPreferences.edit().putString(DateConstant.KEY_DATE, dateString).apply()

            val db = DailyDatabaseHandler(this)
            db.resetAll()
        }
    }

    private fun storeDate() {
        val sharedPreferences = applicationContext.getSharedPreferences(
            DateConstant.PREFS_NAME, Context.MODE_PRIVATE)

        val date = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateString = dateFormat.format(date)

        sharedPreferences.edit().putString(DateConstant.KEY_DATE, dateString).apply()
        sharedPreferences.edit().putBoolean(DateConstant.FIRST_TIME, false).apply()

    }

    private fun setupQuote() {
        val random = Random()
        val randomNumber = random.nextInt(20)
        binding?.tvQuote?.text = "${quotes[randomNumber]}"
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}