package com.example.inventorymanager.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.inventorymanager.data.model.InventoryItem;

import java.util.List;

@Dao
public interface InventoryDao {
    @Insert
    void insertItem(InventoryItem item);

    @Update
    void updateItem(InventoryItem item);

    @Delete
    void deleteItem(InventoryItem item);


    @Query("SELECT * FROM inventory_items ORDER BY name ASC")
    LiveData<List<InventoryItem>> getAllItems();

}
