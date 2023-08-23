package com.android.pupildetection.main;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.pupildetection.R;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class QuestionsActivity extends AppCompatActivity {

    Integer size;
    RadioGroup rg;
    RadioGroup rg2;
    RadioGroup rg3;
    RadioGroup rg4;
    RadioGroup rg5;
    Button next;
    Button back;
    private Dialog dialog;
    private static AtomicLong idCounter;
    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        String user = getRandomString(5);
        next = findViewById(R.id.okbutton);
        back = findViewById(R.id.cancelbutton);
        idCounter = new AtomicLong();

        dialog = new Dialog(QuestionsActivity.this);
        dialog.setContentView(R.layout.filter_info);
        dialog.show();



        final Button accept = dialog.findViewById(R.id.button_ok);
        final Button cancel = dialog.findViewById(R.id.button_cancel);
        final TextView info = dialog.findViewById(R.id.info);
        info.setText( "Dear user " + "\"" + user + "\"" + " thank you for participating in this study.");
        accept.setOnClickListener(view -> dialog.dismiss());
        cancel.setOnClickListener(view -> {
            finish();
            System.exit(0);
        });

        LinearLayout linearLayout = findViewById(R.id.container1);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);

        TextView textView = new TextView(this);
        textView.setText("Sex:");
        textView.setGravity(Gravity.TOP);
        textView.setBackgroundColor(Color.WHITE);
        textView.setPadding(10,0,10,0);
        textView.setTextSize(15);
        linearLayout.addView(textView);

        rg = new RadioGroup(getApplicationContext());
        rg.setOrientation(RadioGroup.VERTICAL);
        RadioButton rb = new RadioButton(this);
        rb.setText("Male");
        rg.addView(rb);
        RadioButton rb1 = new RadioButton(this);
        rb1.setText("Female");
        rg.addView(rb1);
        rg.setGravity(Gravity.START);
        rg.setPadding(10,0,10,0);
        linearLayout.addView(rg);

        TextView textView2 = new TextView(this);
        textView2.setText("Age:");
        textView2.setGravity(Gravity.TOP);
        textView2.setBackgroundColor(Color.WHITE);
        textView2.setPadding(10,10,10,0);
        textView2.setTextSize(15);
        linearLayout.addView(textView2);

        rg2 = new RadioGroup(getApplicationContext());
        rg2.setOrientation(RadioGroup.VERTICAL);
        RadioButton rb2 = new RadioButton(this);
        rb2.setText("Under 18");
        rg2.addView(rb2);
        RadioButton rb3 = new RadioButton(this);
        rb3.setText("18-25");
        rg2.addView(rb3);
        RadioButton rb4 = new RadioButton(this);
        rb4.setText("25-35");
        rg2.addView(rb4);
        RadioButton rb5 = new RadioButton(this);
        rb5.setText("35-45");
        rg2.addView(rb5);
        RadioButton rb6 = new RadioButton(this);
        rb6.setText("Over 45");
        rg2.addView(rb6);
        rg2.setGravity(Gravity.START);
        rg2.setPadding(10,0,10,0);
        linearLayout.addView(rg2);

        TextView textView3 = new TextView(this);
        textView3.setText("Are you wearing glasses?");
        textView3.setGravity(Gravity.TOP);
        textView3.setBackgroundColor(Color.WHITE);
        textView3.setPadding(10,10,10,0);
        textView3.setTextSize(15);
        linearLayout.addView(textView3);

        rg3 = new RadioGroup(getApplicationContext());
        rg3.setOrientation(RadioGroup.VERTICAL);
        RadioButton rb7 = new RadioButton(this);
        rb7.setText("Yes");
        rg3.addView(rb7);
        RadioButton rb8 = new RadioButton(this);
        rb8.setText("No");
        rg3.addView(rb8);
        rg3.setGravity(Gravity.START);
        rg3.setPadding(10,0,10,0);
        linearLayout.addView(rg3);

        TextView textView4 = new TextView(this);
        textView4.setText("What device do you use more for applications such as Skype, Zoom, and...?");

        textView4.setGravity(Gravity.TOP);
        textView4.setBackgroundColor(Color.WHITE);
        textView4.setPadding(10,10,10,0);
        textView4.setTextSize(15);
        linearLayout.addView(textView4);

        rg5 = new RadioGroup(getApplicationContext());
        rg5.setOrientation(RadioGroup.VERTICAL);
        RadioButton rb9= new RadioButton(this);
        rb9.setText("Cell Phone");
        rg5.addView(rb9);
        RadioButton rb10 = new RadioButton(this);
        rb10.setText("Personal Computer");
        rg5.addView(rb10);
        RadioButton rb11 = new RadioButton(this);
        rb11.setText("Tablet");
        rg5.addView(rb11);
        rg5.setGravity(Gravity.START);
        rg5.setPadding(10,0,10,0);
        linearLayout.addView(rg5);

        TextView textView5 = new TextView(this);
        textView5.setText("How many days per week do you spend using video messaging/streaming apps on average?");
        textView5.setGravity(Gravity.TOP);
        textView5.setBackgroundColor(Color.WHITE);
        textView5.setPadding(10,10,10,0);

        textView5.setTextSize(15);
        linearLayout.addView(textView5);

        rg4 = new RadioGroup(getApplicationContext());
        rg4.setOrientation(RadioGroup.VERTICAL);
        RadioButton rb15 = new RadioButton(this);
        rb15.setText("Never Use");
        rg4.addView(rb15);
        RadioButton rb12 = new RadioButton(this);
        rb12.setText("One or Two Days");
        rg4.addView(rb12);
        RadioButton rb13 = new RadioButton(this);
        rb13.setText("Three-Five Days");
        rg4.addView(rb13);
        RadioButton rb14 = new RadioButton(this);
        rb14.setText("Almost Every Day");
        rg4.addView(rb14);
        rg4.setGravity(Gravity.START);
        rg4.setPadding(10,0,10,0);
        linearLayout.addView(rg4);

        next.setOnClickListener(view -> {
            Random rn = new Random();
            int answer = rn.nextInt(5) + 1;
            Intent intent = new Intent(QuestionsActivity.this, MainActivity2.class);
            intent.putExtra("num", answer);
            intent.putExtra("user", user);
            startActivity(intent);
        });
    }

    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }
}
