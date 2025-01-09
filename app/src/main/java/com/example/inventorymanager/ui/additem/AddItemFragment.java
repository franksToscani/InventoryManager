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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.inventorymanager.R;
import com.example.inventorymanager.data.database.InventoryDatabase;
import com.example.inventorymanager.data.model.InventoryItem;
import com.example.inventorymanager.ui.barcode.BarcodeScannerFragment;

public class AddItemFragment extends Fragment {
    private EditText editTextName, editTextQuantity, editTextCategory, editTextEanCode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        // Inizializza i campi di input
        editTextName = view.findViewById(R.id.edit_text_name);
        editTextQuantity = view.findViewById(R.id.edit_text_quantity);
        editTextCategory = view.findViewById(R.id.edit_text_category);
        editTextEanCode = view.findViewById(R.id.edit_text_ean_code); // Campo per l'EAN-13

        CardView cardSave = view.findViewById(R.id.card_save);
        CardView cardScan = view.findViewById(R.id.card_scan);

        cardSave.setOnClickListener(v -> saveItem());
        cardScan.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main, new BarcodeScannerFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Recupera i dati scansionati dal Bundle e popola i campi
        if (getArguments() != null) {
            if (getArguments().containsKey("name")) {
                String name = getArguments().getString("name");
                Log.d("AddItemFragment", "Nome ricevuto: " + name);
                editTextName.setText(name);
            }
            if (getArguments().containsKey("category")) {
                String category = getArguments().getString("category");
                Log.d("AddItemFragment", "Categoria ricevuta: " + category);
                editTextCategory.setText(category);
            }
            if (getArguments().containsKey("eanCode")) {
                float eanCode = getArguments().getFloat("eanCode");
                Log.d("AddItemFragment", "EAN ricevuto: " + eanCode);
                editTextEanCode.setText(String.valueOf(eanCode));
            }
        }

        return view;
    }

    // Salvataggio dell'articolo nel database locale
    private void saveItem() {
        String name = editTextName.getText().toString().trim();
        String quantityStr = editTextQuantity.getText().toString().trim();
        String category = editTextCategory.getText().toString().trim();
        String eanCodeStr = editTextEanCode.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(quantityStr) || TextUtils.isEmpty(category) || TextUtils.isEmpty(eanCodeStr)) {
            Toast.makeText(requireContext(), "Compila tutti i campi", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityStr);
        float eanCode;
        try {
            eanCode = Float.parseFloat(eanCodeStr);
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "EAN non valido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crea un oggetto InventoryItem
        InventoryItem item = new InventoryItem();
        item.setName(name);
        item.setQuantity(quantity);
        item.setCategory(category);
        item.setEanCode((long) eanCode);

        // Salva l'articolo nel database in un thread separato
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
