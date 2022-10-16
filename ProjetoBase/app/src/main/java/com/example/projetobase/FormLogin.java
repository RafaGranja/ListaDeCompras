package com.example.projetobase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

public class FormLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_login);
        getSupportActionBar().hide();

    }
    protected void LoadLogin(){

        ProgressBar load= (ProgressBar)findViewById(R.id.progress_bar);
        load.setVisibility(View.VISIBLE);

    }
}