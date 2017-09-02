package com.adjectitious.android.petrolpatrol.sql;

import android.provider.BaseColumns;

public final class DatabaseContract
{
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "PetrolPatrol.db";
    public static final String TEXT_TYPE = " TEXT";
    public static final String REAL_TYPE = " REAL";
    public static final String INTEGER_TYPE = " INTEGER";
    public static final String COMMA_SEP = ",";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private DatabaseContract() {}

    /* Inner class that defines the table contents */
    public static abstract class gasTable implements BaseColumns
    {
        public static final String TABLE_NAME = "gas";
        public static final String COLUMN_NAME_CAR = "car";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_GALLONS = "gallons";
        public static final String COLUMN_NAME_MILEAGE = "mileage";
        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + gasTable.TABLE_NAME + " (" +
                        gasTable._ID + " INTEGER PRIMARY KEY," +
                        gasTable.COLUMN_NAME_CAR + INTEGER_TYPE + COMMA_SEP +
                        gasTable.COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP +
                        gasTable.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                        gasTable.COLUMN_NAME_PRICE + REAL_TYPE + COMMA_SEP +
                        gasTable.COLUMN_NAME_GALLONS + REAL_TYPE + COMMA_SEP +
                        gasTable.COLUMN_NAME_MILEAGE + INTEGER_TYPE +
                " )";

        public static final String SQL_DROP_TABLE =
                "DROP TABLE IF EXISTS " + gasTable.TABLE_NAME;
    }

    public static abstract class carTable implements BaseColumns
    {
        public static final String TABLE_NAME = "cars";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_ACTIVE = "active";
        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + carTable.TABLE_NAME + " (" +
                        carTable._ID + " INTEGER PRIMARY KEY," +
                        carTable.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                        carTable.COLUMN_ACTIVE + INTEGER_TYPE + " DEFAULT 0)";

        public static final String SQL_DROP_TABLE =
                "DROP TABLE IF EXISTS " + carTable.TABLE_NAME;

        public static final String SQL_DELETE_ENTRIES =
                "DELETE FROM " + carTable.TABLE_NAME;
    }
}