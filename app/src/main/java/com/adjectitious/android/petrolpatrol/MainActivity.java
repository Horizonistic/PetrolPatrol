package com.adjectitious.android.petrolpatrol;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.adjectitious.android.petrolpatrol.sql.DatabaseContract;
import com.adjectitious.android.petrolpatrol.sql.DatabaseHelper;

// TODO: Graphs and other statistics
// TODO: Multiple cars
// TODO: Help menu (single or per-view?)
// TODO: Nicer looking buttons (bigger, evenly split across view
public class MainActivity extends AppCompatActivity
{
    private Toolbar toolbar;
    protected DatabaseHelper dbHelper;
    protected SQLiteDatabase db;
    protected Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        this.dbHelper = new DatabaseHelper(getApplicationContext());
        this.db = this.dbHelper.getWritableDatabase();

        if (dbHelper.isTableEmpty(db, DatabaseContract.carTable.TABLE_NAME))
        {
            findViewById(R.id.option_add_entry).setVisibility(View.GONE);
            findViewById(R.id.option_view_entries).setVisibility(View.GONE);
        }
        else if (dbHelper.isTableEmpty(db, DatabaseContract.gasTable.TABLE_NAME))
        {
            findViewById(R.id.option_view_entries).setVisibility(View.GONE);
            findViewById(R.id.option_add_entry).setVisibility(View.VISIBLE);
        }
        else
        {
            findViewById(R.id.option_add_entry).setVisibility(View.VISIBLE);
            findViewById(R.id.option_view_entries).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        this.dbHelper = new DatabaseHelper(getApplicationContext());
        this.db = this.dbHelper.getWritableDatabase();
        if (dbHelper.isTableEmpty(db, DatabaseContract.carTable.TABLE_NAME))
        {
            findViewById(R.id.option_add_entry).setVisibility(View.GONE);
            findViewById(R.id.option_view_entries).setVisibility(View.GONE);
        }
        else if (dbHelper.isTableEmpty(db, DatabaseContract.gasTable.TABLE_NAME))
        {
            findViewById(R.id.option_view_entries).setVisibility(View.GONE);
            findViewById(R.id.option_add_entry).setVisibility(View.VISIBLE);
        }
        else
        {
            findViewById(R.id.option_add_entry).setVisibility(View.VISIBLE);
            findViewById(R.id.option_view_entries).setVisibility(View.VISIBLE);
        }
    }

    public void addEntry(View view)
    {
        Intent intent = new Intent(this, AddEntry.class);
        startActivity(intent);
    }

    public void viewEntries(View view)
    {
        Intent intent = new Intent(this, ViewEntries.class);
        startActivity(intent);
    }

    public void switchCars(View view)
    {
        Intent intent = new Intent(this, SwitchCars.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.drawer_button)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}