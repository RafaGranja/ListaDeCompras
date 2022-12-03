package com.example.projetobase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.google.firebase.auth.FirebaseAuth;

public class RecuperarSenha extends AppCompatActivity {

    private static final int SUCCESS =  1;
    private static final int WARNING =  0;
    private static final int ERROR =  -1;

    EditText email;
    AppCompatButton enviar;
    ProgressBar load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_senha);
        getSupportActionBar().hide();
        IniciarComponentes();

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                load.setVisibility(View.VISIBLE);

                if(email.getText().toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
                    Toast(ERROR,"Insira um e-mail válido");
                    load.setVisibility(View.INVISIBLE);
                }
                else{


                    FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast(SUCCESS,"Email enviado com sucesso!");

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        load.setVisibility(View.INVISIBLE);
                                        Intent intent = new Intent(RecuperarSenha.this,FormLogin.class);
                                        startActivity(intent);
                                    }
                                },100);
                            }
                            else{
                                try {
                                    load.setVisibility(View.INVISIBLE);
                                    throw task.getException();
                                }
                                catch (FirebaseNetworkException e){
                                    Toast(WARNING,"Sem conexão com a Internet");
                                }
                                catch (Exception e){
                                    Toast(ERROR,"Erro ao enviar email, cheque os dados");
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    public void IniciarComponentes(){

        email = (EditText)findViewById(R.id.emailrescue);
        enviar = (AppCompatButton)findViewById(R.id.button_rescue);
        load = (ProgressBar)findViewById(R.id.progress_bar_rescue);

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