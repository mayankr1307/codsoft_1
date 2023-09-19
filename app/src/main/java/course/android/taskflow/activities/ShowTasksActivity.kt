package course.android.taskflow.activities

import android.app.Dialog
import android.content.Context
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import course.android.taskflow.R
import course.android.taskflow.adapters.DailyAdapter
import course.android.taskflow.adapters.DailyCompletedAdapter
import course.android.taskflow.adapters.OneTimeAdapter
import course.android.taskflow.database.Daily
import course.android.taskflow.database.DailyDatabaseHandler
import course.android.taskflow.database.OneTimeDatabaseHandler
import course.android.taskflow.database.OneTimeTask
import course.android.taskflow.databinding.ActivityShowTasksBinding
import java.text.SimpleDateFormat
import java.util.*

class ShowTasksActivity : AppCompatActivity() {

    private var binding: ActivityShowTasksBinding? = null
    private var flagDaily = false
    private var flagCompleted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowTasksBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.rgTasks?.setOnCheckedChangeListener{_, checkedId ->
            when(checkedId) {
                R.id.rb_daily_tasks -> {
                    flagDaily = true
                    binding?.svDailiesRvContainer?.visibility = View.VISIBLE
                    binding?.svOnetimeRvContainer?.visibility = View.GONE
                    binding?.ivAdd?.visibility = View.VISIBLE
                    setupListOfDailiesIntoRecyclerView()
                }
                R.id.rb_one_time_tasks -> {
                    flagDaily = false
                    flagCompleted = false
                    binding?.btnViewCompleted?.text = "View Completed"
                    binding?.svDailiesCompletedRvContainer?.visibility = View.GONE
                    binding?.svOnetimeRvContainer?.visibility = View.VISIBLE
                    binding?.svDailiesRvContainer?.visibility = View.GONE
                    binding?.ivAdd?.visibility = View.VISIBLE
                    setupListOfOneTimeTasksIntoRecyclerView()
                }
            }
        }

        binding?.ivAdd?.setOnClickListener {
            when(flagDaily) {
                false -> {
                    openAddOneTimeTaskDialog()
                }
                true -> {
                    addDailyDialog()
                }
            }

        }

        binding?.btnViewCompleted?.setOnClickListener {
            if(!flagDaily) {
                Toast.makeText(
                    this@ShowTasksActivity,
                    "Switch to the dailies tab first.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if(!flagCompleted) {
                flagCompleted = true
                binding?.svDailiesRvContainer?.visibility = View.GONE
                binding?.svDailiesCompletedRvContainer?.visibility = View.VISIBLE
                setupListOfDailiesIntoRecyclerView()
                binding?.btnViewCompleted?.text = "View Pending"

            }else if(flagCompleted) {
                flagCompleted = false
                binding?.svDailiesRvContainer?.visibility = View.VISIBLE
                binding?.svDailiesCompletedRvContainer?.visibility = View.GONE
                setupListOfDailiesIntoRecyclerView()
                binding?.btnViewCompleted?.text = "View Completed"
            }
        }

        setupListOfOneTimeTasksIntoRecyclerView()

    }

    private fun addDailyDialog() {
        val dialog = Dialog(this@ShowTasksActivity)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.add_daily_dialog)

        val etName = dialog.findViewById<EditText>(R.id.et_name)
        val etPriority = dialog.findViewById<EditText>(R.id.et_priority)
        val etDescription = dialog.findViewById<EditText>(R.id.et_description)
        val btnClose = dialog.findViewById<ImageView>(R.id.iv_close)
        val btnSubmit = dialog.findViewById<Button>(R.id.btn_submit)

        btnClose.setOnClickListener {
            dialog.cancel()
        }

