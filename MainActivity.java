package com.example.mcalcpro;

import androidx.appcompat.app.AppCompatActivity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;
import ca.roumani.i2c.MPro;


public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, SensorEventListener {
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.tts = new TextToSpeech(this, this);
        SensorManager sm = (SensorManager)getSystemService((SENSOR_SERVICE));
        sm.registerListener(this,sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void onInit(int initStatus) {
        this.tts.setLanguage(Locale.US);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        double x = event.values[0];
        double y = event.values[1];
        double z = event.values[2];
        double a = Math.sqrt(x * x * y * y * z * z);
        if (a > 20) {
            ((EditText) findViewById(R.id.pBox)).setText("");
            ((EditText) findViewById(R.id.aBox)).setText("");
            ((EditText) findViewById(R.id.iBox)).setText("");
            ((TextView) findViewById(R.id.output)).setText("");
        }
    }

    public void buttonClicked(View v) {
        try {
            MPro mp = new MPro();
            mp.setPrinciple(((EditText) findViewById(R.id.pBox)).getText().toString());
            mp.setAmortization(((EditText) findViewById(R.id.aBox)).getText().toString());
            mp.setInterest(((EditText) findViewById(R.id.iBox)).getText().toString());

            String s = "Monthly Payments = $" + mp.computePayment("%,.2f");
            s += "\n\n";
            s += "By making this payments monthly for 20 years, the mortgage will be paid in full. " + "But if you terminate the mortgage on its nth anniversary, the balance still owing depends on n as shown below:";
            s += "\n\n";
            for (int i = 0; i <= 20; i++) {
                if (i < 6 || (i % 5 == 0)) {
                    s += "\n\n";
                    s += String.format("%d", i) + (mp.outstandingAfter(i, "%,16.0f"));
                    tts.speak(s, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
            ((TextView) findViewById(R.id.output)).setText(s);
        }
        catch(Exception e){
            Toast label = Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
            label.show();
        }
    }
}
