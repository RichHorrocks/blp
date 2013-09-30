package uk.co.richardhorrocks.blp;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class CommentsDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
                                    MySQLiteHelper.COLUMN_DATE,
                                    MySQLiteHelper.COLUMN_USER_NAME,
                                    MySQLiteHelper.COLUMN_TIME,
                                    MySQLiteHelper.COLUMN_LEVEL,
                                    MySQLiteHelper.COLUMN_VO2 };

    public CommentsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Times createTime(String date, String name, String time, String level, long vo2) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_DATE, date);
        values.put(MySQLiteHelper.COLUMN_USER_NAME, name);
        values.put(MySQLiteHelper.COLUMN_TIME, time);
        values.put(MySQLiteHelper.COLUMN_LEVEL, level);
        values.put(MySQLiteHelper.COLUMN_VO2, vo2);
        long insertId = database.insert(MySQLiteHelper.TABLE_TIMES, 
                                        null,
                                        values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_TIMES,
                                       allColumns, 
                                       MySQLiteHelper.COLUMN_ID + " = " + insertId, 
                                       null,
                                       null, 
                                       null, 
                                       null);
        cursor.moveToFirst();
        Times newTime = cursorToTime(cursor);
        cursor.close();
        return newTime;
    }

    public void deleteTime(Times time) {
        long id = time.getId();
        System.out.println("Time deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_TIMES, 
                        MySQLiteHelper.COLUMN_ID + " = " + id, 
                        null);
    }

    public List<Times> getAllTimes() {
        List<Times> times = new ArrayList<Times>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_TIMES,
                                       allColumns, 
                                       null, 
                                       null, 
                                       null, 
                                       null, 
                                       null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Times time = cursorToTime(cursor);
            times.add(time);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return times;
    }

    public List<Times> getTimesForUser(String name) {
        List<Times> times = new ArrayList<Times>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_TIMES,
                                       allColumns, 
                                       null, 
                                       null, 
                                       null, 
                                       null, 
                                       null);
        cursor.moveToFirst();               
        while (!cursor.isAfterLast()) {
            Times time = cursorToTime(cursor);
            if (time.getName().equals(name)) {
                times.add(time);
            }
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return times;
    }
           
    private Times cursorToTime(Cursor cursor) {
        Times time = new Times();
        time.setId(cursor.getLong(0));
        time.setDate(cursor.getString(1));
        time.setName(cursor.getString(2));
        time.setTime(cursor.getString(3));
        time.setLevel(cursor.getString(4));
        time.setVo2(cursor.getInt(5));
        return time;
    }
} 