package com.example.projetobase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;


public class FormLogin extends AppCompatActivity {

    private TextView text_signup;
    private TextView text_passwordchange;
    private EditText login;
    private EditText password;
    private ProgressBar load;
    private AppCompatButton button_login;

    private static final int SUCCESS =  1;
    private static final int WARNING =  0;
    private static final int ERROR =  -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_login);
        getSupportActionBar().hide();
        IniciarComponentes();

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadLogin();
            }
        });

        text_signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FormLogin.this,FormCadastro.class);
                startActivity(intent);
            }
        });

        password.addTextChangedListener(
                new TextWatcher(){
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        load.setVisibility(View.INVISIBLE);

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
    public void LoadLogin() {

        if(login.getText().length()==0){
            Toast(ERROR,"Insira um Login Válido");
        }
        else if(password.getText().length()==0){
            Toast(ERROR,"Insira uma senha válida");
            load.setVisibility(View.INVISIBLE);
        }
        else{
            Toast(SUCCESS,"Buscando informações");
            login.setFocusable(false);
            password.setFocusable(false);
            button_login.setClickable(false);
            text_signup.setClickable(false);
            text_passwordchange.setClickable(false);
            load.setVisibility(View.VISIBLE);

            AutenticarUsuario();
        }
    }

    private void AutenticarUsuario(){

        String _login = login.getText().toString();
        String _pass = password.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(_login,_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    LogaUsuario();
                }
                else{
                    try {
                        throw task.getException();
                    }
                    catch (Exception e){
                        ErroLogin(ERROR,"Erro ao autenticar usuário, cheque os dados");
                    }
                }
            }
        });

    }

    private void TelaPrincipal(){
        Intent intent = new Intent(FormLogin.this,TelaPrincipal.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();

        if(current_user!=null){
            TelaPrincipal();
        }
    }

    private void LogaUsuario(){
        Toast(SUCCESS,"Login Efetuado");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                login.setFocusableInTouchMode(true);
                login.setText("");
                password.setFocusableInTouchMode(true);
                password.setText("");
                button_login.setClickable(true);
                text_signup.setClickable(true);
                text_passwordchange.setClickable(true);
                load.setVisibility(View.INVISIBLE);
                TelaPrincipal();
            }
        },2000);
    }

    private void ErroLogin(int Type, String Erro){

        Toast(Type,Erro);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                login.setFocusableInTouchMode(true);
                password.setFocusableInTouchMode(true);
                button_login.setClickable(true);
                text_signup.setClickable(true);
                text_passwordchange.setClickable(true);
                load.setVisibility(View.INVISIBLE);
            }
        },1000);

    }

    private void IniciarComponentes(){

        text_signup = (TextView)findViewById((R.id.text_signup));
        login = (EditText)findViewById((R.id.edit_text_login));
        password = (EditText)findViewById((R.id.edit_text_password));
        load= (ProgressBar)findViewById(R.id.progress_bar);
        button_login = findViewById(R.id.button_login);
        text_passwordchange = findViewById(R.id.text_passwordchange);


    }

    private void Toast(int type, String message){

        View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_toast,null);
        TextView txt = v.findViewById(R.id.text_message);
        txt.setText(message);
        ImageView ic = v.findViewById(R.id.image_message);
        v.setElevation(200);

        switch (type){
            case ERROR:
                v.setBackground(ContextCompat.getDrawable(this,R.drawable.error_toast));
                ic.setImageResource(R.drawable.error_toast_ic);
                break;

            case WARNING:
                v.setBackground(ContextCompat.getDrawable(this,R.drawable.warning_toast));
                ic.setImageResource(R.drawable.warning_toast_ic);
                break;

            case SUCCESS:
                v.setBackground(ContextCompat.getDrawable(this,R.drawable.sucess_toast));
                ic.setImageResource(R.drawable.sucess_toast_ic);
                break;

            default:
                break;
        }
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(v);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();

    }

}