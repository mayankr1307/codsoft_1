package course.android.taskflow.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class DailyDatabaseHandler(context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    companion object {
        private const val DATABASE_VERSION = 3
        private const val DATABASE_NAME = "DailyDatabase"
        private const val TABLE_CONTACTS = "DailyTable"

        private const val KEY_ID = "_id"
        private const val KEY_NAME = "name"
        private const val KEY_PRIORITY = "priority"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_ISCOMPLETED = "isCompleted"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_CONTACTS + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_NAME + " TEXT," +
                KEY_PRIORITY + " INTEGER," +
                KEY_ISCOMPLETED + " INTEGER," +
                KEY_DESCRIPTION + " TEXT" + ")")

        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS)
        onCreate(db)
    }

    fun addDaily(daily: Daily): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, daily.name)
        contentValues.put(KEY_PRIORITY, daily.priority)
        contentValues.put(KEY_DESCRIPTION, daily.description)
        contentValues.put(KEY_ISCOMPLETED, daily.isCompleted)

        val success = db.insert(TABLE_CONTACTS, null, contentValues)

        db.close()
        return success
    }

    fun getPendingDailies(): ArrayList<Daily> {
        val pendingDailiesList = ArrayList<Daily>()

        val selectQuery = "SELECT * FROM $TABLE_CONTACTS WHERE $KEY_ISCOMPLETED = 0"

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

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID))
                name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME))
                description = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION))
                priority = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PRIORITY))

                val daily = Daily(
                    id = id,
                    name = name,
                    priority = priority,
                    description = description,
                    isCompleted = 0
                )
                pendingDailiesList.add(daily)
            } while (cursor.moveToNext())
        }

        return pendingDailiesList
    }


    fun getCompletedDailies(): ArrayList<Daily> {
        val completedDailiesList = ArrayList<Daily>()

        val selectQuery = "SELECT * FROM $TABLE_CONTACTS WHERE $KEY_ISCOMPLETED = 1"

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

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID))
                name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME))
                description = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION))
                priority = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PRIORITY))

                val daily = Daily(
                    id = id,
                    name = name,
                    priority = priority,
                    description = description,
                    isCompleted = 1
                )
                completedDailiesList.add(daily)
            } while (cursor.moveToNext())
        }

        return completedDailiesList
    }


    fun viewDaily() : ArrayList<Daily> {
        val dailiesList = ArrayList<Daily>()

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

                val daily = Daily(
                    id = id, name = name, priority = priority, description = description, isCompleted = 0)
                dailiesList.add(daily)
            } while (cursor.moveToNext())
        }

        return dailiesList
    }

    fun updateDaily(daily: Daily): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, daily.name)
        contentValues.put(KEY_PRIORITY, daily.priority)
        contentValues.put(KEY_DESCRIPTION, daily.description)
        contentValues.put(KEY_ISCOMPLETED, daily.isCompleted)

        val success = db.update(
            TABLE_CONTACTS, contentValues , KEY_ID + "=" + daily.id, null)
        db.close()

        return success
    }

    fun completedDaily(daily: Daily): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_ISCOMPLETED, 1)

        val success = db.update(
            TABLE_CONTACTS, contentValues, KEY_ID + "=" + daily.id, null
        )

        db.close()

        return success
    }

    fun resetDaily(daily: Daily): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_ISCOMPLETED, 0)

        val success = db.update(
            TABLE_CONTACTS, contentValues, KEY_ID + "=" + daily.id, null
        )

        db.close()

        return success
    }

    fun resetAll(): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_ISCOMPLETED, 0)

        val success = db.update(TABLE_CONTACTS, contentValues, null, null)

        db.close()

        return success
    }

    fun deleteTask(daily: Daily): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_ID, daily.id)

        val success = db.delete(TABLE_CONTACTS, KEY_ID + "=" + daily.id, null)
        db.close()

        return success
    }

}