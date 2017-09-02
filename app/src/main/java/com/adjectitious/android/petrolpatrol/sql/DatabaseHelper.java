package com.adjectitious.android.petrolpatrol.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final String TAG = "DatabaseHelper";
    // If you change the database schema, you must increment the database version.

    public DatabaseHelper(Context context)
    {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(DatabaseContract.carTable.SQL_CREATE_ENTRIES);
        db.execSQL(DatabaseContract.gasTable.SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(DatabaseContract.gasTable.SQL_DROP_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void createGasTable(SQLiteDatabase db, String tableName)
    {
        String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS" + DatabaseContract.gasTable.TABLE_NAME + " (" +
                        DatabaseContract.gasTable._ID + " INTEGER PRIMARY KEY," +
                        DatabaseContract.gasTable.COLUMN_NAME_DATE + DatabaseContract.TEXT_TYPE + DatabaseContract.COMMA_SEP +
                        DatabaseContract.gasTable.COLUMN_NAME_NAME + DatabaseContract.TEXT_TYPE + DatabaseContract.COMMA_SEP +
                        DatabaseContract.gasTable.COLUMN_NAME_PRICE + DatabaseContract.REAL_TYPE + DatabaseContract.COMMA_SEP +
                        DatabaseContract.gasTable.COLUMN_NAME_GALLONS + DatabaseContract.REAL_TYPE + DatabaseContract.COMMA_SEP +
                        DatabaseContract.gasTable.COLUMN_NAME_MILEAGE + DatabaseContract.INTEGER_TYPE +
                        " )";
    }

    public void deleteCarTable(SQLiteDatabase db)
    {
        Log.d("Delete pre", TAG);
        db.rawQuery(DatabaseContract.carTable.SQL_DELETE_ENTRIES, null);
        Log.d("Delete post", TAG);
    }

    public boolean doesTableExist(SQLiteDatabase db, String tableName)
    {
        Cursor cursor = db.rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = '" + tableName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public boolean isTableEmpty(SQLiteDatabase db, String tableName)
    {
        Boolean flag;
        if (doesTableExist(db, tableName)) {
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableName, null);
            // If can move to first then table is not empty, return false
            flag = !cursor.moveToFirst();
            cursor.close();
        }
        else
        {
            flag = false;
        }
        return flag;
    }
}