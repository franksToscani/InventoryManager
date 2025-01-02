package com.example.inventorymanager.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.inventorymanager.data.database.InventoryDatabase;
import com.example.inventorymanager.data.model.InventoryItem;

public class AddItemViewModel extends AndroidViewModel {
    private final InventoryDatabase database;

    public AddItemViewModel(@NonNull Application application) {
        super(application);
        database = InventoryDatabase.getInstance(application);
    }

    public void addItem(InventoryItem item) {
        new Thread(() -> database.inventoryDao().insertItem(item)).start();
    }
}
