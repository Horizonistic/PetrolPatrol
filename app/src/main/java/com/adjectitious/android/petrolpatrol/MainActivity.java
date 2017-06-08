package com.adjectitious.android.petrolpatrol;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;

// TODO: Graphs and other statistics
// TODO: Multiple cars
// TODO: Help menu (single or per-view?)
// TODO: Nicer looking buttons (bigger, evenly split across view
public class MainActivity extends AppCompatActivity
{
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
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