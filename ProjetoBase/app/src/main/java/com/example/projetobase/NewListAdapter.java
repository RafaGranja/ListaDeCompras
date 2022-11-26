package com.example.projetobase;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;


public class NewListAdapter extends RecyclerView.Adapter<NewList> {

    public static class Item{

        String produto;
        String qtd;
        String obs;

        public Item(String _prod, String _qtd, String _obs){

            this.produto = _prod;
            this.qtd = _qtd;
            this.obs = _obs;

        }

        public Item(){

            this.produto = "";
            this.qtd = "";
            this.obs = "";

        }

        public String getQtd() {
            return qtd;
        }

        public String getProduto() {
            return produto;
        }

        public String getObs() {
            return obs;
        }

        public void setObs(String obs) {
            this.obs = obs;
        }

        public void setProduto(String produto) {
            this.produto = produto;
        }

        public void setQtd(String qtd) {
            this.qtd = qtd;
        }

    }

    List<Item> itens;

    public NewListAdapter(List<Item> itens){
        this.itens=itens;
    }

    @NonNull
    @Override
    public NewList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,parent,false);
        return new NewList(view).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull NewList holder, int position) {

        holder.position.setText(String.valueOf(position));
        holder.produto.setText(itens.get(position).getProduto());
        holder.quantidade.setText(itens.get(position).getQtd());
        //holder.quantidade.setText(String.valueOf(position));
        holder.obs.setText(itens.get(position).getObs());

    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    public void update(List<Item> mDataset){
        this.itens = mDataset;
        notifyDataSetChanged();
        SalvaLista();

    }

    public void SalvaLista(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String,Object> item = new HashMap<>();
        Log.d("ITENS","125 - "+itens.toString());

        ArrayList<HashMap<String,String>> inner = new ArrayList<>();
        Log.d("value","460 - "+inner.toString());
        for(int i=0;i<itens.size();i++){

            HashMap<String,String> b = new HashMap<>();
            b.put("obs", itens.get(i).getObs().toString());
            b.put("produto",itens.get(i).getProduto().toString());
            b.put("qtd",itens.get(i).getQtd().toString());
            inner.add(b);
        }
        item.put("ITENS",inner);

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference doc = db.collection("CURRENT_LIST").document(userID);
        doc.set(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("db","133 - Sucesso ao salvar dados da atual lista do usuario "+userID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("db", "138 - Erro ao salvar dados da atual lista do usuario " + userID +" "+ e.toString());
            }
        });
    }

}

class NewList extends RecyclerView.ViewHolder{

    EditText produto;
    EditText quantidade;
    EditText obs;
    TextView position;

    private NewListAdapter adapter;

    public NewList(@NonNull View itemView){
        super(itemView);

        produto = itemView.findViewById(R.id.edit_produto);
        quantidade= itemView.findViewById(R.id.edit_produto_qtd);
        obs = itemView.findViewById(R.id.edit_produto_desc);
        position = itemView.findViewById(R.id.text_position);

        itemView.findViewById(R.id.delete_item_list).setOnClickListener( view -> {

            adapter.itens.remove(getAdapterPosition());
            adapter.notifyItemRemoved(getAdapterPosition());
            if(adapter.getItemCount()==0){
                itemView.getRootView().findViewById(R.id.app_bar_tela_principal).findViewById(R.id.text_new_list).setVisibility(View.VISIBLE);
            }
            adapter.update(adapter.itens);

        });
        produto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                //adapter.itens.get(Integer.parseInt(position.getText().toString())).setProduto(charSequence.toString());

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                adapter.itens.get(Integer.parseInt(position.getText().toString())).setProduto(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

                adapter.itens.get(Integer.parseInt(position.getText().toString())).setProduto(editable.toString());

            }
        });
        quantidade.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                //adapter.itens.get(Integer.parseInt(position.getText().toString())).setQtd(Float.parseFloat(charSequence.toString()));

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                adapter.itens.get(Integer.parseInt(position.getText().toString())).setQtd(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

                adapter.itens.get(Integer.parseInt(position.getText().toString())).setQtd(editable.toString());

            }
        });
        obs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                //adapter.itens.get(Integer.parseInt(position.getText().toString())).setObs(charSequence.toString());

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                adapter.itens.get(Integer.parseInt(position.getText().toString())).setObs(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

                adapter.itens.get(Integer.parseInt(position.getText().toString())).setObs(editable.toString());

            }
        });



    }

    public NewList linkAdapter(NewListAdapter adapter){

            this.adapter = adapter;
            return this;

    }


}
