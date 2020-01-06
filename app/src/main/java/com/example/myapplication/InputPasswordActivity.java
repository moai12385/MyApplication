package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class InputPasswordActivity extends AppCompatActivity {

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_password);
    }

    public void onClick0(View v) {
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
    }

    public void onClick1(View v) {
        Intent intent = getIntent();
        int judge = intent.getIntExtra("source",0);

        if (judge == 0){
            intent = new Intent(this, InputMailActivity.class);
            startActivity(intent);
        }
        if (judge == 1){
            intent = new Intent(this, InputPasswordActivity.class);
            int source = 2;
            intent.putExtra("source", source);
            startActivity(intent);
        }
        if (judge == 2){
            intent = new Intent(this, AccountActivity.class);
            startActivity(intent);
        }
    }
}
