package com.example.projetobase;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ArchivedListAdapter extends RecyclerView.Adapter<ArchivedList> {

    private static final int SUCCESS =  1;
    private static final int WARNING =  0;
    private static final int ERROR =  -1;

    public ArchivedListAdapter(List<Archived> itens_archived) {
        this.itens=itens_archived;
    }

    public static class Archived{

        String nome;

        Archived(String _nome){
            this.nome  =_nome;
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

    }

    List<Archived> itens;

    @NonNull
    @Override

    public ArchivedList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.archived_item_list,parent,false);
        return new ArchivedList(view).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull ArchivedList holder, int position) {

        holder.nome.setText(itens.get(position).getNome());

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

    TextView nome;

    private ArchivedListAdapter adapter;

    public ArchivedList(@NonNull View itemView) {

        super(itemView);

        nome = itemView.findViewById(R.id.edit_nome_lista);

    }

    public ArchivedList linkAdapter(ArchivedListAdapter adapter){

        this.adapter = adapter;
        return this;

    }


}
