package com.example.projetobase;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import com.example.projetobase.R;

import java.lang.reflect.Field;
import java.util.List;


public class DialogSaveList extends Dialog {

    public EditText editTextNome;
    public AppCompatButton confirm;
    public AppCompatButton cancel;
    public String nome;
    Context context;

    public DialogSaveList(Context _context) {
        super(_context);
        context= _context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_save_list);

        editTextNome = (EditText)findViewById(R.id.edit_text_name);
        confirm = (AppCompatButton)findViewById(R.id.button_confirm);
        cancel = (AppCompatButton)findViewById(R.id.button_cancel);

        editTextNome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                nome = charSequence.toString();

            }
            @Override
            public void afterTextChanged(Editable editable) {

                nome = editable.toString();

            }

        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Field itens = context.getClass().getField("itens");
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
