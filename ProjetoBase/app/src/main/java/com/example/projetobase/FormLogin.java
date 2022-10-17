package com.example.projetobase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

public class FormLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_login);
        getSupportActionBar().hide();

    }
    /** Called when the user touches the button */
    public void LoadLogin(View view) {
        ProgressBar load= (ProgressBar)findViewById(R.id.progress_bar);
        load.setVisibility(View.VISIBLE);

        EditText login = (EditText)findViewById((R.id.edit_text_login));
        EditText password = (EditText)findViewById((R.id.edit_text_password));

        if(login.getText().length()==0){
            login.setHintTextColor(getResources().getColor(R.color.red));
        }
        else if(password.getText().length()==0){
            login.setHintTextColor(getResources().getColor(R.color.black));
            password.setHintTextColor(getResources().getColor(R.color.red));
        }
        else{
            password.setHintTextColor(getResources().getColor(R.color.black));
            load.setVisibility(View.INVISIBLE);
        }
    }
}