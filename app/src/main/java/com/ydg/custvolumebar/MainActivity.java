package com.ydg.custvolumebar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements OnProgressChangeListener{

    private static final String TAG = "MainActivity";
    CircleProgressBar circleBar = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        circleBar = (CircleProgressBar)findViewById(R.id.circle_bar);
        circleBar.setOnProgressChangeListener(this);

        //circleBar.setProgress(100,3000); //设置动画时间为3000ms
        Button buttonInc = (Button)findViewById(R.id.button_inc);
        Button buttonDec = (Button)findViewById(R.id.button_dec);

        buttonInc.setOnClickListener(onClickListener);
        buttonDec.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            switch(view.getId()){
                case R.id.button_inc:
                    if (circleBar != null){
                        int value =circleBar.getProgress();
                        value = (value >= 0 && value < 100)?(value + 1):100;
                        circleBar.setProgress(value);
                    }
                    break;
                case R.id.button_dec:
                    if (circleBar != null){
                        int value = circleBar.getProgress();
                        value = (value >0 && value <= 100)? (value -1):0;
                        circleBar.setProgress(value);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    public void onProgressChange(){
        Log.i(TAG, "zhougq, onProgressChange()");
    }
}
