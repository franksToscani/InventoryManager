package com.example.inventorymanager.data.database;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.inventorymanager.data.model.InventoryItem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Database(entities = {InventoryItem.class}, version = 3, exportSchema = false)
public abstract class InventoryDatabase extends RoomDatabase {

    // Singleton instance
    private static volatile InventoryDatabase INSTANCE;

    // DAO
    public abstract InventoryDao inventoryDao();

    // Metodo per ottenere l'istanza del database
    public static synchronized InventoryDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (InventoryDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    InventoryDatabase.class, "inventory_database")
                            .fallbackToDestructiveMigration()
                            .build();

                            /*Room.databaseBuilder(context.getApplicationContext(),
                                    InventoryDatabase.class, "inventory_database")
                            .addMigrations(MIGRATION_2_3) // Migrazione dalla versione 2 alla 3
                            .fallbackToDestructiveMigration() // Resetta il database in caso di cambiamenti nella struttura
                            .build();*/
                }
            }
        }
        return INSTANCE;
    }

    // Migrazione da versione 1 a 2 a 3
    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE inventory_items ADD COLUMN eanCode INTEGER NOT NULL DEFAULT 0");
        }
    };


}
