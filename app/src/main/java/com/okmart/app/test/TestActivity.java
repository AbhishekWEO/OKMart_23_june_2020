package com.okmart.app.test;

import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.okmart.app.R;

public class TestActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private RelativeLayout marker;
    int step = 1;
    int max = 180;
    int min = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        marker = findViewById(R.id.marker);

        seekBar=(SeekBar)findViewById(R.id.seekBar);

        seekBar.setMax( (max - min) / step );

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

                double value = min + (progress * step);

                Toast.makeText(getApplicationContext(),"seekbar progress: "+value, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(getApplicationContext(),"seekbar touch started!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(getApplicationContext(),"seekbar touch stopped!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
