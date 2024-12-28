package com.example.inventorymanager.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.inventorymanager.data.database.InventoryDatabase;
import com.example.inventorymanager.data.model.InventoryItem;

import java.util.List;

public class ListViewModel extends ViewModel {
    private LiveData<List<InventoryItem>> items;

    public ListViewModel(Context context) {
        // Ottieni i dati dal database
        InventoryDatabase db = InventoryDatabase.getInstance(context); // Initialize the database here
        items = db.inventoryDao().getAllItems();
    }

    public LiveData<List<InventoryItem>> getItems() {
        return items;
    }
}
