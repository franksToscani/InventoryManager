package com.example.inventorymanager.data.database;

import android.content.Context;
import android.os.Environment;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.inventorymanager.data.model.InventoryItem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Database(entities = {InventoryItem.class}, version = 1, exportSchema = false)
public abstract class InventoryDatabase extends RoomDatabase {

    // Singleton instance
    private static volatile InventoryDatabase INSTANCE;

    // DAO
    public abstract InventoryDao inventoryDao();

    // Metodo per ottenere l'istanza del database
    public static InventoryDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (InventoryDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    InventoryDatabase.class, "inventory_database")
                            .fallbackToDestructiveMigration() // Resetta il database in caso di cambiamenti nella struttura
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
