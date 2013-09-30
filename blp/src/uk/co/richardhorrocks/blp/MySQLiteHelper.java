package uk.co.richardhorrocks.blp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "times.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_TIMES = "times";
    
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_LEVEL = "level";
    public static final String COLUMN_VO2 = "vo2";

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
      + TABLE_TIMES + "(" 
      + COLUMN_ID + " integer primary key autoincrement, " 
      + COLUMN_DATE + " text not null, "
      + COLUMN_USER_NAME + " text not null, "
      + COLUMN_TIME + " text not null, "
      + COLUMN_LEVEL + " text not null,"
      + COLUMN_VO2 + " integer not null);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMES);
        onCreate(db);
    }
} 