package com.example.inventorymanager.ui.edititem;

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

public class EditItemFragment extends Fragment {
    private EditText editTextName, editTextQuantity, editTextCategory;
    private InventoryItem item;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_item, container, false);

        editTextName = view.findViewById(R.id.edit_text_name);
        editTextQuantity = view.findViewById(R.id.edit_text_quantity);
        editTextCategory = view.findViewById(R.id.edit_text_category);
        Button buttonUpdate = view.findViewById(R.id.button_update);

        if (getArguments() != null) {
            item = (InventoryItem) getArguments().getSerializable("item");
            if (item != null) {
                editTextName.setText(item.getName());
                editTextQuantity.setText(String.valueOf(item.getQuantity()));
                editTextCategory.setText(item.getCategory());
            }
        }

        buttonUpdate.setOnClickListener(v -> updateItem());

        return view;
    }

    private void updateItem() {
        String name = editTextName.getText().toString().trim();
        String quantityStr = editTextQuantity.getText().toString().trim();
        String category = editTextCategory.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(quantityStr) || TextUtils.isEmpty(category)) {
            Toast.makeText(requireContext(), "Compila tutti i campi", Toast.LENGTH_SHORT).show();
            return;
        }

        item.setName(name);
        item.setQuantity(Integer.parseInt(quantityStr));
        item.setCategory(category);
        // Log per debug
        Log.d("EditItemFragment", "Aggiornamento articolo: " + item.getName() + ", Quantità: " + item.getQuantity() + ", Categoria: " + item.getCategory());

        //aggiornamento dell'articolo nel db
        new Thread(() -> {
            InventoryDatabase db = InventoryDatabase.getInstance(requireContext().getApplicationContext());
            db.inventoryDao().updateItem(item);

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Articolo aggiornato con successo:)!", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            });
        }).start();
    }
}
