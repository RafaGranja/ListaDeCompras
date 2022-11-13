package com.example.projetobase.ui.analytics_fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AnalyticsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AnalyticsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Analise seus gastos!");
    }

    public LiveData<String> getText() {
        return mText;
    }
}