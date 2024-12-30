package com.example.inventorymanager.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventorymanager.R;
import com.example.inventorymanager.viewmodel.ListViewModel;
import com.example.inventorymanager.viewmodel.ListViewModelFactory;
import com.example.inventorymanager.ui.list.ItemAdapter;

public class ListFragment extends Fragment {
    private ItemAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Infla il layout per il fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        // Configura il RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ItemAdapter();
        recyclerView.setAdapter(adapter);

        // Inizializza il ViewModel usando la Factory
        ListViewModelFactory factory = new ListViewModelFactory(requireContext());
        ListViewModel viewModel = new ViewModelProvider(this, factory).get(ListViewModel.class);

        // Osserva i dati del ViewModel
        viewModel.getItems().observe(getViewLifecycleOwner(), items -> {
            adapter.setItems(items); // Aggiorna l'adapter con i dati
        });

        return view;
    }
}
