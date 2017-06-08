package com.adjectitious.android.petrolpatrol;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;

import com.adjectitious.android.petrolpatrol.sql.*;

import java.text.DecimalFormat;

// TODO: Add option to add entry (or am using tab system?)
// TODO: Implement pagination to prevent too long a list
// TODO: Implement sorting view calender
// TODO: Implement sorting via different stats
// TODO: Change display to table (might be too narrow, at least make it look nice)
public class ViewEntries extends AppCompatActivity
{
    private static final String TAG = "ViewEntries";
    private Context context;

    protected static final int SUBITEM_ID = 1;

    protected DatabaseHelper dbHelper;
    protected SQLiteDatabase db;
    protected Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entries);
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mainToolbar.setTitle(getString(R.string.view_entries_title));
        setSupportActionBar(mainToolbar);
        this.context = getApplicationContext();
        viewAll();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        this.dbHelper.close();
        this.db.close();
        this.cursor.close();
    }

    public void viewAll()
    {
        this.dbHelper = new DatabaseHelper(getApplicationContext());
        this.db = this.dbHelper.getWritableDatabase();
        this.cursor = this.db.query(
                DatabaseContract.gasTable.TABLE_NAME,               // The table to query
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
        if (Build.VERSION.SDK_INT >= 23)
        {
            colorBlack = this.context.getColor(R.color.black);
            colorGrayHighlight = this.context.getColor(R.color.grayHighlight);
            colorPrimaryBackground = this.context.getColor(R.color.primaryBackground);
        }
        else
        {
            colorBlack = getResources().getColor(R.color.black);
            colorGrayHighlight = getResources().getColor(R.color.grayHighlight);
            colorPrimaryBackground = getResources().getColor(R.color.primaryBackground);
        }
        LinearLayout list = (LinearLayout) findViewById(R.id.list);
        list.removeAllViews();
        if (this.cursor.moveToFirst())
        {
            while (!this.cursor.isAfterLast())
            {
                final LinearLayout layout = new LinearLayout(this.context);
                layout.setId(R.id.list_subitem);
                layout.setOrientation(LinearLayout.VERTICAL);
                LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                layout.setLayoutParams(params);
                layout.setPadding(0, 50, 0, 0);
                layout.setLongClickable(true);
                layout.setOnLongClickListener(new DeleteOnLongClickListener(this.cursor.getInt(this.cursor.getColumnIndex(DatabaseContract.gasTable._ID))));
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

                list.addView(layout);

                // DATE
                TextView date = new TextView(this.context);
                date.setLayoutParams(params);
                date.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                date.setTextSize(context.getResources().getDimension(R.dimen.header_font_size));
                date.setTypeface(null, Typeface.BOLD);
                date.setTextColor(colorBlack);
                date.setText(this.cursor.getString(this.cursor.getColumnIndex(DatabaseContract.gasTable.COLUMN_NAME_DATE)));
                date.setVisibility(View.VISIBLE);
                layout.addView(date);

                // LOCATION
                TextView location = new TextView(this.context);
                location.setLayoutParams(params);
                location.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                location.setTextSize(context.getResources().getDimension(R.dimen.subitems_font_size));
                location.setTextColor(colorBlack);
                location.setText(this.cursor.getString(this.cursor.getColumnIndex(DatabaseContract.gasTable.COLUMN_NAME_NAME)));
                layout.addView(location);

                // PRICE
                TextView price = new TextView(this.context);
                price.setLayoutParams(params);
                price.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                price.setTextSize(context.getResources().getDimension(R.dimen.subitems_font_size));
                price.setTextColor(colorBlack);
                price.setText(
                        new DecimalFormat("#,###.##").format(
                            this.cursor.getDouble(
                                    this.cursor.getColumnIndex(DatabaseContract.gasTable.COLUMN_NAME_PRICE))));
                layout.addView(price);

                // GALLONS
                TextView gallons = new TextView(this.context);
                gallons.setLayoutParams(params);
                gallons.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                gallons.setTextSize(context.getResources().getDimension(R.dimen.subitems_font_size));
                gallons.setTextColor(colorBlack);
                gallons.setText(
                        new DecimalFormat("#,###.###").format(
                            this.cursor.getDouble(
                                this.cursor.getColumnIndex(DatabaseContract.gasTable.COLUMN_NAME_GALLONS))));
                layout.addView(gallons);


                // MILEAGE
                TextView mileage = new TextView(this.context);
                mileage.setLayoutParams(params);
                mileage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                mileage.setTextSize(context.getResources().getDimension(R.dimen.subitems_font_size));
                mileage.setTextColor(colorBlack);
                mileage.setText(
                        new DecimalFormat("#,###").format(
                            this.cursor.getDouble(
                                this.cursor.getColumnIndex(DatabaseContract.gasTable.COLUMN_NAME_MILEAGE))));
                layout.addView(mileage);

                this.cursor.moveToNext();
            }
        }

        return;
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
            Log.wtf(ViewEntries.TAG, "Uhm, yeah");
            // TODO: Add an edit option
            AlertDialog dialog = new AlertDialog.Builder(ViewEntries.this).create();
            dialog.setTitle(R.string.delete_entry_title);
            dialog.setMessage(getString(R.string.delete_entry_prompt));
            dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.confirm),
                    new DialogTest(this.position)
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            String where = DatabaseContract.gasTable._ID + "= ?";
                            String[] values = new String[]{String.valueOf(this.position)};
                            db.delete(DatabaseContract.gasTable.TABLE_NAME, where, values);
                            viewAll();
                            Log.wtf(ViewEntries.TAG, String.valueOf(this.position));
                        }
                    });

            dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel),
                    new DialogTest(this.position)
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
}