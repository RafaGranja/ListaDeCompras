package com.example.projetobase;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.projetobase.ui.archive_list_fragment.ArchiveListFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ArchivedListAdapter extends RecyclerView.Adapter<ArchivedList> {

    private static final int SUCCESS =  1;
    private static final int WARNING =  0;
    private static final int ERROR =  -1;

    public ArchivedListAdapter(List<Archived> itens_archived,Context _context1,Context _context) {
        this.itens=itens_archived;
        context=_context;
        context1=_context1;
    }

    public static class Archived{

        String nome;
        LocalDate data;

        SimpleDateFormat formatter;

        Archived(String _nome){

            this.nome  =_nome;
            this.formatter = new SimpleDateFormat("dd/MM/yyyy");
            formatter.setLenient (false);

        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        Archived(String _nome, String _data){

            this.nome  =_nome;
            this.formatter = new SimpleDateFormat("dd/MM/yyyy");
            formatter.setLenient (false);
            this.data= LocalDate.of(Integer.valueOf(_data.split("-")[0]),
                                    Integer.valueOf(_data.split("-")[1]),
                                    Integer.valueOf(_data.split("-")[2]));


        }

        Archived(){
            this.nome="Sem nome";
        }

        public String getNome(){
            return this.nome;
        }

        public void setNome(String _nome){
            this.nome=_nome;
        }

        public String getData(){
            String ret = this.data.toString();
            return ret.split("-")[2]+"/"+ret.split("-")[1]+"/"+ret.split("-")[0];
        }

        public void setData(LocalDate _data){ this.data=_data; }

    }

    List<Archived> itens;
    Context context;
    Context context1;
    View root;


    @NonNull
    @Override

    public ArchivedList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.archived_item_list,parent,false);
        root = (View)parent.getRootView();
        Log.d("ROOT", "Root : "+ root.toString());
        return new ArchivedList(view).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull ArchivedList holder, int position) {

        holder.nome.setText(itens.get(position).getNome());
        holder.data.setText(itens.get(position).getData());

    }

    public void update(List<ArchivedListAdapter.Archived> mDataset){
        this.itens = mDataset;
        notifyDataSetChanged();

    }


    @Override
    public int getItemCount() {

        return itens.size();

    }

}


class ArchivedList extends RecyclerView.ViewHolder{

    private static final int SUCCESS =  1;
    private static final int WARNING =  0;
    private static final int ERROR =  -1;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    TextView nome;
    TextView data;
    FloatingActionButton info;
    RecyclerView recycle;
    NewListAdapter adapter_edit;
    View view;
    ProgressBar bar ;

    private ArchivedListAdapter adapter;

