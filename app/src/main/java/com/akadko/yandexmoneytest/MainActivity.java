package com.akadko.yandexmoneytest;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerViewFragment fragment = (RecyclerViewFragment)
                getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragment == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new RecyclerViewFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh) {
            RecyclerViewFragment fragment = ((RecyclerViewFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.container));
            if (fragment != null) {
                fragment.refresh();
            } else {
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.container, new RecyclerViewFragment())
                        .commit();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
