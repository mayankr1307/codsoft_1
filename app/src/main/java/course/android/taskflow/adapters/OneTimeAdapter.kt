package course.android.taskflow.adapters

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import course.android.taskflow.R
import course.android.taskflow.database.OneTimeDatabaseHandler
import course.android.taskflow.database.OneTimeTask

class OneTimeAdapter(private val context: Context, private val data: ArrayList<OneTimeTask>) :
    RecyclerView.Adapter<OneTimeAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.tv_name)
        val priority = itemView.findViewById<TextView>(R.id.tv_priority)
        val edit: ImageView = itemView.findViewById<ImageView>(R.id.iv_edit)
        val description = itemView.findViewById<TextView>(R.id.tv_description)
        val checkbox = itemView.findViewById<CheckBox>(R.id.cb_checkbox)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)

        val itemView = inflater.inflate(R.layout.item_task, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.name.text = item.name
        holder.description.text = item.description
        holder.priority.text = item.priority.toString()

        holder.checkbox.setOnClickListener {
            val db = OneTimeDatabaseHandler(context)
            db.deleteTask(item)
            data.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)

            Toast.makeText(
                context,
                "Task completed!",
                Toast.LENGTH_SHORT
            ).show()
        }

        holder.edit.setOnClickListener {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.update_task_dialog)
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
                    val db = OneTimeDatabaseHandler(context)

                    val newOneTimeTask: OneTimeTask = OneTimeTask(
                        item.id,
                        name = name.text.toString(),
                        priority = priority.text.toString().toInt(),
                        description = description.text.toString()
                    )

                    data[holder.adapterPosition] = newOneTimeTask

                    db.updateTask(newOneTimeTask)
                    notifyItemChanged(holder.adapterPosition)

                    Toast.makeText(
                        context,
                        "Task updated successfully.",
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
    }

    override fun getItemCount(): Int {
        return data.size
    }
}