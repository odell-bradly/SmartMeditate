package com.bradlyodell.ucsb.ece150.smartmeditate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FinalActivity extends Activity {
    private final String TAG = "FinalActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "On Create Final Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        Intent intent = getIntent();
        double avg_hr = intent.getDoubleExtra("AVG_HR", -1);
        String duration = intent.getStringExtra("DURATION");

        if (avg_hr == -1)
            finish();

        TextView AHR = (TextView) findViewById(R.id.AHR);
        TextView DUR = (TextView) findViewById(R.id.DUR);

        AHR.setText(AHR.getText().toString() + (int)avg_hr);
        DUR.setText(DUR.getText().toString() + duration);

        Button backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Button  Clicked");
                finish();
            }
        });
    }
}
