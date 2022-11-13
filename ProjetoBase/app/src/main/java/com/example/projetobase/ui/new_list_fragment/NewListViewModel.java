package com.example.projetobase.ui.new_list_fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NewListViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public NewListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Crie sua Lista de Compra!");
    }

    public LiveData<String> getText() {
        return mText;
    }
}