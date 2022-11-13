package com.example.projetobase.ui.archive_list_fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ArchiveListViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ArchiveListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Arquive suas despesas!");
    }

    public LiveData<String> getText() {
        return mText;
    }
}