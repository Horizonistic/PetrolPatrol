package com.adjectitious.android.petrolpatrol;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.database.sqlite.*;

import com.adjectitious.android.petrolpatrol.sql.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

// TODO: Add option to see entries?
// TODO: Implement max limit?  Or some performance control
public class AddEntry extends AppCompatActivity
{
    private static final String TAG = "AddEntry";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mainToolbar.setTitle(getString(R.string.add_entry_title));
        setSupportActionBar(mainToolbar);

        // Autofills date by default
        View dateCheckboxView = findViewById(R.id.text_date_edit_checkbox);
        onCheckBoxToggle(dateCheckboxView);

        // Populates name spinner with options from string array names_array
        Spinner nameSpinner = (Spinner) findViewById(R.id.spinner_name);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.names_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nameSpinner.setAdapter(adapter);

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] returnColumns = new String[] {DatabaseContract.carTable.COLUMN_NAME_NAME, DatabaseContract.carTable.COLUMN_ACTIVE};
        Cursor cursor = db.query(
                DatabaseContract.carTable.TABLE_NAME,               // The table to query
                returnColumns,                                      // The columns to return
                null,                                               // The columns for the WHERE clause
                null,                                               // The values for the WHERE clause
                null,                                               // don't group the rows
                null,                                               // don't filter by row groups
                null                                                // The sort order
        );

        // Populates car spinner with options from car table
        ArrayList<String> carNames = new ArrayList<>();
        int active = 0;
        if (cursor.moveToFirst())
        {
            while (!cursor.isAfterLast())
            {
                carNames.add(cursor.getString(cursor.getColumnIndex(DatabaseContract.carTable.COLUMN_NAME_NAME)));

                if (cursor.getInt(cursor.getColumnIndex(DatabaseContract.carTable.COLUMN_ACTIVE)) == 1)
                {
                    active = cursor.getPosition();
                    Log.d(TAG, "carSpinner position: " + active);
                }

                cursor.moveToNext();
            }
        }
        cursor.close();

        Spinner carSpinner = (Spinner) findViewById(R.id.spinner_car);
        ArrayAdapter<String> adapterCar = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, carNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carSpinner.setAdapter(adapterCar);
        carSpinner.setSelection(active);
    }

    public boolean addEntry(Spinner carSpinner, EditText name, EditText price, EditText gallons, EditText mileage, EditText date)
    {
        String carString = carSpinner.getSelectedItem().toString();
        String dateString = date.getText().toString();
        String nameString = name.getText().toString();
        String priceString = price.getText().toString();
        String gallonsString = gallons.getText().toString();
        String mileageString = mileage.getText().toString();

        TextView errorText = (TextView) findViewById(R.id.text_fields_errors);
        errorText.setVisibility(View.VISIBLE);
        errorText.setText("");

        boolean emptyField = false;

        if (carString == null || dateString.isEmpty())
        {
            emptyField = true;
            errorText.append(getString(R.string.text_car_error));
            errorText.append(getString(R.string.text_newline));
        }
        if (dateString.isEmpty())
        {
            emptyField = true;
            errorText.append(getString(R.string.text_date_error));
            errorText.append(getString(R.string.text_newline));
        }
        if (nameString.isEmpty())
        {
            emptyField = true;
            errorText.append(getString(R.string.text_name_error));
            errorText.append(getString(R.string.text_newline));
        }
        if (priceString.isEmpty())
        {
            emptyField = true;
            errorText.append(getString(R.string.text_price_error));
            errorText.append(getString(R.string.text_newline));
        }
        if (gallonsString.isEmpty())
        {
            emptyField = true;
            errorText.append(getString(R.string.text_gallons_error));
            errorText.append(getString(R.string.text_newline));
        }
        if (mileageString.isEmpty())
        {
            emptyField = true;
            errorText.append(getString(R.string.text_mileage_error));
            errorText.append(getString(R.string.text_newline));
        }

        if (emptyField)
        {
            errorText.setVisibility(View.VISIBLE);
            return false;
        }

        // Create new helper
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] returnColumns = new String[] {DatabaseContract.carTable._ID};
        String[] whereArgs = new String[] {"1"};
        Cursor cursor = db.query(
                DatabaseContract.carTable.TABLE_NAME,               // The table to query
                returnColumns,                                      // The columns to return
                "active = ?",                                       // The columns for the WHERE clause
                whereArgs,                                          // The values for the WHERE clause
                null,                                               // don't group the rows
                null,                                               // don't filter by row groups
                null                                                // The sort order
        );

        // Create insert entries
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.gasTable.COLUMN_NAME_DATE, dateString);
//        values.put(DatabaseContract.gasTable.COLUMN_NAME_CAR, carID);
        values.put(DatabaseContract.gasTable.COLUMN_NAME_NAME, nameString);
        values.put(DatabaseContract.gasTable.COLUMN_NAME_PRICE, priceString);
        values.put(DatabaseContract.gasTable.COLUMN_NAME_GALLONS, gallonsString);
        values.put(DatabaseContract.gasTable.COLUMN_NAME_MILEAGE, mileageString);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                DatabaseContract.gasTable.TABLE_NAME,
                null,
                values);

        Log.d(TAG, Long.toString(newRowId));

        return true;
    }

    public boolean addEntry(Spinner carSpinner, Spinner name, EditText price, EditText gallons, EditText mileage, EditText date)
    {
        String carString = carSpinner.getSelectedItem().toString();
        String dateString = date.getText().toString();
        String nameString = name.getSelectedItem().toString();
        String priceString = price.getText().toString();
        String gallonsString = gallons.getText().toString();
        String mileageString = mileage.getText().toString();

        TextView errorText = (TextView) findViewById(R.id.text_fields_errors);
        errorText.setText("");

        boolean emptyField = false;


        if (carString == null || dateString.isEmpty())
        {
            emptyField = true;
            errorText.append(getString(R.string.text_car_error));
            errorText.append(getString(R.string.text_newline));
        }
        if (dateString.isEmpty())
        {
            emptyField = true;
            errorText.append(getString(R.string.text_date_error));
            errorText.append(getString(R.string.text_newline));
        }
        if (nameString.isEmpty())
        {
            emptyField = true;
            errorText.append(getString(R.string.text_name_error));
            errorText.append(getString(R.string.text_newline));
        }
        if (priceString.isEmpty())
        {
            emptyField = true;
            errorText.append(getString(R.string.text_price_error));
            errorText.append(getString(R.string.text_newline));
        }
        if (gallonsString.isEmpty())
        {
            emptyField = true;
            errorText.append(getString(R.string.text_gallons_error));
            errorText.append(getString(R.string.text_newline));
        }
        if (mileageString.isEmpty())
        {
            emptyField = true;
            errorText.append(getString(R.string.text_mileage_error));
            errorText.append(getString(R.string.text_newline));
        }

        if (emptyField)
        {
            errorText.setVisibility(View.VISIBLE);
            return false;
        }

        // Create new helper
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create insert entries
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.gasTable.COLUMN_NAME_CAR, carString);
        values.put(DatabaseContract.gasTable.COLUMN_NAME_DATE, dateString);
        values.put(DatabaseContract.gasTable.COLUMN_NAME_NAME, nameString);
        values.put(DatabaseContract.gasTable.COLUMN_NAME_PRICE, priceString);
        values.put(DatabaseContract.gasTable.COLUMN_NAME_GALLONS, gallonsString);
        values.put(DatabaseContract.gasTable.COLUMN_NAME_MILEAGE, mileageString);

        // Insert the new row, returning the primary key value of the new row

        long newRowId;
        newRowId = db.insert(
                    DatabaseContract.gasTable.TABLE_NAME,
                    null,
                    values);

        return true;
    }

    public void submitEntry(View view)
    {
        Spinner carSpinner = (Spinner) findViewById(R.id.spinner_car);
        EditText date = (EditText) findViewById(R.id.text_date_edit);
        EditText nameEditText = (EditText) findViewById(R.id.text_name_edit);
        Spinner nameSpinner = (Spinner) findViewById(R.id.spinner_name);
        EditText price = (EditText) findViewById(R.id.text_price_edit);
        EditText gallons = (EditText) findViewById(R.id.text_gallons_edit);
        EditText mileage = (EditText) findViewById(R.id.text_mileage_edit);


        // Check if table exists first, create if it doesn't
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (!dbHelper.doesTableExist(db, DatabaseContract.gasTable.TABLE_NAME))
        {
            db.execSQL(DatabaseContract.gasTable.SQL_CREATE_ENTRIES);
        }

        if (nameEditText.isShown())
        {
            if (addEntry(carSpinner, price, gallons, mileage, date, nameEditText))
            {
                displayToast();
                resetValues();
            }
        }
        else if (nameSpinner.isShown())
        {
            if (addEntry(carSpinner, nameSpinner, price, gallons, mileage, date))
            {
                displayToast();
                resetValues();
            }
        }
        else
        {
            Log.wtf(TAG, "Neither name EditText or Spinner is visible.\nNow just how in the heck did you manage to do that?");
        }
    }

    private void resetValues()
    {
        EditText date = (EditText) findViewById(R.id.text_date_edit);
        if (checkBoxState(R.id.text_date_edit_checkbox))
        {
            date.setText(getDate());
        }
        else
        {
            date.setText("");
        }
        EditText name = (EditText) findViewById(R.id.text_name_edit);
        name.setText("");
        EditText price = (EditText) findViewById(R.id.text_price_edit);
        price.setText("");
        EditText gallons = (EditText) findViewById(R.id.text_gallons_edit);
        gallons.setText("");
        EditText mileage = (EditText) findViewById(R.id.text_mileage_edit);
        mileage.setText("");
    }

    public boolean checkBoxState(int id)
    {
        return ((CheckBox) findViewById(id)).isChecked();
    }

    /*
        General CheckBox toggle by passing view
     */
    public void onCheckBoxToggle(View view)
    {
        CheckBox checkbox = (CheckBox) view;

        switch (checkbox.getId())
        {
            // Date toggle
            case R.id.text_date_edit_checkbox:
                EditText dateEditText = (EditText) findViewById(R.id.text_date_edit);
                if (checkbox.isChecked())
                {
                    dateEditText.setFocusableInTouchMode(false);
                    dateEditText.setFocusable(false);
                    dateEditText.setText(getDate());
                }
                else
                {
                    dateEditText.setFocusableInTouchMode(true);
                    dateEditText.setFocusable(true);
                    dateEditText.setText("");
                }
                break;

            // Gas station name toggle
            case R.id.text_name_edit_checkbox:
                Spinner nameSpinner = (Spinner) findViewById(R.id.spinner_name);
                EditText nameEditText = (EditText) findViewById(R.id.text_name_edit);
                if (checkbox.isChecked())
                {
                    nameEditText.setVisibility(View.VISIBLE);
                    nameSpinner.setVisibility(View.GONE);
                }
                else
                {
                    nameEditText.setVisibility(View.GONE);
                    nameSpinner.setVisibility(View.VISIBLE);
                }
        }

    }

    public void displayToast()
    {
        Toast toast = new Toast(getApplicationContext());
        toast.setView(getLayoutInflater().inflate(R.layout.toast, (ViewGroup) findViewById(R.id.toast_submit)));
        toast.makeText(this, getString(R.string.toast_submit_message), Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 200);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    /*
        Passed EditText and fills with current date
     */
    public String getDate()
    {
        SimpleDateFormat simpleDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        String date = simpleDate.format(Calendar.getInstance().getTime());
        return date;
    }
}