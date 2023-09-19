package course.android.taskflow.database

data class Daily(
    val id: Int,
    val priority: Int,
    val name: String,
    val description: String,
    var isCompleted: Int
)