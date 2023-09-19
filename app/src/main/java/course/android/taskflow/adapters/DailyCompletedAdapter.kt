package course.android.taskflow.adapters

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import course.android.taskflow.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import course.android.taskflow.constants.DateConstant
import course.android.taskflow.database.Daily
import course.android.taskflow.database.DailyDatabaseHandler
import course.android.taskflow.database.OneTimeDatabaseHandler
import course.android.taskflow.database.OneTimeTask
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DailyCompletedAdapter(private val context: Context, private val data: ArrayList<Daily>) :
    RecyclerView.Adapter<DailyCompletedAdapter.ViewHolder>(){

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val name = itemView.findViewById<TextView>(R.id.tv_name)
            val priority = itemView.findViewById<TextView>(R.id.tv_priority)
            val edit: ImageView = itemView.findViewById<ImageView>(R.id.iv_edit)
            val description = itemView.findViewById<TextView>(R.id.tv_description)
            val checkbox = itemView.findViewById<TextView>(R.id.iv_checkbox)
            val delete = itemView.findViewById<ImageView>(R.id.iv_delete)
            val background = itemView.findViewById<ConstraintLayout>(R.id.cl_background)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)

        val itemView = inflater.inflate(R.layout.item_daily_completed, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        holder.name.text = item.name
        holder.description.text = item.description
        holder.priority.text = item.priority.toString()
        holder.background.isClickable = false



        holder.delete.setOnClickListener {

            val alertDialog = AlertDialog.Builder(context)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes") {dialog, _ ->
                    val db = DailyDatabaseHandler(context)

                    db.deleteTask(item)
                    data.removeAt(holder.adapterPosition)
                    notifyItemRemoved(holder.adapterPosition)

                    Toast.makeText(
                        context,
                        "Task deleted!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .setNegativeButton("No") {dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            alertDialog.show()
        }

        holder.edit.setOnClickListener {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.update_daily_dialog)
            dialog.setCancelable(false)

            val close = dialog.findViewById<ImageView>(R.id.iv_close)
            val name = dialog.findViewById<EditText>(R.id.et_name)
            val priority = dialog.findViewById<EditText>(R.id.et_priority)
            val description = dialog.findViewById<EditText>(R.id.et_description)
            val btnSubmit = dialog.findViewById<Button>(R.id.btn_submit)

            name.setText(item.name)
            priority.setText(item.priority.toString())
            description.setText(item.description)

            close.setOnClickListener {
                dialog.cancel()
            }

            btnSubmit.setOnClickListener {
                if(name.text.isNotEmpty() && priority.text.isNotEmpty()) {
                    val db = DailyDatabaseHandler(context)

                    val newDaily: Daily = Daily(
                        item.id,
                        name = name.text.toString(),
                        priority = priority.text.toString().toInt(),
                        description = description.text.toString(),
                        isCompleted = 0
                    )

                    data[position] = newDaily

                    db.updateDaily(newDaily)
                    notifyItemChanged(position)

                    Toast.makeText(
                        context,
                        "Daily updated successfully.",
                        Toast.LENGTH_SHORT
                    ).show()

                    dialog.cancel()
                }else {
                    Toast.makeText(
                        context,
                        "Name and priority cannot be left empty.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            dialog.show()
        }

        holder.checkbox.setOnClickListener {
            val db = DailyDatabaseHandler(context)

            val item = data[holder.adapterPosition] // Get the item at the current adapter position

            if (item.isCompleted != 1) {
                db.completedDaily(item)
                data.removeAt(holder.adapterPosition)
                notifyItemRemoved(holder.adapterPosition)

                Toast.makeText(context, "Daily completed!", Toast.LENGTH_SHORT).show()
            } else {
                db.resetDaily(item)
                data.removeAt(holder.adapterPosition)
                notifyItemRemoved(holder.adapterPosition)

                Toast.makeText(context, "Daily reset!", Toast.LENGTH_SHORT).show()
            }
        }

    }


    override fun getItemCount(): Int {
        return data.size
    }

}