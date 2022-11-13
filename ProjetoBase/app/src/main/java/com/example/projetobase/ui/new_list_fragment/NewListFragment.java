package com.example.projetobase.ui.new_list_fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.projetobase.databinding.FragmentNewListBinding;

public class NewListFragment extends Fragment {

private FragmentNewListBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        NewListViewModel galleryViewModel =
                new ViewModelProvider(this).get(NewListViewModel.class);

    binding = FragmentNewListBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

        final TextView textView = binding.textNewList;
        galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}