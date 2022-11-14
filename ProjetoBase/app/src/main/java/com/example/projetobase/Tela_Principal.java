package com.example.projetobase;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projetobase.databinding.ActivityTelaPrincipalBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Text;

public class Tela_Principal extends AppCompatActivity {

    private static final int SUCCESS =  1;
    private static final int WARNING =  0;
    private static final int ERROR =  -1;

    private TextView account_name;
    private TextView account_email;
    //private int logout;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userID;

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityTelaPrincipalBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTelaPrincipalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarTelaPrincipal.toolbar);

        binding.appBarTelaPrincipal.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_new_list_fragment, R.id.nav_archive_list_fragment,
                R.id.nav_analytics_fragment, R.id.nav_account_fragment)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_tela_principal);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        CarregaComponenetes();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            //Back button
            case R.id.logout:
                //If this activity started from other activity
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Tela_Principal.this, FormLogin.class);
                startActivity(intent);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void buscaInformacoes(){

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference doc = db.collection("SOCIAL").document(userID);


        doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {

            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if(value != null){

                    account_name.setText(value.get("login").toString());
                    if(value.get("email")==null || value.get("email")==""){
                        account_email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    }
                    else {
                        account_email.setText(value.get("email").toString());
                    }

                }

            }
        });

    }



    @Override
    protected void onStart() {
        CarregaComponenetes();
        super.onStart();
    }

    private void CarregaComponenetes(){

        NavigationView nav = (NavigationView)findViewById(R.id.nav_view);
        View header = nav.getHeaderView(0);
        account_name = (TextView)header.findViewById(R.id.account_name);
        account_email = (TextView)header.findViewById(R.id.account_email);
        buscaInformacoes();

        /*View menu =  (View)findViewById(R.id.app_bar_tela_principal);
        Toolbar bar= (Toolbar)menu.findViewById(R.id.toolbar);
        logout = bar.findViewById(R.id.logout).getId();*/


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tela__principal, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_tela_principal);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
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