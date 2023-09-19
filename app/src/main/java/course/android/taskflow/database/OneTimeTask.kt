package course.android.taskflow.database

data class OneTimeTask(
    val id: Int,
    val priority: Int,
    val name: String,
    val description: String
)
