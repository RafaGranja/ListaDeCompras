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


public class DialogEditList extends Dialog {

    public EditText editTextRename;
    public AppCompatButton edit;
    public AppCompatButton rename;
    public AppCompatButton delete;
    public String item_name;
    public String nome;
    Context context;

    public DialogEditList(Context _context,String _name) {
        super(_context);
        context= _context;
        item_name = _name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_edit_list);

        editTextRename = (EditText)findViewById(R.id.edit_text_rename);
        edit = (AppCompatButton)findViewById(R.id.button_edit);
        rename = (AppCompatButton)findViewById(R.id.button_rename);
        delete = (AppCompatButton)findViewById(R.id.button_delete);

        editTextRename.addTextChangedListener(new TextWatcher() {
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

    }
}
