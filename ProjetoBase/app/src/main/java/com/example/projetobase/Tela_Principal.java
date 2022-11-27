package com.example.projetobase;


import static com.example.projetobase.NewListAdapter.*;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.projetobase.databinding.ActivityTelaPrincipalBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class Tela_Principal extends AppCompatActivity {

    private static final int SUCCESS =  1;
    private static final int WARNING =  0;
    private static final int ERROR =  -1;

    private TextView account_name;
    private TextView account_email;

    public List<Item> itens;
    public List<ArchivedListAdapter.Archived> itens_archived;
    public NewListAdapter adapter;
    public ArchivedListAdapter adapter_archived;
    public int counter=0;
    public FloatingActionButton ButtonAdiconarCard;
    public FloatingActionButton ButtonDeletarLista;
    public FloatingActionButton ButtonSalvarLista;
    public SwipeRefreshLayout refresh_new_list;
    public SwipeRefreshLayout refresh_archived_list;
    public AlertDialog alerta_salvar_lista;

    //private int logout;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userID;

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityTelaPrincipalBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        itens = new LinkedList<>();
        itens_archived = new LinkedList<>();
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

                Log.d("ConfiguraLista","119 - Vou configurar lista");
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
                        itens.add(0,new Item());
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

                                ArquivaLista();

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

                refresh_new_list.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        RetomaLista();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ReindexaLista();
                                refresh_new_list.setRefreshing(false);

                            }
                        },1500);

                    }
                });
                EscondeFundo();

            }
        },500);

    }

    public void ConfiguraListaArchived(){
        new Handler().postDelayed(new Runnable() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void run() {

                Log.d("ConfiguraListaArchived","119 - Vou configurar lista");
                RecyclerView recycle = findViewById(R.id.archived_list);
                recycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter_archived = new ArchivedListAdapter(itens_archived);
                recycle.setAdapter(adapter_archived);
                refresh_archived_list = (SwipeRefreshLayout)findViewById(R.id.app_bar_tela_principal).findViewById(R.id.refresh_archived_list);

                refresh_archived_list.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        RetomaListaArchived();
                        refresh_archived_list.setRefreshing(false);


                    }
                });

                EscondeFundoArchived();

            }
        },500);

    }

    public void EscondeFundoArchived(){

        if (itens_archived.isEmpty() == false) {
            findViewById(R.id.app_bar_tela_principal).findViewById(R.id.text_archive_list).setVisibility(View.INVISIBLE);
        }else {
            findViewById(R.id.app_bar_tela_principal).findViewById(R.id.text_archive_list).setVisibility(View.VISIBLE);
        }

    }

    public void ArquivaLista(){

         alerta_salvar_lista = new  AlertDialog.Builder(this).setView(R.layout.dialog_save_list).show();

         EditText nome = alerta_salvar_lista.findViewById(R.id.edit_text_name);

         alerta_salvar_lista.findViewById(R.id.button_confirm).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                String lista_nome = nome.getText().toString();
                if(lista_nome.trim().isEmpty()){
                    Toast(ERROR,"Insira um nome...");
                }
                else{

                    AdicionaLista(lista_nome);

                }
             }
         });

         alerta_salvar_lista.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alerta_salvar_lista.hide();

            }
         });

    }

    private void AdicionaLista(String lista_nome){

        List<String> nomes = new LinkedList<>();
        List<Listas> archived = new LinkedList<>();
        IniciaCarregamento();
        alerta_salvar_lista.hide();

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference doc = db.collection("ARCHIVED_LIST").document(userID);
        if(doc.equals(null)){
            InsereLista(archived, lista_nome);
        }
        else {

            doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){

                        DocumentSnapshot value = task.getResult();

                        Log.d("value", "248 - " + value.get("LISTAS").getClass().toString());

                        ArrayList<HashMap<String, Object>> ret = (ArrayList<HashMap<String, Object>>) value.get("LISTAS");
                        Log.d("value", "251 - " + ret.toString());
                        for (int i = 0; i < ret.size(); i++) {

                            HashMap<String,Object> item = (HashMap<String, Object>)ret.get(i);
                            Log.d("value", "255 - " + item.toString());
                            nomes.add(item.get("nome").toString());
                            String nome_lista = item.get("nome").toString();
                            List<Item> lista = new LinkedList<>();
                            List<HashMap<String, String>> nodes = (List<HashMap<String, String>>) item.get("itens");
                            for (int j = 0; j < nodes.size(); j++) {

                                HashMap<String, String> node = nodes.get(j);
                                Log.d("value", "262 - " + item.toString());
                                Item a = new Item();
                                a.setObs(node.get("obs"));
                                a.setProduto(node.get("produto"));
                                a.setQtd(node.get("qtd"));
                                lista.add(a);

                            }
                            archived.add(new Listas(nome_lista, lista));

                        }

                        Log.d("db", "271 - Sucesso ao salvar dados da lista do usuario " + userID);


                        if (nomes.contains(lista_nome)) {
                            TerminaCarregamento();
                            alerta_salvar_lista.show();
                            Toast(ERROR, "Já existe uma lista com esse nome");
                            Log.d("db", "271 - Já existe uma lista com esse nome " + userID);
                        } else {
                            InsereLista(archived, lista_nome);
                            itens.clear();
                            ReindexaLista();
                        }

                    }
                    else{

                        Log.d("ERROR DOC", "244 - Listen failed");
                        TerminaCarregamento();
                        alerta_salvar_lista.show();
                        Toast(ERROR,"Erro ao salvar lista");

                    }
                }
            });

        }
    }

    public void InsereLista(List<Listas> archived, String lista_nome){
        archived.add(new Listas(lista_nome,itens));
        List<Map<String,Object>> add = new LinkedList<>();

        for (int i=0;i<archived.size();i++){

            Listas a = archived.get(i);

            Map<String,Object> lista = new HashMap<>();
            lista.put("nome",a.getNome());

            List<Map<String,String>> itens = new LinkedList<>();
            for (int j=0;j<a.itens.size();j++){

                Map<String,String> b = new HashMap<>();
                Item c = a.itens.get(j);
                b.put("produto",c.getProduto());
                b.put("qtd",c.getQtd());
                b.put("obs",c.getObs());
                itens.add(b);

            }
            lista.put("itens",itens);
            add.add(lista);
        }


        Map<String,Object> item = new HashMap<>();
        item.put("LISTAS",add);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference doc = db.collection("ARCHIVED_LIST").document(userID);
        doc.set(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("db","306 - Sucesso ao salvar dados da lista "+lista_nome+" do usuario "+userID);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TerminaCarregamento();
                        DeletaLista();
                        alerta_salvar_lista.hide();
                        Toast(SUCCESS,"Lista "+lista_nome+" salva com sucesso");
                    }
                },500);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("db", "311 - Erro ao salvar dados da lista "+lista_nome+" do usuario " + userID +" "+ e.toString());
                TerminaCarregamento();
                alerta_salvar_lista.show();
                Toast(ERROR,"Erro ao salvar lista");
            }
        });


    }

    public void DeletaLista(){

        itens.clear();
        ReindexaLista();
        Toast(SUCCESS,"Lista limpa!");

    }

    public void ReindexaLista(){

        adapter.update(itens);
        EscondeFundo();

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

        try {
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if(userID == null){
                throw new NullPointerException();
            }else{

                DocumentReference doc = db.collection("SOCIAL").document(userID);

                doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {

                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        if(value != null) {
                            if (value.exists()) {

                                account_name.setText(value.get("login").toString());
                                if (value.get("email") == null || value.get("email") == "") {
                                    account_email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                } else {
                                    account_email.setText(value.get("email").toString());
                                }

                            }
                        }

                    }
                });

            }
        }
        catch (Exception e ){
            Log.d("383 - buscaInformacoes",e.getMessage().toString());
        }

    }


    public void IniciaCarregamento(){

        ProgressBar bar = (ProgressBar)findViewById(R.id.progress_bar_total);
        bar.setVisibility(View.VISIBLE);

    }

    public void TerminaCarregamento(){

        ProgressBar bar = (ProgressBar)findViewById(R.id.progress_bar_total);
        bar.setVisibility(View.INVISIBLE);

    }

    private void CarregaComponenetes(){

        NavigationView nav = (NavigationView)findViewById(R.id.nav_view);
        View header = nav.getHeaderView(0);
        account_name = (TextView)header.findViewById(R.id.account_name);
        account_email = (TextView)header.findViewById(R.id.account_email);
        refresh_new_list = (SwipeRefreshLayout)findViewById(R.id.app_bar_tela_principal).findViewById(R.id.refresh_new_list);

        IniciaCarregamento();

        buscaInformacoes();

        ConfiguraLista();

        RetomaLista();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ReindexaLista();

                TerminaCarregamento();
            }
        },1500);

        nav.getMenu().findItem(R.id.nav_new_list_fragment).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                ConfiguraLista();
                return false;
            }
        });

        nav.getMenu().findItem(R.id.nav_archive_list_fragment).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                IniciaCarregamento();
                ConfiguraListaArchived();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RetomaListaArchived();
                        TerminaCarregamento();
                    }
                },500);
                return false;
            }
        });


    }

    public void RetomaListaArchived(){

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        itens_archived.clear();
        ReindexaListaArchived();

        DocumentReference doc = db.collection("ARCHIVED_LIST").document(userID);
        if(!doc.equals(null)){

            doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){

                        DocumentSnapshot value = task.getResult();

                        Log.d("value", "248 - " + value.get("LISTAS").getClass().toString());

                        ArrayList<HashMap<String, Object>> ret = (ArrayList<HashMap<String, Object>>) value.get("LISTAS");
                        Log.d("value", "251 - " + ret.toString());
                        for (int i = 0; i < ret.size(); i++) {

                            HashMap<String,Object> item = (HashMap<String, Object>)ret.get(i);
                            Log.d("value", "255 - " + item.toString());
                            itens_archived.add(new ArchivedListAdapter.Archived(item.get("nome").toString()));
                            adapter_archived.notifyItemInserted(0);

                        }

                        Log.d("db", "271 - Sucesso ao salvar dados da lista do usuario " + userID);

                        EscondeFundoArchived();
                    }
                    else{

                        Log.d("ERROR DOC", "244 - Listen failed");
                        Toast(ERROR,"Erro ao recuperar lista");

                    }
                }
            });

        }

    }

    public void ReindexaListaArchived(){

        adapter_archived.update(itens_archived);
        EscondeFundoArchived();
    }

    public void RetomaLista(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                DocumentReference doc = db.collection("CURRENT_LIST").document(userID);

                doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        if(value != null) {
                            if (value.exists()) {

                                itens.clear();

                                Log.d("value", "455 - " + value.get("ITENS").getClass().toString());
                                //findViewById(R.id.app_bar_tela_principal).findViewById(R.id.text_new_list).setVisibility(View.VISIBLE);

                                ArrayList<HashMap<String, String>> ret = (ArrayList<HashMap<String, String>>) value.get("ITENS");
                                Log.d("value", "460 - " + ret.toString());
                                for (int i = 0; i < ret.size(); i++) {
                                    Log.d("value", "460 - " + ret.get(i).toString());
                                    HashMap<String, String> item = (HashMap<String, String>) ret.get(i);
                                    Log.d("value", "464 - " + item.toString());
                                    Item a = new Item();
                                    a.setObs(item.get("obs").toString());
                                    a.setProduto(item.get("produto").toString());
                                    a.setQtd(item.get("qtd").toString());
                                    itens.add(a);
                                    counter++;
                                    //findViewById(R.id.app_bar_tela_principal).findViewById(R.id.text_new_list).setVisibility(View.INVISIBLE);

                                    //itens.add(new Item(item.getProduto().toString(),item.getQtd().toString(),item.getObs().toString()));
                                }
                                Log.d("db", "475 - Sucesso ao retornar dados da atual lista do usuario " + userID);
                            }
                        }

                    }

                });

                EscondeFundo();

            }
        },500);


    }

    public void EscondeFundo(){

        if (itens.isEmpty() == false) {
            findViewById(R.id.app_bar_tela_principal).findViewById(R.id.text_new_list).setVisibility(View.INVISIBLE);
        }else {
            findViewById(R.id.app_bar_tela_principal).findViewById(R.id.text_new_list).setVisibility(View.VISIBLE);
        }

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