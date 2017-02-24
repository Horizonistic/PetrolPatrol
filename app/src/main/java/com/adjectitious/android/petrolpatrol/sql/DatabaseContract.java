package com.adjectitious.android.petrolpatrol.sql;

import android.provider.BaseColumns;

public final class DatabaseContract
{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Gas.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String REAL_TYPE = " REAL";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private DatabaseContract() {}

    /* Inner class that defines the table contents */
    public static abstract class gasTable implements BaseColumns
    {
        public static final String TABLE_NAME = "gasinfo";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_GALLONS = "gallons";
        public static final String COLUMN_NAME_MILEAGE = "mileage";
        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + gasTable.TABLE_NAME + " (" +
                        gasTable._ID + " INTEGER PRIMARY KEY," +
                        gasTable.COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP +
                        gasTable.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                        gasTable.COLUMN_NAME_PRICE + REAL_TYPE + COMMA_SEP +
                        gasTable.COLUMN_NAME_GALLONS + REAL_TYPE + COMMA_SEP +
                        gasTable.COLUMN_NAME_MILEAGE + INTEGER_TYPE +
                " )";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + gasTable.TABLE_NAME;
    }
}