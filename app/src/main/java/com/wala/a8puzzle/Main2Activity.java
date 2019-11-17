package com.wala.a8puzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Button Goal1 = findViewById(R.id.goal1);
        Button Goal2 =(Button) findViewById(R.id.goal2);
        Goal1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                intent.putExtra("Goalid", 1);
                startActivity(intent);
            }
        }
        );
        Goal2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (Main2Activity.this, MainActivity.class);
                intent.putExtra("Goal2Id", 2);
                startActivity(intent);
            }
        });
    }
}
