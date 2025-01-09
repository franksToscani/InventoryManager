package com.example.inventorymanager.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.inventorymanager.data.model.InventoryItem;

import java.util.List;

@Dao
public interface InventoryDao {

    // Inserisce un nuovo articolo
    @Insert
    void insertItem(InventoryItem item);

    // Aggiorna un articolo esistente
    @Update
    void updateItem(InventoryItem item);

    // Elimina un articolo
    @Delete
    void deleteItem(InventoryItem item);

    // Ottiene tutti gli articoli come LiveData
    @Query("SELECT * FROM inventory_items")
    LiveData<List<InventoryItem>> getAllItems();

    // Ottiene tutti gli articoli per l'esportazione in formato CSV (non LiveData)
    @Query("SELECT * FROM inventory_items")
    List<InventoryItem> getItemsForCsvExport();

    @Query("SELECT * FROM inventory_items WHERE eanCode = :eanCode")
    InventoryItem getItemByEanCode(long eanCode);
}
