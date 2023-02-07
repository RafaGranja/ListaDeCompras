package com.example.projetobase;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EditList extends AppCompatActivity {

    private static final int SUCCESS =  1;
    private static final int WARNING =  0;
    private static final int ERROR =  -1;

    RecyclerView recycle;
    NewListAdapter adapter;
    List<NewListAdapter.Item> itens_edit;
    public String nome;
    SwipeRefreshLayout refresh;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);
        getSupportActionBar().hide();
        nome = getIntent().getExtras().get("nome").toString();
        //IniciaCarregamento();
        itens_edit = new LinkedList<>();

        ConfiguraListaEdit();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RetomaLista();
                //TerminaCarregamento();
            }
        },500);

        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh_edit_list);

    }

    public void ConfiguraListaEdit(){
        new Handler().postDelayed(new Runnable() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void run() {

                recycle = (RecyclerView) findViewById(R.id.archived_list_edit);
                recycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new NewListAdapter(itens_edit,false);
                recycle.setAdapter(adapter);
                FloatingActionButton ButtonSalvarLista = findViewById(R.id.fab_save_edit_list_archived);
                FloatingActionButton ButtonDeletarLista = findViewById(R.id.fab_delete_edit_list_archived);
                FloatingActionButton ButtonAdiconarCard = findViewById(R.id.fab_add_archive);

                ButtonAdiconarCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itens_edit.add(0,new NewListAdapter.Item());
                        adapter.notifyItemInserted(0);
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

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(EditList.this, Tela_Principal.class);
                                        intent.putExtra("fragment","archived");
                                        startActivity(intent);
                                        finish();
                                    }
                                },500);

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

                refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        RetomaLista();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ReindexaLista();
                                refresh.setRefreshing(false);

                            }
                        },500);

                    }
                });

                ReindexaLista();

            }
        },500);
    }

    public void DeletaLista(){

        itens_edit.clear();
        ReindexaLista();
        Toast(SUCCESS,"Lista limpa!");

    }

    public void DeletaLista2(){

        itens_edit.clear();
        ReindexaLista();

    }

    private void SalvaLista(){

        List<Listas> archived = new LinkedList<>();
        //IniciaCarregamento();

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference doc = db.collection("ARCHIVED_LIST").document(userID);
        if(doc.equals(null)){
            InsereLista(archived,nome);
        }
        else {

            doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
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
                            String nome_lista = item.get("nome").toString();
                            String[] data = item.get("datacriacao").toString().split("-");
                            LocalDate datacriacao = LocalDate.of(Integer.valueOf(data[0]),Integer.valueOf(data[1]),Integer.valueOf(data[2]));
                            List<NewListAdapter.Item> lista = new LinkedList<>();
                            List<HashMap<String, String>> nodes = (List<HashMap<String, String>>) item.get("itens");
                            if(!nome_lista.equals(nome)){

                                for (int j = 0; j < nodes.size(); j++) {

                                    HashMap<String, String> node = nodes.get(j);
                                    Log.d("value", "262 - " + item.toString());
                                    NewListAdapter.Item a = new NewListAdapter.Item();

                                    a.setObs(node.get("obs"));
                                    a.setProduto(node.get("produto"));
                                    a.setQtd(node.get("qtd"));
                                    lista.add(a);

                                }

                                archived.add(new Listas(nome_lista, lista,datacriacao));

                            }
                            else{

                                archived.add(new Listas(nome, itens_edit,datacriacao));

                            }


                        }

                        Log.d("db", "271 - Sucesso ao salvar dados da lista do usuario " + userID);

                        InsereLista(archived,nome);
                        itens_edit.clear();
                        ReindexaLista();

                    }
                    else{

                        Log.d("ERROR DOC", "244 - Listen failed");
                        //TerminaCarregamento();
                        Toast(ERROR,"Erro ao salvar lista");

                    }
                }
            });

        }
    }

    public void InsereLista(List<Listas> archived, String lista_nome){

        List<Map<String,Object>> add = new LinkedList<>();

        for (int i=0;i<archived.size();i++){

            Listas a = archived.get(i);

            Map<String,Object> lista = new HashMap<>();
            lista.put("nome",a.getNome());
            lista.put("datacriacao",a.getData());

            List<Map<String,String>> itens = new LinkedList<>();
            for (int j=0;j<a.itens.size();j++){

                Map<String,String> b = new HashMap<>();
                NewListAdapter.Item c = a.itens.get(j);
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

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference doc = db.collection("ARCHIVED_LIST").document(userID);
        doc.set(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("db","306 - Sucesso ao salvar dados da lista "+lista_nome+" do usuario "+userID);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //TerminaCarregamento();
                        DeletaLista2();
                        Toast(SUCCESS,"Lista "+lista_nome+" salva com sucesso");
                    }
                },100);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("db", "311 - Erro ao salvar dados da lista "+lista_nome+" do usuario " + userID +" "+ e.toString());
                //TerminaCarregamento();
                Toast(ERROR,"Erro ao salvar lista");
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void IniciaCarregamento(){

        ProgressBar bar = (ProgressBar)findViewById(R.id.progress_bar_total);
        bar.setVisibility(View.VISIBLE);

    }

    public void TerminaCarregamento(){

        ProgressBar bar = (ProgressBar)findViewById(R.id.progress_bar_total);
        bar.setVisibility(View.INVISIBLE);

    }

    public void RetomaLista(){

        itens_edit.clear();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference doc = db.collection("ARCHIVED_LIST").document(userID);

        if(!doc.equals(null)) {

            doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                @RequiresApi(api = Build.VERSION_CODES.N)

                @Override

                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()){

                        DocumentSnapshot value = task.getResult();

                        Log.d("value", "248 - " + value.get("LISTAS").getClass().toString());

                        ArrayList<HashMap<String, Object>> ret = (ArrayList<HashMap<String, Object>>) value.get("LISTAS");

                        Log.d("value", "251 - " + ret.toString());

                        for(int i = 0; i < ret.size(); i++) {

                            Log.d("value", "392 - Laço de repetição" + i);
                            HashMap<String, Object> item = (HashMap<String, Object>)ret.get(i);
                            Log.d("value", "251 - " + item.get("nome"));
                            Log.d("value", "251 - " + nome);
                            if(item.get("nome").toString().equals(nome)){

                                Log.d("value", "460 - " + item.get("itens").toString());

                                ArrayList<HashMap<String, String>> lista = (ArrayList<HashMap<String, String>>)item.get("itens");

                                for (int j = 0; j < lista.size() ; j++) {

                                    HashMap<String, String> item2 = (HashMap<String, String>)lista.get(j);
                                    Log.d("value", "464 - " + item.toString());
                                    NewListAdapter.Item a = new NewListAdapter.Item();
                                    a.setObs(item2.get("obs").toString());
                                    a.setProduto(item2.get("produto").toString());
                                    a.setQtd(item2.get("qtd").toString());
                                    itens_edit.add(a);
                                    adapter.notifyItemInserted(0);

                                }

                            }

                        }

                    }

                }

            });

        }

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

    public void update_edit(List<NewListAdapter.Item> mDataset){

        itens_edit = mDataset;

    }

    public void ReindexaLista(){
        update_edit(itens_edit);
        adapter.notifyDataSetChanged();
    }

    private void SalvaLista2(){

        List<Listas> archived = new LinkedList<>();

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference doc = db.collection("ARCHIVED_LIST").document(userID);
        if(doc.equals(null)){
            InsereLista(archived,nome);
        }
        else {

            doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
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
                            String nome_lista = item.get("nome").toString();
                            String[] data = item.get("datacriacao").toString().split("-");
                            LocalDate datacriacao = LocalDate.of(Integer.valueOf(data[0]),Integer.valueOf(data[1]),Integer.valueOf(data[2]));
                            List<NewListAdapter.Item> lista = new LinkedList<>();
                            List<HashMap<String, String>> nodes = (List<HashMap<String, String>>) item.get("itens");
                            if(!nome_lista.equals(nome)){
                                for (int j = 0; j < nodes.size(); j++) {

                                    HashMap<String, String> node = nodes.get(j);
                                    Log.d("value", "262 - " + item.toString());
                                    NewListAdapter.Item a = new NewListAdapter.Item();
                                    a.setObs(node.get("obs"));
                                    a.setProduto(node.get("produto"));
                                    a.setQtd(node.get("qtd"));
                                    lista.add(a);

                                }
                                archived.add(new Listas(nome_lista, lista,datacriacao));
                            }
                            else{

                                archived.add(new Listas(nome, itens_edit,datacriacao));

                            }


                        }

                        Log.d("db", "271 - Sucesso ao salvar dados da lista do usuario " + userID);

                        InsereLista2(archived,nome);

                    }
                    else{

                        Log.d("ERROR DOC", "244 - Listen failed");
                        Toast(ERROR,"Erro ao salvar lista");

                    }
                }
            });

        }
    }

    public void InsereLista2(List<Listas> archived, String lista_nome){

        List<Map<String,Object>> add = new LinkedList<>();

        for (int i=0;i<archived.size();i++){

            Listas a = archived.get(i);

            Map<String,Object> lista = new HashMap<>();
            lista.put("nome",a.getNome());

            List<Map<String,String>> itens = new LinkedList<>();
            for (int j=0;j<a.itens.size();j++){

                Map<String,String> b = new HashMap<>();
                NewListAdapter.Item c = a.itens.get(j);
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

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference doc = db.collection("ARCHIVED_LIST").document(userID);
        doc.set(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("db","306 - Sucesso ao salvar dados da lista "+lista_nome+" do usuario "+userID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("db", "311 - Erro ao salvar dados da lista "+lista_nome+" do usuario " + userID +" "+ e.toString());
            }
        });


    }
}