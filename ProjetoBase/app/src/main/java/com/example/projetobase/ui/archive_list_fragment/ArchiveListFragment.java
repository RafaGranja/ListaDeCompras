package com.example.projetobase.ui.archive_list_fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.projetobase.databinding.FragmentArchiveListBinding;

public class ArchiveListFragment extends Fragment {

private FragmentArchiveListBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        ArchiveListViewModel homeViewModel =
                new ViewModelProvider(this).get(ArchiveListViewModel.class);

    binding = FragmentArchiveListBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

        final TextView textView = binding.textArchiveList;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}