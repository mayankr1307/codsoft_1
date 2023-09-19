package course.android.taskflow.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.selects.select

class OneTimeDatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "OneTimeDatabase"
        private const val TABLE_CONTACTS = "OneTimeTable"

        private const val KEY_ID = "_id"
        private const val KEY_NAME = "name"
        private const val KEY_PRIORITY = "priority"
        private const val KEY_DESCRIPTION = "description"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_CONTACTS + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_NAME + " TEXT," +
                KEY_PRIORITY + " INTEGER," +
                KEY_DESCRIPTION + " TEXT" + ")")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS)
        onCreate(db)
    }

    fun addOneTimeTask(task: OneTimeTask): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, task.name)
        contentValues.put(KEY_PRIORITY, task.priority)
        contentValues.put(KEY_DESCRIPTION, task.description)

        val success = db.insert(TABLE_CONTACTS, null, contentValues)

        db.close()
        return success
    }

    fun viewOneTimeTask(): ArrayList<OneTimeTask> {
        val taskList: ArrayList<OneTimeTask> = ArrayList<OneTimeTask>()

        val selectQuery = "SELECT * FROM $TABLE_CONTACTS"

        val db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var name: String
        var priority: Int
        var description: String
        if(cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID))
                name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME))
                description = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION))
                priority = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PRIORITY))

                val task = OneTimeTask(
                    id = id, name = name, priority = priority, description = description)
                taskList.add(task)
            } while (cursor.moveToNext())
        }

        return taskList
    }

    fun updateTask(task: OneTimeTask): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, task.name)
        contentValues.put(KEY_PRIORITY, task.priority)
        contentValues.put(KEY_DESCRIPTION, task.description)

        val success = db.update(
            TABLE_CONTACTS, contentValues , KEY_ID + "=" + task.id, null)
        db.close()

        return success
    }

    fun deleteTask(task: OneTimeTask): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_ID, task.id)

        val success = db.delete(TABLE_CONTACTS, KEY_ID + "=" + task.id, null)
        db.close()

        return success
    }

}