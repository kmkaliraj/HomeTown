package com.example.kalirajkalimuthu.hometown;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;



public class PickLocation extends AppCompatActivity  {

    private DisplayMapFragment fragment;
    private Button mSetButton;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_location);
        setTitle("Pick Location");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fragments = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragments.beginTransaction();
        fragment = new DisplayMapFragment();
        fragmentTransaction.add(R.id.map_fragment_holder_1, fragment);
        fragmentTransaction.commit();
        fragment.setPickerMode(true);

        mSetButton = (Button) findViewById(R.id.map_ok);

        mSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                done();
            }
        });


    }

    public void done() {
        Intent goHome = getIntent();
        Double latitude = fragment.getLatitude();
        Double longitude = fragment.getLongitude();
        int result_code = Activity.RESULT_CANCELED;
        if (latitude != null && longitude != null) {
            result_code = Activity.RESULT_OK;
            goHome.putExtra("latitude", latitude.toString());
            goHome.putExtra("longitude", longitude.toString());
        }

        setResult(result_code, goHome);
        finish();
    }
}


