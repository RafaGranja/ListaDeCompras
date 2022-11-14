package com.example.projetobase.ui.analytics_fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.projetobase.databinding.FragmentAnalyticsBinding;

public class AnalyticsFragment extends Fragment {

    private FragmentAnalyticsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        AnalyticsViewModel slideshowViewModel =
                new ViewModelProvider(this).get(AnalyticsViewModel.class);

        binding = FragmentAnalyticsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textAnalytics;
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}