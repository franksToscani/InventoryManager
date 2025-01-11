package com.example.inventorymanager.ui.edititem;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.inventorymanager.R;
import com.example.inventorymanager.data.database.InventoryDatabase;
import com.example.inventorymanager.data.model.InventoryItem;

public class EditItemDialog extends DialogFragment {

    private EditText editTextName, editTextQuantity, editTextCategory;
    private InventoryItem item;

    public EditItemDialog(InventoryItem item) {
        this.item = item;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edit_item, null);

        editTextName = view.findViewById(R.id.edit_text_name);
        editTextQuantity = view.findViewById(R.id.edit_text_quantity);
        editTextCategory = view.findViewById(R.id.edit_text_category);
        Button buttonUpdate = view.findViewById(R.id.button_update);

        // Popolamento dei campi con i dati dell'articolo
        if (item != null) {
            editTextName.setText(item.getName());
            editTextQuantity.setText(String.valueOf(item.getQuantity()));
            editTextCategory.setText(item.getCategory());
        }

        buttonUpdate.setOnClickListener(v -> updateItem());
        // Costruzione della finestra di dialogo con alterDialog builder
        return new AlertDialog.Builder(requireContext())
                .setView(view)
                .setTitle("Modifica Articolo selezionato")
                .setNegativeButton("Annulla", (dialog, which) -> dismiss())
                .create();
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

        // Aggiorna il nostro db con un new Thread
        new Thread(() -> {
            InventoryDatabase db = InventoryDatabase.getInstance(requireContext().getApplicationContext());
            db.inventoryDao().updateItem(item);

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Articolo aggiornato con successo!", Toast.LENGTH_SHORT).show();
                dismiss();  // Chiude la finestra di dialogo
            });
        }).start();
    }
}
