package com.example.inventorymanager.ui.list;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventorymanager.R;
import com.example.inventorymanager.data.database.InventoryDatabase;
import com.example.inventorymanager.ui.additem.AddItemFragment;
import com.example.inventorymanager.viewmodel.ListViewModel;
import com.example.inventorymanager.viewmodel.ListViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ListFragment extends Fragment {
    private ItemAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        SearchView searchView = view.findViewById(R.id.search_view);
        FloatingActionButton fab = view.findViewById(R.id.fab_add);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ItemAdapter(requireContext());
        recyclerView.setAdapter(adapter);


        // Inizializza il ViewModel
        ListViewModelFactory factory = new ListViewModelFactory(requireContext());
        ListViewModel viewModel = new ViewModelProvider(this, factory).get(ListViewModel.class);

        // Osserva i dati
        viewModel.getItems().observe(getViewLifecycleOwner(), items -> adapter.setItems(items));

        // Configura il SearchView per filtrare i risultati
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterList(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }

            private void filterList(String query) {
                adapter.filter(query); // Implementa il metodo filter in ItemAdapter
            }
        });

        // Aggiungi un nuovo articolo
        fab.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main, new AddItemFragment())
                    .addToBackStack(null)
                    .commit();
        });



        // Listener per eliminare un articolo
        adapter.setOnItemDeleteListener(item -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Conferma Eliminazione")
                    .setMessage("Sei sicuro di voler eliminare l'articolo " + item.getName() + "?")
                    .setPositiveButton("Sì", (dialog, which) -> {
                        new Thread(() -> {
                            InventoryDatabase db = InventoryDatabase.getInstance(requireContext().getApplicationContext());
                            db.inventoryDao().deleteItem(item);
                        }).start();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        return view;
    }

}
