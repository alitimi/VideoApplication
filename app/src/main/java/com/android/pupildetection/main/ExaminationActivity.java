package com.android.pupildetection.main;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.android.pupildetection.R;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class ExaminationActivity extends AppCompatActivity {

    RadioGroup rg;
    RadioGroup rg2;
    RadioGroup rg3;
    Gson gson;
    int number;
    InputStream inputStream;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examination);

        Bundle b = getIntent().getExtras();
        number = b.getInt("num");

        LinearLayout linearLayout = findViewById(R.id.container1);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);

        gson = new Gson();

        if (number == 1) {
            inputStream = getResources().openRawResource(R.raw.q_video1);
        } else if (number == 2){
            inputStream = getResources().openRawResource(R.raw.q_video1);
        } else if (number == 3){
            inputStream = getResources().openRawResource(R.raw.q_video3);
        } else if (number == 4){
            inputStream = getResources().openRawResource(R.raw.q_video4);
        } else if (number == 5){
            inputStream = getResources().openRawResource(R.raw.q_video5);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

//        try (reader) {

            // Convert JSON File to Java Object
            Questions questions = gson.fromJson(reader, Questions.class);
            ArrayList qs = questions.questions;
            for (int i = 0; i < qs.size(); i++) {
                Question q = (Question) qs.get(i);
                TextView textView = new TextView(this);
                textView.setText(q.label);
                textView.setGravity(Gravity.TOP);
                textView.setBackgroundColor(Color.WHITE);
                textView.setPadding(10,0,10,0);
                textView.setTextSize(20);
                linearLayout.addView(textView);
                ArrayList choices = q.choices;
                for (int j = 0; j < choices.size(); j++) {
                    rg = new RadioGroup(getApplicationContext());
                    rg.setOrientation(RadioGroup.VERTICAL);
                    RadioButton rb = new RadioButton(this);
                    Choice ch = (Choice) choices.get(j);
                    rb.setText(ch.label);
                    rg.addView(rb);
                    rg.setGravity(Gravity.END);
                    rg.setPadding(10,0,10,0);
                    linearLayout.addView(rg);
                }
            }

            System.out.println(questions);
    }

    public class Questions {

        public ArrayList<Question> questions;

    }

    public class Question {
        public String label;
        public ArrayList<Choice> choices;
    }

    public class Choice {
        public String label;
    }

}
