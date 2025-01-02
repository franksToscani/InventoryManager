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
import androidx.lifecycle.ViewModelProvider;

import com.example.inventorymanager.R;
import com.example.inventorymanager.data.model.InventoryItem;
import com.example.inventorymanager.viewmodel.AddItemViewModel;

public class AddItemFragment extends Fragment {
    private EditText editTextName, editTextQuantity, editTextCategory;
    private AddItemViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        editTextName = view.findViewById(R.id.edit_text_name);
        editTextQuantity = view.findViewById(R.id.edit_text_quantity);
        editTextCategory = view.findViewById(R.id.edit_text_category);
        Button buttonSave = view.findViewById(R.id.button_save);

        viewModel = new ViewModelProvider(this).get(AddItemViewModel.class);

        buttonSave.setOnClickListener(v -> saveItem());

        return view;
    }

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

        viewModel.addItem(item);
        Toast.makeText(requireContext(), "Articolo salvato", Toast.LENGTH_SHORT).show();

        // Torna al fragment precedente
        requireActivity().getSupportFragmentManager().popBackStack();

        // Log per debug
        Log.d("AddItemFragment", "Articolo salvato: " + item.getName());

    }
}
