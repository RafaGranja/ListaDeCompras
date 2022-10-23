package com.example.projetobase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

public class FormCadastro extends AppCompatActivity {

    private EditText senha1;
    private EditText senha2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro);
        getSupportActionBar().hide();
        IniciarComponentes();

        senha1.addTextChangedListener(
            new TextWatcher(){
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    senha1.setTextColor(getResources().getColor(R.color.black));
                    senha2.setTextColor(getResources().getColor(R.color.black));
                    senha1.setHintTextColor(getResources().getColor(R.color.black));
                    senha2.setHintTextColor(getResources().getColor(R.color.black));
                }
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            }
        );
        senha2.addTextChangedListener(
                new TextWatcher(){
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        senha1.setTextColor(getResources().getColor(R.color.black));
                        senha2.setTextColor(getResources().getColor(R.color.black));
                        senha1.setHintTextColor(getResources().getColor(R.color.black));
                        senha2.setHintTextColor(getResources().getColor(R.color.black));
                    }

                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                }
        );
    }

    /** Called when the user touches the button */
    public void LoadSignUp(View view) {

        ProgressBar load= (ProgressBar)findViewById(R.id.progress_bar_signup);
        EditText login = (EditText)findViewById((R.id.nameSignup));
        EditText password1 = (EditText)findViewById((R.id.passSignup));
        EditText password2 = (EditText)findViewById((R.id.passSignupConfirm));
        EditText email =  (EditText)findViewById((R.id.emailSignup));

        if(login.getText().length()==0){
            login.setHintTextColor(getResources().getColor(R.color.red));
        }
        else{
            login.setHintTextColor(getResources().getColor(R.color.black));
        }

        if(email.getText().length()==0){
            email.setHintTextColor(getResources().getColor(R.color.red));
        }
        else{
            email.setHintTextColor(getResources().getColor(R.color.black));
        }

        if(password1.getText().length()==0){
            password1.setHintTextColor(getResources().getColor(R.color.red));
        }
        else if(password2.getText().length()==0){
            password2.setHintTextColor(getResources().getColor(R.color.red));
            password1.setHintTextColor(getResources().getColor(R.color.black));
        }
        else if(password1.getText().toString().equals(password2.getText().toString())){
            password1.setTextColor(getResources().getColor(R.color.black));
            password2.setTextColor(getResources().getColor(R.color.black));
            password2.setHintTextColor(getResources().getColor(R.color.black));
            password1.setHintTextColor(getResources().getColor(R.color.black));
            load.setVisibility(View.VISIBLE);
        }
        else{
            password1.setTextColor(getResources().getColor(R.color.red));
            password2.setTextColor(getResources().getColor(R.color.red));
            password1.setHintTextColor(getResources().getColor(R.color.red));
            password2.setHintTextColor(getResources().getColor(R.color.red));
        }
    }

    public void SetBlackText(EditText text){
        text.setTextColor(getResources().getColor(R.color.black));
    }

    private void IniciarComponentes(){
        senha1 = (EditText)findViewById(R.id.passSignup);
        senha2 = (EditText)findViewById(R.id.passSignupConfirm);
    }


}