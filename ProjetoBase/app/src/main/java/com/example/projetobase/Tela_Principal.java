package com.example.projetobase;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.OverScroller;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetobase.databinding.ActivityTelaPrincipalBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Tela_Principal extends AppCompatActivity {

    private static final int SUCCESS =  1;
    private static final int WARNING =  0;
    private static final int ERROR =  -1;

    private TextView account_name;
    private TextView account_email;

    List<NewListAdapter.Item> itens = new LinkedList<>();;
    NewListAdapter adapter;
    int counter=0;
    FloatingActionButton ButtonAdiconarCard;
    FloatingActionButton ButtonDeletarLista;
    FloatingActionButton ButtonSalvarLista;

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

    public void ConfiguraLista(){
        new Handler().postDelayed(new Runnable() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void run() {

                RecyclerView recycle = findViewById(R.id.item_list);
                recycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new NewListAdapter(itens);
                recycle.setAdapter(adapter);
                ButtonSalvarLista = findViewById(R.id.app_bar_tela_principal).findViewById(R.id.fab_save_list);
                ButtonDeletarLista = findViewById(R.id.app_bar_tela_principal).findViewById(R.id.fab_delete_list);
                ButtonAdiconarCard = findViewById(R.id.app_bar_tela_principal).findViewById(R.id.fab_new_list);
                ButtonAdiconarCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itens.add(0,new NewListAdapter.Item());
                        counter++;
                        adapter.notifyItemInserted(0);
                        findViewById(R.id.app_bar_tela_principal).findViewById(R.id.text_new_list).setVisibility(View.INVISIBLE);
                        recycle.smoothScrollToPosition(0);
                        ReindexaLista();

                    }
                });
                ButtonAdiconarCard.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        if(ButtonSalvarLista.getVisibility()==View.INVISIBLE) {
                            ButtonSalvarLista.setVisibility(View.VISIBLE);
                        }
                        else{
                            ButtonSalvarLista.setVisibility(View.INVISIBLE);
                        }
                        if(ButtonDeletarLista.getVisibility()==View.INVISIBLE) {
                            ButtonDeletarLista.setVisibility(View.VISIBLE);
                        }
                        else{
                            ButtonDeletarLista.setVisibility(View.INVISIBLE);
                        }

                        ButtonSalvarLista.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                SalvaLista();

                            }
                        });

                        ButtonDeletarLista.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                DeletaLista();

                            }
                        });

                        return true;
                    }
                });

                recycle.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        ButtonSalvarLista.setVisibility(View.INVISIBLE);
                        ButtonDeletarLista.setVisibility(View.INVISIBLE);
                        return false;
                    }
                });
                ReindexaLista();
            }
        },500);

    }

    public void SalvaLista(){

    }

    public void DeletaLista(){

        itens.clear();
        ReindexaLista();
        Toast(SUCCESS,"Lista limpa!");

    }

    public void ReindexaLista(){
        adapter.update(itens);
        if(itens.isEmpty()==false){
            findViewById(R.id.app_bar_tela_principal).findViewById(R.id.text_new_list).setVisibility(View.INVISIBLE);
        }

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

        ConfiguraLista();

        //RetomaLista();

        nav.getMenu().findItem(R.id.nav_new_list_fragment).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                ConfiguraLista();
                return false;
            }
        });


    }

    public void RetomaLista(){

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference doc = db.collection("CURRENT_LIST").document(userID);

        doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {

            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if(value != null ){

                    itens = (List<NewListAdapter.Item>)value.get("ITENS");
                    Log.d("db","Sucesso ao salvar dados da atual lista do usuario "+userID);
                    ReindexaLista();

                }

            }
        });

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