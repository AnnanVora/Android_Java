package annan.example.tasktimer;

import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    public static int finalProgress;
    public static int finalDay;

    RadioGroup days = findViewById(R.id.days);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SeekBar slider = findViewById(R.id.slider);
        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                finalProgress = seekBar.getProgress();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finalDay = days.getCheckedRadioButtonId();
    }
}
