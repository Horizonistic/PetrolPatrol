package com.adjectitious.android.petrolpatrol;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.adjectitious.android.petrolpatrol.sql.DatabaseContract;
import com.adjectitious.android.petrolpatrol.sql.DatabaseHelper;

import java.text.DecimalFormat;

public class SwitchCars extends AppCompatActivity {
    private static final String TAG = "SwitchCars";
    private Context context;

    protected DatabaseHelper dbHelper;
    protected SQLiteDatabase db;
    protected Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_cars);

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mainToolbar.setTitle(getString(R.string.switch_cars_title));
        setSupportActionBar(mainToolbar);

        this.context = getApplicationContext();

        this.dbHelper = new DatabaseHelper(getApplicationContext());
        this.db = this.dbHelper.getWritableDatabase();
        View form = findViewById(R.id.add_car_form);
        if (!dbHelper.isTableEmpty(db, DatabaseContract.carTable.TABLE_NAME))
        {
            form.setVisibility(View.GONE);
            this.displayCarsList();
        }
    }

    public void displayCarsList()
    {
        this.dbHelper = new DatabaseHelper(getApplicationContext());
        this.db = this.dbHelper.getWritableDatabase();

//        if (!dbHelper.doesTableExist(this.db, DatabaseContract.carTable.TABLE_NAME))
//        {
//            dbHelper.onCreate(db);
//        }

        // For finding the active car
//        String[] whereArgs = new String[] {"1"};
//        this.cursor = this.db.query(
//                DatabaseContract.carTable.TABLE_NAME,               // The table to query
//                null,                                               // The columns to return
//                "active = ?",                                               // The columns for the WHERE clause
//                whereArgs,                                               // The values for the WHERE clause
//                null,                                               // don't group the rows
//                null,                                               // don't filter by row groups
//                null                                                // The sort order
//        );

        this.cursor = this.db.query(
                DatabaseContract.carTable.TABLE_NAME,               // The table to query
                null,                                               // The columns to return
                null,                                               // The columns for the WHERE clause
                null,                                               // The values for the WHERE clause
                null,                                               // don't group the rows
                null,                                               // don't filter by row groups
                null                                                // The sort order
        );

        final int colorBlack;
        final int colorGrayHighlight;
        final int colorPrimaryBackground;
        final int colorGreenActive;
        final int colorGreenActiveHighlight;

        // If Android version is recent enough to not use deprecated methods, then use them... otherwise, be that way
        if (Build.VERSION.SDK_INT >= 23)
        {
            colorBlack = this.context.getColor(R.color.black);
            colorGrayHighlight = this.context.getColor(R.color.grayHighlight);
            colorPrimaryBackground = this.context.getColor(R.color.primaryBackground);
            colorGreenActive = this.context.getColor(R.color.greenActive);
            colorGreenActiveHighlight = this.context.getColor(R.color.greenActiveHighlight);
        }
        else
        {
            colorBlack = getResources().getColor(R.color.black);
            colorGrayHighlight = getResources().getColor(R.color.grayHighlight);
            colorPrimaryBackground = getResources().getColor(R.color.primaryBackground);
            colorGreenActive = getResources().getColor(R.color.greenActive);
            colorGreenActiveHighlight = getResources().getColor(R.color.greenActiveHighlight);
        }

        boolean active = false;
        LinearLayout list = (LinearLayout) findViewById(R.id.list);
        list.removeAllViews();
        if (this.cursor.moveToFirst())
        {
            while (!this.cursor.isAfterLast())
            {
                if (this.cursor.getInt(this.cursor.getColumnIndex(DatabaseContract.carTable.COLUMN_ACTIVE)) == 1)
                {
                    active = true;
                }
                else
                {
                    active = false;
                }
                final LinearLayout layout = new LinearLayout(this.context);
                layout.setId(R.id.list_subitem);
                layout.setOrientation(LinearLayout.VERTICAL);
                ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layout.setLayoutParams(params);
                layout.setPadding(0, 25, 0, 25);
                layout.setLongClickable(true);
                layout.setClickable(true);
                layout.setOnLongClickListener(new SwitchCars.DeleteOnLongClickListener(this.cursor.getInt(this.cursor.getColumnIndex(DatabaseContract.carTable._ID))));
                layout.setOnClickListener(new SwitchCars.SelectOnClickListener(this.cursor.getInt(this.cursor.getColumnIndex(DatabaseContract.carTable._ID))));
                if (active)
                {
                    layout.setBackgroundColor(colorGreenActive);
                    layout.setOnTouchListener(new View.OnTouchListener()
                    {
                        @Override
                        public boolean onTouch(View v, MotionEvent event)
                        {
                            if (event.getAction() == MotionEvent.ACTION_DOWN)
                            {
                                layout.setBackgroundColor(colorGreenActiveHighlight);
                                return false;
                            }
                            else if (event.getAction() == MotionEvent.ACTION_UP)
                            {
                                layout.setBackgroundColor(colorPrimaryBackground);
                                return false;
                            }
                            return true;
                        }
                    });
                }
                else
                {
                    layout.setOnTouchListener(new View.OnTouchListener()
                    {
                        @Override
                        public boolean onTouch(View v, MotionEvent event)
                        {
                            if (event.getAction() == MotionEvent.ACTION_DOWN)
                            {
                                layout.setBackgroundColor(colorGrayHighlight);
                                return false;
                            }
                            else if (event.getAction() == MotionEvent.ACTION_UP)
                            {
                                layout.setBackgroundColor(colorPrimaryBackground);
                                return false;
                            }
                            return true;
                        }
                    });
                }

                list.addView(layout);

                // Car Name
                TextView date = new TextView(this.context);
                date.setLayoutParams(params);
                date.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                date.setTextSize(context.getResources().getDimension(R.dimen.header_font_size));
                date.setTypeface(null, Typeface.BOLD);
                date.setTextColor(colorBlack);
                date.setText(this.cursor.getString(this.cursor.getColumnIndex(DatabaseContract.carTable.COLUMN_NAME_NAME)));
                date.setVisibility(View.VISIBLE);
                layout.addView(date);

                this.cursor.moveToNext();
            }
        }
    }

    public void submitCar(View view)
    {
        String carName = ((TextView) findViewById(R.id.text_add_car)).getText().toString();

        TextView errorText = (TextView) findViewById(R.id.text_fields_errors);
        errorText.setText("");

        boolean emptyField = false;

        if (!carName.isEmpty())
        {
            if (carName.isEmpty())
            {
                emptyField = true;
                errorText.append(getString(R.string.text_car_error));
                errorText.append(getString(R.string.text_newline));
            }

            if (emptyField)
            {
                errorText.setVisibility(View.VISIBLE);
                return;
            }

            this.dbHelper = new DatabaseHelper(getApplicationContext());
            this.db = this.dbHelper.getWritableDatabase();

            ContentValues reset = new ContentValues();
            reset.put(DatabaseContract.carTable.COLUMN_ACTIVE, false);
            if (!dbHelper.isTableEmpty(db, DatabaseContract.carTable.TABLE_NAME))
            {
                db.update(DatabaseContract.carTable.TABLE_NAME, reset, null, null);
            }

            ContentValues values = new ContentValues();
            values.put(DatabaseContract.carTable.COLUMN_NAME_NAME, carName);
            values.put(DatabaseContract.carTable.COLUMN_ACTIVE, true);

            long newRowId;
            newRowId = db.insert(
                    DatabaseContract.carTable.TABLE_NAME,
                    null,
                    values);

            Log.d(TAG, Long.toString(newRowId));

            this.toggleAddNewCar(view);

            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            displayCarsList();
        }
        return;
    }

    public void flushCarTable(View view)
    {
        this.dbHelper = new DatabaseHelper(getApplicationContext());
        this.db = this.dbHelper.getWritableDatabase();

        if (!dbHelper.isTableEmpty(db, DatabaseContract.carTable.TABLE_NAME))
        {
            db.delete(DatabaseContract.carTable.TABLE_NAME, null, null);
        }

        return;
    }

    public void toggleAddNewCar(View view)
    {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.add_car_form);
        Button button = (Button) findViewById(R.id.add_new_car_button);
        EditText editText = (EditText) findViewById(R.id.text_add_car);
        if (layout.getVisibility() == View.VISIBLE)
        {
            layout.setVisibility(View.GONE);
            button.setText(R.string.add_new_car_button);
        }
        else
        {
            editText.requestFocus();
            layout.setVisibility(View.VISIBLE);
            button.setText(R.string.cancel);
        }
    }

    private class DeleteOnLongClickListener implements View.OnLongClickListener
    {
        public DatabaseHelper dbHelper;
        public SQLiteDatabase db;
        public int position;

        public DeleteOnLongClickListener(int position)
        {
            this.dbHelper = new DatabaseHelper(getApplicationContext());
            this.db = this.dbHelper.getWritableDatabase();
            this.position = position;
        }

        @Override
        public boolean onLongClick(View v)
        {
            Log.d(SwitchCars.TAG, "onLongClick deleting...");
            // TODO: Add an edit option
            AlertDialog dialog = new AlertDialog.Builder(SwitchCars.this).create();
            dialog.setTitle(R.string.delete_entry_title);
            dialog.setMessage(getString(R.string.delete_warning));
            displayCarsList();
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.confirm),
                    new SwitchCars.DeleteOnLongClickListener.DialogTest(this.position)
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // Get the ID of the row to delete to check if it was active, then delete
                            String[] columnsSelect = new String[]{DatabaseContract.carTable.COLUMN_ACTIVE};
                            String whereDelete = DatabaseContract.carTable._ID + "= ?";
                            String[] valuesDelete = new String[]{String.valueOf(this.position)};

                            Cursor cursorDelete = db.query(DatabaseContract.carTable.TABLE_NAME,
                                    columnsSelect, whereDelete, valuesDelete, null, null, null);
                            cursorDelete.moveToFirst();
                            int wasActiveCar = cursorDelete.getInt(0);

                            db.delete(DatabaseContract.carTable.TABLE_NAME, whereDelete, valuesDelete);

                            Log.d(SwitchCars.TAG, "Deleting car ID: " + String.valueOf(this.position));


                            // First see if the car deleted was the active car
                            // If it was the active car, set the top-most car as the new active
                            if (wasActiveCar != 0) {
                                Cursor cursorUpdate = db.rawQuery("SELECT MIN(" +
                                        DatabaseContract.carTable._ID + ") FROM " +
                                        DatabaseContract.carTable.TABLE_NAME, null);
                                cursorUpdate.moveToFirst();
                                int lowestID = cursorUpdate.getInt(0);

                                ContentValues active = new ContentValues();
                                active.put(DatabaseContract.carTable.COLUMN_ACTIVE, true);

                                String whereUpdate = DatabaseContract.carTable._ID + "= ?";
                                String[] valuesUpdate = new String[]{String.valueOf(lowestID)};

                                db.update(DatabaseContract.carTable.TABLE_NAME, active, whereUpdate, valuesUpdate);
                            }

                            displayCarsList();
                        }
                    });

            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel),
                    new SwitchCars.DeleteOnLongClickListener.DialogTest(this.position)
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    });
            dialog.show();
            return true;
        }

        public class DialogTest implements DialogInterface.OnClickListener
        {
            public DatabaseHelper dbHelper;
            public SQLiteDatabase db;
            public int position;

            public DialogTest(int position)
            {
                this.dbHelper = new DatabaseHelper(getApplicationContext());
                this.db = this.dbHelper.getWritableDatabase();
                this.position = position;
            }

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
            }
        }
    }

    private class SelectOnClickListener implements View.OnClickListener
    {
        DatabaseHelper dbHelper;
        SQLiteDatabase db;
        int position;

        SelectOnClickListener(int position)
        {
            this.dbHelper = new DatabaseHelper(getApplicationContext());
            this.db = this.dbHelper.getWritableDatabase();
            this.position = position;
        }

        @Override
        public void onClick(View v)
        {
            Log.d(SwitchCars.TAG, "onClick, making active...");

            ContentValues reset = new ContentValues();
            reset.put(DatabaseContract.carTable.COLUMN_ACTIVE, false);
            if (!dbHelper.isTableEmpty(db, DatabaseContract.carTable.TABLE_NAME))
            {
                db.update(DatabaseContract.carTable.TABLE_NAME, reset, null, null);
                ContentValues active = new ContentValues();

                String where = DatabaseContract.carTable._ID + "= ?";
                String[] values = new String[]{String.valueOf(this.position)};
                active.put(DatabaseContract.carTable.COLUMN_ACTIVE, true);
                db.update(DatabaseContract.carTable.TABLE_NAME, active, where, values);
            }

            displayCarsList();
        }
    }
}
