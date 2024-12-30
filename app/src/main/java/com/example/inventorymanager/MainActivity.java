package com.example.inventorymanager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.inventorymanager.data.database.InventoryDao;
import com.example.inventorymanager.data.database.InventoryDatabase;
import com.example.inventorymanager.data.model.InventoryItem;
import com.example.inventorymanager.ui.list.ListFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Imposta il layout dell'activity
        setContentView(R.layout.activity_main);

        // Gestisci i margini per evitare sovrapposizioni con le barre di sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Popola il database con dati di esempio in un thread separato
        new Thread(() -> {
            InventoryDatabase db = InventoryDatabase.getInstance(getApplicationContext());
            InventoryDao inventoryDao = db.inventoryDao(); // Metodo corretto
            if (inventoryDao != null) {
                InventoryItem item1 = new InventoryItem();
                item1.setName("Laptop");
                item1.setQuantity(5);
                item1.setCategory("Elettronica");

                InventoryItem item2 = new InventoryItem();
                item2.setName("Mouse");
                item2.setQuantity(15);
                item2.setCategory("Accessori");

                InventoryItem item3 = new InventoryItem();
                item3.setName("Tastiera");
                item3.setQuantity(10);
                item3.setCategory("Accessori");

                inventoryDao.insertItem(item1);
                inventoryDao.insertItem(item2);
                inventoryDao.insertItem(item3);
            }
        }).start();

        // Mostra il ListFragment nel contenitore R.id.main
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main, new ListFragment()) // Usa l'ID 'main' del tuo layout
                    .commit();
        }
    }
}
