package com.example.projetobase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FormLogin extends AppCompatActivity {

    private TextView text_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_login);
        getSupportActionBar().hide();
        IniciarComponentes();

        text_signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FormLogin.this,FormCadastro.class);
                startActivity(intent);
            }
        });


    }
    /** Called when the user touches the button */
    public void LoadLogin(View view) {
        ProgressBar load= (ProgressBar)findViewById(R.id.progress_bar);

        EditText login = (EditText)findViewById((R.id.edit_text_login));
        EditText password = (EditText)findViewById((R.id.edit_text_password));

        if(login.getText().length()==0){
            login.setHintTextColor(getResources().getColor(R.color.red));
        }
        else{
            login.setHintTextColor(getResources().getColor(R.color.black));
        }
        if(password.getText().length()==0){
            password.setHintTextColor(getResources().getColor(R.color.red));
        }
        else{
            password.setHintTextColor(getResources().getColor(R.color.black));
            load.setVisibility(View.VISIBLE);
        }
    }

    private void IniciarComponentes(){

        text_signup = findViewById((R.id.text_signup));

    }
}