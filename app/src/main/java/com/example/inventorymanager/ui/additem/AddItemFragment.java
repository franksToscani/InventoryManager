package com.example.inventorymanager.ui.additem;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.inventorymanager.R;
import com.example.inventorymanager.data.database.InventoryDatabase;
import com.example.inventorymanager.data.model.InventoryItem;
import com.example.inventorymanager.ui.barcode.BarcodeScannerFragment;

public class AddItemFragment extends Fragment {
    private EditText editTextName, editTextQuantity, editTextCategory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        editTextName = view.findViewById(R.id.edit_text_name);
        editTextQuantity = view.findViewById(R.id.edit_text_quantity);
        editTextCategory = view.findViewById(R.id.edit_text_category);
        Button buttonSave = view.findViewById(R.id.button_save);
        Button buttonScan = view.findViewById(R.id.button_scan);

        // Gestisce il salvataggio dell'articolo
        buttonSave.setOnClickListener(v -> saveItem());

        // Naviga al fragment di scansione
        buttonScan.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main, new BarcodeScannerFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Recupero il codice scansionato e popola i campi ho avuto problemi con il codice scansionato
        // bisognava implementare una logica per dividere il codice scansionato nei campi (Nome, Quantità, Categoria)
        // e passare questi campi a un Bundle.
        if (getArguments() != null) {
            if (getArguments().containsKey("name")) {
                String name = getArguments().getString("name");
                Log.d("AddItemFragment", "Nome ricevuto: " + name);
                editTextName.setText(name);
            }
            if (getArguments().containsKey("quantity")) {
                String quantity = getArguments().getString("quantity");
                Log.d("AddItemFragment", "Quantità ricevuta: " + quantity);
                editTextQuantity.setText(quantity);
            }
            if (getArguments().containsKey("category")) {
                String category = getArguments().getString("category");
                Log.d("AddItemFragment", "Categoria ricevuta: " + category);
                editTextCategory.setText(category);
            }
        }



        return view;
    }

    // Salvataggio dell'articolo nel database locale tramite un thread separato e navigazione alla schermata precedente
    private void saveItem() {
        String name = editTextName.getText().toString().trim();
        String quantityStr = editTextQuantity.getText().toString().trim();
        String category = editTextCategory.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(quantityStr) || TextUtils.isEmpty(category)) {
            Toast.makeText(requireContext(), "Compila tutti i campi", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityStr);

        InventoryItem item = new InventoryItem();
        item.setName(name);
        item.setQuantity(quantity);
        item.setCategory(category);

        new Thread(() -> {
            InventoryDatabase db = InventoryDatabase.getInstance(requireContext().getApplicationContext());
            db.inventoryDao().insertItem(item);

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Articolo aggiunto con successo!", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            });
        }).start();
    }
}
