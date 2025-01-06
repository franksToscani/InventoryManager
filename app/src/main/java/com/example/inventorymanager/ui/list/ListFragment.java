package com.example.inventorymanager.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventorymanager.R;
import com.example.inventorymanager.data.database.InventoryDatabase;
import com.example.inventorymanager.data.model.InventoryItem;
import com.example.inventorymanager.ui.additem.AddItemFragment;
import com.example.inventorymanager.ui.edititem.EditItemFragment;
import com.example.inventorymanager.viewmodel.ListViewModel;
import com.example.inventorymanager.viewmodel.ListViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ListFragment extends Fragment {
    private ItemAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        View emptyView = view.findViewById(R.id.empty_view);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ItemAdapter(requireContext());
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main, new AddItemFragment())
                    .addToBackStack(null)
                    .commit();
        });

        ListViewModelFactory factory = new ListViewModelFactory(requireContext());
        ListViewModel viewModel = new ViewModelProvider(this, factory).get(ListViewModel.class);

        viewModel.getItems().observe(getViewLifecycleOwner(), items -> {
            if (items == null || items.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                adapter.setItems(items);
            }
        });

        // Listener per cliccare sull'articolo (modifica)
        adapter.setOnItemClickListener(item -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("item", item);
            EditItemFragment fragment = new EditItemFragment();
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main, fragment)
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
    @Override
    public void onResume() {
        super.onResume();
        // Forziamo sto cazzo di ricaricamento dei dati ogni volta che si torna alla lista
        ListViewModelFactory factory = new ListViewModelFactory(requireContext());
        ListViewModel viewModel = new ViewModelProvider(this, factory).get(ListViewModel.class);

        viewModel.getItems().observe(getViewLifecycleOwner(), items -> {
            adapter.setItems(items);
        });
    }

}
