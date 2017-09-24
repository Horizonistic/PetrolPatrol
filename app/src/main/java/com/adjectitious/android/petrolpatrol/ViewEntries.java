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
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;

import com.adjectitious.android.petrolpatrol.sql.*;

import java.text.DecimalFormat;

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
        this.viewAll();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        this.dbHelper.close();
        this.db.close();
        // todo: figure out for to check if null
        if (this.cursor != null)
        {
            this.cursor.close();
        }
    }

    public void viewAll()
    {
        this.dbHelper = new DatabaseHelper(getApplicationContext());
        this.db = this.dbHelper.getWritableDatabase();

        // Get the name and ID of the currently active car
        if (dbHelper.doesTableExist(db, DatabaseContract.gasTable.TABLE_NAME))
        {
            this.cursor = this.db.query(
                    DatabaseContract.carTable.TABLE_NAME,               // The table to query
                    null,                                              // The columns to return
                    DatabaseContract.carTable.COLUMN_ACTIVE + "=?",                                               // The columns for the WHERE clause
                    new String[] {"1"},                                               // The values for the WHERE clause
                    null,                                               // don't group the rows
                    null,                                               // don't filter by row groups
                    null                                                // The sort order
            );
        }
        else
        {
            Log.e(TAG, "Empty car table");
            return;
        }

        int activeCarId = 0;
        TextView activeCarText = new TextView(this.context);
        if (this.cursor.moveToFirst() && !this.cursor.isAfterLast())
        {
            activeCarText.setText(this.cursor.getString(this.cursor.getColumnIndex(DatabaseContract.carTable.COLUMN_NAME_NAME)));
            activeCarId = this.cursor.getInt(this.cursor.getColumnIndex(DatabaseContract.carTable._ID));
        }
        else
        {
            activeCarText.setText("Oops");
        }

        // Loop through all entries for the currently selected car
        if (dbHelper.doesTableExist(db, DatabaseContract.gasTable.TABLE_NAME))
        {
            if (activeCarId != 0)
            {
                this.cursor = this.db.query(
                        DatabaseContract.gasTable.TABLE_NAME,               // The table to query
                        null,                                               // The columns to return
                        DatabaseContract.gasTable.COLUMN_NAME_CAR + "=?",                                               // The columns for the WHERE clause
                        new String[] {String.valueOf(activeCarId)},                                               // The values for the WHERE clause
                        null,                                               // don't group the rows
                        null,                                               // don't filter by row groups
                        null                                                // The sort order
                );
            }
            else
            {
                Log.e(TAG, "No active car selected");
                return;
            }
        }
        else
        {
            Log.e(TAG, "Empty fillup table");
            return;
        }

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

        LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        activeCarText.setLayoutParams(layoutParams);
        activeCarText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        activeCarText.setTextSize(context.getResources().getDimension(R.dimen.header_font_size));
        activeCarText.setTypeface(null, Typeface.BOLD);
        activeCarText.setTextColor(colorBlack);
        activeCarText.setVisibility(View.VISIBLE);
        list.addView(activeCarText);
        if (this.cursor.moveToFirst())
        {
            while (!this.cursor.isAfterLast())
            {
                final LinearLayout layout = new LinearLayout(this.context);

                layout.setId(R.id.list_subitem);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setLayoutParams(layoutParams);
                layout.setPadding(0, 50, 0, 0);

                final GestureDetector gestureDetector = new GestureDetector(
                        this, new DeleteGestureDetector(this.cursor.getInt(this.cursor.getColumnIndex(DatabaseContract.gasTable._ID)))
                );

                View.OnTouchListener touchListener = new View.OnTouchListener()
                {
                    @Override
                    public boolean onTouch(View v, MotionEvent event)
                    {
                        if (event.getAction() == MotionEvent.ACTION_DOWN)
                        {
                            layout.setBackgroundColor(colorGrayHighlight);
                            return gestureDetector.onTouchEvent(event);
                        }
                        else if (event.getAction() == MotionEvent.ACTION_UP)
                        {
                            layout.setBackgroundColor(colorPrimaryBackground);
                            return gestureDetector.onTouchEvent(event);
                        }
                        else if (event.getAction() == MotionEvent.ACTION_CANCEL)
                        {
                            layout.setBackgroundColor(colorPrimaryBackground);
                            return gestureDetector.onTouchEvent(event);
                        }
                        return gestureDetector.onTouchEvent(event);
                    }
                };
                layout.setOnTouchListener(touchListener);

                list.addView(layout);

                // DATE
                TextView date = new TextView(this.context);
                date.setLayoutParams(layoutParams);
                date.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                date.setTextSize(context.getResources().getDimension(R.dimen.header_font_size));
                date.setTypeface(null, Typeface.BOLD);
                date.setTextColor(colorBlack);
                date.setText(this.cursor.getString(this.cursor.getColumnIndex(DatabaseContract.gasTable.COLUMN_NAME_DATE)));
                date.setVisibility(View.VISIBLE);
                layout.addView(date);

                // LOCATION
                TextView location = new TextView(this.context);
                location.setLayoutParams(layoutParams);
                location.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                location.setTextSize(context.getResources().getDimension(R.dimen.subitems_font_size));
                location.setTextColor(colorBlack);
                location.setText(this.cursor.getString(this.cursor.getColumnIndex(DatabaseContract.gasTable.COLUMN_NAME_NAME)));
                layout.addView(location);

                // PRICE
                TextView price = new TextView(this.context);
                price.setLayoutParams(layoutParams);
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
                gallons.setLayoutParams(layoutParams);
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
                mileage.setLayoutParams(layoutParams);
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
    }

    protected class DeleteGestureDetector implements GestureDetector.OnGestureListener
    {
        public int position;

        public DeleteGestureDetector(int position)
        {
            this.position = position;
        }

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d(TAG,"onDown: ");
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e)
        {
            Log.d(TAG, "onSingleTapUp: ");
            return true;
        }

        // todo: add swipe to delete functionality, changes onLongPress to edit
        @Override
        public void onLongPress(MotionEvent e) {
            Log.i(TAG, "onLongPress: ");
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
                            Log.i(ViewEntries.TAG, "Deleted entry: " + String.valueOf(this.position));
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
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            Log.i(TAG, "onScroll: ");
            return true;
        }

        // todo: add swipe to delete functionality, changes onLongPress to edit
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY)
        {
            Log.d(TAG, "onFling: ");
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e)
        {
            Log.d(TAG, "onShowPress: ");
        }
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