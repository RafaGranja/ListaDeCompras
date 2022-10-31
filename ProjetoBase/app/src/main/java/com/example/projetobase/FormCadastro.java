package com.example.projetobase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import java.io.*;
import okhttp3.*;

public class FormCadastro extends AppCompatActivity {

    private EditText pass1;
    private EditText pass2;
    private EditText login;
    private EditText email;
    private ProgressBar load;
    private AppCompatButton button_sign_up;

    String userID;

    private static final int SUCCESS =  1;
    private static final int WARNING =  0;
    private static final int ERROR =  -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro);
        getSupportActionBar().hide();
        IniciarComponentes();

        button_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadSignUp();
            }
        });

        pass1.addTextChangedListener(
            new TextWatcher(){
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            }
        );
        pass2.addTextChangedListener(
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

    public void LoadSignUp() {

        if(login.getText().length()==0){
            Toast(ERROR,"Insira um usuário válido");
        }
        else if(login.getText().length()>15){
            Toast(ERROR,"O usuário não deve conter mais de 15 caracteres");
        }
        else if(email.getText().toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
            Toast(ERROR,"Insira um e-mail válido");
        }
        else if(pass1.getText().toString().isEmpty()){
            Toast(ERROR,"Insira uma senha válida");
        }
        else if(pass2.getText().toString().isEmpty()){
            Toast(WARNING,"Confirme sua senha");

        }
        else if(pass1.getText().toString().equals(pass2.getText().toString())){
            load.setVisibility(View.VISIBLE);
            login.setFocusable(false);
            email.setFocusable(false);
            pass1.setFocusable(false);
            pass2.setFocusable(false);
            button_sign_up.setClickable(false);

            Toast(SUCCESS,"Registrando Usuário");

            CadastrarUsuario();
        }
        else{
            Toast(WARNING,"As senhas devem ser iguais");
        }
    }

    private void CadastrarUsuario(){

        String _email = email.getText().toString();
        String _senha = pass1.getText().toString();
        String key = "vg5VtisJk2rVVh8unO44KYOM0l7SQS35";
        String url = "https://api.apilayer.com/email_verification/check?apikey=" + key + "&email=" + _email + "/json/";
        String api;

        API autenticaEmail = new API();
        autenticaEmail.execute("viacep.com.br/ws/01001000/json/");
        api = autenticaEmail.result;

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(_email, _senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    CadastroCompleto();
                    SalvaDadosUsario();
                } else {

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        ErroCadastro(WARNING, "A senha deve conter no mínimo 6 caracteres");
                    } catch (FirebaseAuthUserCollisionException e) {
                        ErroCadastro(WARNING, "Esta conta já foi cadastrada anteriormente");
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        ErroCadastro(WARNING, "O e-mail digitado não válido");
                    } catch (FirebaseNetworkException e) {
                        ErroCadastro(WARNING, "Sem conexão com a Internet");
                    } catch (Exception e) {
                        ErroCadastro(ERROR, "Erro ao cadastrar usuário");
                    }

                }
            }
        });


    }

    private void EnviaEmail(){
        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(FormCadastro.this, FormLogin.class);
                            startActivity(intent);
                        }
                    }, 2000);
                }
                else{
                    Toast(ERROR,"Ocorreu um erro ao enviar email de autenticação");
                }

            }
        });
    }

    private class API extends AsyncTask<String, String, String>{

        String result;

        @Override
        protected String doInBackground(String... strings) {
            String ret = Conexao.getApi(strings[0]);
            return ret;
        }

        @Override
        protected void onPostExecute(String s) {
            result=s;
        }
    }

    private void SalvaDadosUsario(){
        String _user = login.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String,Object> users = new HashMap<>();
        users.put("login",_user);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference doc = db.collection("SOCIAL").document(userID);
        doc.set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("db","Sucesso ao salvar dados do usario "+userID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("db", "Erro ao salvar dados do usario " + userID +" "+ e.toString());
            }
        });

    }

    private void CadastroCompleto(){
        Toast(SUCCESS,"Usuário cadastrado com sucesso!");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                load.setVisibility(View.INVISIBLE);
                login.setFocusableInTouchMode(true);
                login.setText("");
                email.setFocusableInTouchMode(true);
                email.setText("");
                pass1.setFocusableInTouchMode(true);
                pass1.setText("");
                pass2.setFocusableInTouchMode(true);
                pass2.setText("");
                button_sign_up.setClickable(true);
                EnviaEmail();
                FirebaseAuth.getInstance().signOut();
            }
        },2000);
    }

    private void ErroCadastro(int Type,String Erro){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast(Type,Erro);
                login.setFocusableInTouchMode(true);
                email.setFocusableInTouchMode(true);
                pass1.setFocusableInTouchMode(true);
                pass2.setFocusableInTouchMode(true);
                button_sign_up.setClickable(true);
                load.setVisibility(View.INVISIBLE);
            }
        },1000);
    }

    private void IniciarComponentes(){

        pass1 = (EditText)findViewById((R.id.passSignup));
        pass2 = (EditText)findViewById((R.id.passSignupConfirm));
        load= (ProgressBar)findViewById(R.id.progress_bar_signup);
        login = (EditText)findViewById((R.id.nameSignup));
        email =  (EditText)findViewById((R.id.emailSignup));
        button_sign_up = findViewById(R.id.button_sing_up);
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