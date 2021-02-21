package com.tosi.wastehunters;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class FirstActivity extends AppCompatActivity {
    int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        getSupportActionBar().hide();

        final TextView textView = (TextView)findViewById(R.id.textView23);
        ImageView imageButton = (ImageView) findViewById(R.id.imageView19);
        final ImageView imageView = (ImageView)findViewById(R.id.imageView18);
        final ImageView imageView1 = (ImageView)findViewById(R.id.imageView20);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i++;
                if (i == 1){
                    textView.setText("Zaloguj svoj úlovok do aplikácie");
                    imageView.setImageResource(R.drawable.first_second);
                    imageView1.setImageResource(R.drawable.moretwo);
                }else if (i == 2){
                    textView.setText("Buď aj ty Wastehunter!");
                    imageView.setImageResource(R.drawable.first_third);
                    imageView1.setImageResource(R.drawable.morethree);
                }else if (i == 3){
                    SharedPreferences sp = getSharedPreferences("show_introduction", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("show_introduction_k", 1);
                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(), MainPanel.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }
}