        btnSubmit.setOnClickListener {
            if (etName.text.isNotEmpty() && etPriority.text.isNotEmpty()) {

                val name = etName.text.toString()
                val priority = etPriority.text.toString()
                val priorityInt = priority.toInt()
                val description = if(etDescription.text.isEmpty()) {
                    ""
                } else {
                    etDescription.text.toString()
                }

                val databaseHandler: DailyDatabaseHandler = DailyDatabaseHandler(this)

                val status = databaseHandler.addDaily(
                    Daily(
                    0, name = name, priority = priorityInt, description = description, isCompleted = 0)
                )

                if(status > -1) {
                    Toast.makeText(
                        this@ShowTasksActivity,
                        "Daily task created successfully.",
                        Toast.LENGTH_SHORT
                    ).show()

                    setupListOfDailiesIntoRecyclerView()
                } else {
                    Toast.makeText(
                        this@ShowTasksActivity,
                        "Failed to add daily task.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                dialog.cancel()

            } else {
                Toast.makeText(
                    this@ShowTasksActivity,
                    "You cannot leave any field empty.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        dialog.show()
    }

    private fun setupListOfDailiesIntoRecyclerView() {
        val databaseHandler: DailyDatabaseHandler = DailyDatabaseHandler(this)

        if(flagCompleted) {
            val dailyList: ArrayList<Daily> = databaseHandler.getCompletedDailies()
            binding?.rvDailiesCompleted?.layoutManager = LinearLayoutManager(this)
            val itemAdapter = DailyCompletedAdapter(this, dailyList)
            binding?.ivAdd?.visibility = View.GONE
            binding?.rvDailiesCompleted?.adapter = itemAdapter
        }else {
            val dailyList: ArrayList<Daily> = databaseHandler.getPendingDailies()
            binding?.rvDailies?.layoutManager = LinearLayoutManager(this)
            val itemAdapter = DailyAdapter(this, dailyList)
            binding?.ivAdd?.visibility = View.VISIBLE
            binding?.rvDailies?.adapter = itemAdapter
        }
    }


    private fun setupListOfOneTimeTasksIntoRecyclerView() {
        val databaseHandler: OneTimeDatabaseHandler = OneTimeDatabaseHandler(this)

        val oneTimeTaskList = databaseHandler.viewOneTimeTask()

        if(oneTimeTaskList.size > 0) {
            binding?.rvOneTimeTasks?.layoutManager = LinearLayoutManager(this)

            val itemAdapter = OneTimeAdapter(this, oneTimeTaskList)

            binding?.rvOneTimeTasks?.adapter = itemAdapter
        }
    }

    private fun openAddOneTimeTaskDialog() {
        val dialog = Dialog(this@ShowTasksActivity)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.add_task_dialog)

        val etName = dialog.findViewById<EditText>(R.id.et_name)
        val etPriority = dialog.findViewById<EditText>(R.id.et_priority)
        val etDescription = dialog.findViewById<EditText>(R.id.et_description)
        val btnClose = dialog.findViewById<ImageView>(R.id.iv_close)
        val btnSubmit = dialog.findViewById<Button>(R.id.btn_submit)

        btnClose.setOnClickListener {
            dialog.cancel()
        }

        btnSubmit.setOnClickListener {
            if (etName.text.isNotEmpty() && etPriority.text.isNotEmpty()) {

                val name = etName.text.toString()
                val priority = etPriority.text.toString()
                val priorityInt = priority.toInt()
                val description = if(etDescription.text.isEmpty()) {
                    ""
                } else {
                    etDescription.text.toString()
                }

                val databaseHandler: OneTimeDatabaseHandler = OneTimeDatabaseHandler(this)

                val status = databaseHandler.addOneTimeTask(OneTimeTask(
                    0, name = name, priority = priorityInt, description = description))

                if(status > -1) {
                    Toast.makeText(
                        this@ShowTasksActivity,
                        "Task created successfully.",
                        Toast.LENGTH_SHORT
                    ).show()

                    setupListOfOneTimeTasksIntoRecyclerView()
                } else {
                    Toast.makeText(
                        this@ShowTasksActivity,
                        "Failed to add task.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                dialog.cancel()

            } else {
                Toast.makeText(
                    this@ShowTasksActivity,
                    "You cannot leave any field empty.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        dialog.show()
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

}