    public ArchivedList(@NonNull View itemView) {

        super(itemView);

        view = itemView;
        nome = itemView.findViewById(R.id.edit_nome_lista);
        info = itemView.findViewById(R.id.info_archived_list);
        data = itemView.findViewById(R.id.edit_data_lista);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(adapter.context).setView(R.layout.dialog_edit_list);
                AlertDialog editar_lista = dialog.show();
                bar = (ProgressBar)view.getRootView().findViewById(R.id.progress_bar_total);
                editar_lista.findViewById(R.id.button_edit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editar_lista.dismiss();
                        Intent intent = new Intent(view.getContext(), EditList.class);
                        intent.putExtra("nome",nome.getText().toString());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        view.getContext().startActivity(intent);

                    }
                });
                editar_lista.findViewById(R.id.button_rename).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        EditText nome_rename =(EditText)editar_lista.findViewById(R.id.edit_text_rename);
                        nome_rename.setVisibility(View.VISIBLE);
                        AppCompatButton confirma =  editar_lista.findViewById(R.id.button_confirm_rename);
                        confirma.setVisibility(View.VISIBLE);
                        AppCompatButton cancela = editar_lista.findViewById(R.id.button_delete_rename);
                        cancela.setVisibility(View.VISIBLE);

                        editar_lista.findViewById(R.id.button_delete).setVisibility(View.GONE);
                        editar_lista.findViewById(R.id.button_rename).setVisibility(View.GONE);
                        editar_lista.findViewById(R.id.button_edit).setVisibility(View.GONE);

                        confirma.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String nome_ = nome_rename.getText().toString();

                                if(nome_.equals(nome.getText().toString())){

                                    Toast(WARNING, "A lista já possui esse nome");

                                }
                                else {
                                    if (!NomeIgual(nome_)) {


                                        editar_lista.dismiss();
                                        EditaNome(nome.getText().toString(), nome_);
                                        for (int i = 0; i < adapter.itens.size(); i++) {

                                            String a = adapter.itens.get(i).getNome();
                                            if (a.equals(nome.getText().toString())) {
                                                adapter.itens.get(i).setNome(nome_);
                                            }

                                        }
                                        Toast(SUCCESS, "Lista " + nome.getText().toString() + " renomeada para " + nome_);
                                        adapter.update(adapter.itens);
                                        adapter.notifyDataSetChanged();

                                    } else {
                                        Toast(ERROR, "Você já possui uma lista com esse nome");
                                    }
                                }

                            }
                        });

                        cancela.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                editar_lista.dismiss();

                            }
                        });


                    }
                });
                editar_lista.findViewById(R.id.button_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        editar_lista.dismiss();
                        ExcluiLista(nome.getText().toString());
                        for(int i=0; i < adapter.itens.size();i++){

                            String a = adapter.itens.get(i).getNome();
                            if(a.equals(nome.getText().toString())){
                                adapter.itens.remove(i);
                            }

                        }
                        Toast(SUCCESS,"Lista "+nome.getText().toString()+" deletada com sucesso");
                        adapter.update(adapter.itens);
                        adapter.notifyDataSetChanged();
                    }
                });

            }
        });

    }

    public boolean NomeIgual(String nome){

        for(int i=0;i<adapter.itens.size();i++){

            if(adapter.itens.get(i).getNome().toString().equals(nome)){
                return true;
            }

        }

        return false;

    }

    private void EditaNome(String nome_1,String nome_2){

        List<Listas> archived = new LinkedList<>();
        //IniciaCarregamento();

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference doc = db.collection("ARCHIVED_LIST").document(userID);
        if(doc.equals(null)){
            InsereLista(archived,nome_2);
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
                            LocalDate datacricao = (LocalDate) item.get("datacricao");
                            List<NewListAdapter.Item> lista = new LinkedList<>();
                            List<HashMap<String, String>> nodes = (List<HashMap<String, String>>) item.get("itens");
                            for (int j = 0; j < nodes.size(); j++) {

                                HashMap<String, String> node = nodes.get(j);
                                Log.d("value", "262 - " + item.toString());
                                NewListAdapter.Item a = new NewListAdapter.Item();
                                a.setObs(node.get("obs"));
                                a.setProduto(node.get("produto"));
                                a.setQtd(node.get("qtd"));
                                lista.add(a);

                            }
                            if(nome_lista.equals(nome_1)){
                                archived.add(new Listas(nome_2, lista,datacricao));
                            }
                            else{
                                archived.add(new Listas(nome_lista, lista,datacricao));
                            }


                        }

                        Log.d("db", "271 - Sucesso ao salvar dados da lista do usuario " + userID);

                        InsereLista(archived,nome_1);

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
                //TerminaCarregamento();
                //Toast(SUCCESS,"Lista "+lista_nome+" salva com sucesso");
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

    public void IniciaCarregamento(){

        bar.bringToFront();
        bar.setVisibility(View.VISIBLE);

    }

    public void TerminaCarregamento(){

        bar.setVisibility(View.INVISIBLE);

    }

    private void ExcluiLista(String _nome){

        List<Listas> archived = new LinkedList<>();
        //IniciaCarregamento();

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference doc = db.collection("ARCHIVED_LIST").document(userID);
        if(doc.equals(null)){
            InsereLista(archived,_nome);
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
                            LocalDate datacricao = (LocalDate) item.get("datacricao");
                            if(!nome_lista.equals(_nome)){

                                List<NewListAdapter.Item> lista = new LinkedList<>();
                                List<HashMap<String, String>> nodes = (List<HashMap<String, String>>) item.get("itens");
                                for (int j = 0; j < nodes.size(); j++) {

                                    HashMap<String, String> node = nodes.get(j);
                                    Log.d("value", "262 - " + item.toString());
                                    NewListAdapter.Item a = new NewListAdapter.Item();
                                    a.setObs(node.get("obs"));
                                    a.setProduto(node.get("produto"));
                                    a.setQtd(node.get("qtd"));
                                    lista.add(a);

                                }
                                archived.add(new Listas(nome_lista, lista,datacricao));

                            }


                        }

                        Log.d("db", "271 - Sucesso ao salvar dados da lista do usuario " + userID);

                        InsereLista(archived,_nome);

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


    public ArchivedList linkAdapter(ArchivedListAdapter adapter){

        this.adapter = adapter;
        return this;

    }

    private void Toast(int type, String message){

        View v = LayoutInflater.from(adapter.context).inflate(R.layout.layout_toast,null);
        TextView txt = v.findViewById(R.id.text_message);
        txt.setText(message);
        ImageView ic = v.findViewById(R.id.image_message);
        v.setElevation(200);

        switch (type){
            case ERROR:
                v.setBackground(ContextCompat.getDrawable(adapter.context,R.drawable.error_toast));
                ic.setImageResource(R.drawable.error_toast_ic);
                break;

            case WARNING:
                v.setBackground(ContextCompat.getDrawable(adapter.context,R.drawable.warning_toast));
                ic.setImageResource(R.drawable.warning_toast_ic);
                break;

            case SUCCESS:
                v.setBackground(ContextCompat.getDrawable(adapter.context,R.drawable.sucess_toast));
                ic.setImageResource(R.drawable.sucess_toast_ic);
                break;

            default:
                break;
        }
        Toast toast = new Toast(adapter.context);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(v);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();

    }





}
