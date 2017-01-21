package app.project.voicecalc;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener {

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    private FloatingActionButton fab = null;
    private TextView textView1 = null;
    private TextView textView2 = null;
    private TextToSpeech tts = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() != 0) {
            fab.setOnClickListener(this);
        } else {
            fab.setEnabled(false);
            Snackbar.make(fab, "Speech recognition not found...!", Snackbar.LENGTH_INDEFINITE).setAction("Close", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            }).show();
        }

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                1);
        tts = new TextToSpeech(this, this);
        tts.setLanguage(Locale.US);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fab:
                startVoiceRecognitionActivity();
                Snackbar.make(v, "Listening...", Snackbar.LENGTH_LONG).setAction("", null).show();
                break;
            default:
                break;
        }
    }

    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech Recognition");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {

            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches.size() > 0) {
                doProcessing(matches.get(0).trim());
            }

            Toast.makeText(this, "" + matches.get(0).toString(), Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void doProcessing(String str) {

        Toast.makeText(this, "" + str.trim(), Toast.LENGTH_SHORT).show();
        textView2.setError("");
        textView2.setError(null);
        textView1.setText("" + str.trim());
        try {
            String split[] = str.split(" ");
            double a = Double.parseDouble(split[0].trim());
            double b = Double.parseDouble(split[2].trim());
            double c = 0;
            String op = split[1].trim();

            if (op.contains("+")) {
                c = a + b;
                op = "+";
            } else if (op.contains("add")) {
                c = a + b;
                op = "+";
            } else if (op.contains("plus")) {
                c = a + b;
                op = "+";
            } else if (op.contains("minus")) {
                c = a - b;
                op = "-";
            } else if (op.contains("-")) {
                c = a - b;
                op = "-";
            } else if (op.contains("subtracts")) {
                c = a - b;
                op = "-";
            } else if (op.contains("multiply")) {
                c = a * b;
                op = "x";
            } else if (op.contains("into")) {
                c = a * b;
                op = "x";
            } else if (op.contains("divide")) {
                c = a / b;
                op = "/";
            } else if (op.contains("of")) {
                c = (a * 100.0) / b;
                op = "%";
            }

            String show = a + " " + op + " " + b;
            textView1.setText("" + show);

            String speak = "";
            int temp = (int) c;
            String other = op.contains("%") == true ? " %" : "";
            if (c - temp != 0) {
                textView2.setText("" + c + " " + other);
                str = "" + c;
            } else {
                textView2.setText("" + temp + "" + other);
                speak = "" + temp + "";
            }
            if (op.contains("%")) {
                speak = speak + " percent";
            }

            tts.speak(speak.trim(), TextToSpeech.QUEUE_ADD, null);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            textView2.setError("Error");
            textView2.setError("Exception");
        }

    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                fab.setEnabled(true);
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }
}
