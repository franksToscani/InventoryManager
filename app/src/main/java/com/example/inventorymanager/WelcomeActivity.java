package com.example.inventorymanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.documentfile.provider.DocumentFile;

import com.example.inventorymanager.data.database.InventoryDatabase;
import com.example.inventorymanager.data.model.InventoryItem;
import com.example.inventorymanager.ui.AnalyticsFragment;
import com.example.inventorymanager.utils.CsvExporter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> directoryPickerLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Riferimenti ai CardView
        CardView startInventoryCard = findViewById(R.id.card_start_inventory);
        CardView exportDataCard = findViewById(R.id.card_export_data);
        CardView analyticsCard = findViewById(R.id.card_analytics);

        // Configura il launcher per selezionare la directory
        directoryPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri directoryUri = result.getData().getData();
                        exportCsvToDirectory(directoryUri);
                    } else {
                        Toast.makeText(this, "Selezione directory annullata", Toast.LENGTH_SHORT).show();
                    }
                });

        // Listener per "Inizio Inventario"
        startInventoryCard.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Listener per "Esporta Dati dell'Inventario"
        exportDataCard.setOnClickListener(v -> {
            // Controlla se ci sono articoli nel database
            new Thread(() -> {
                InventoryDatabase db = InventoryDatabase.getInstance(this);
                List<InventoryItem> items = db.inventoryDao().getItemsForCsvExport();

                runOnUiThread(() -> {
                    if (items == null || items.isEmpty()) {
                        Toast.makeText(this, "Non ci sono articoli nel database!", Toast.LENGTH_LONG).show();
                    } else {
                        // Avvia la selezione della directory
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        directoryPickerLauncher.launch(intent);
                    }
                });
            }).start();
        });

        // Listener per "Visualizza Analisi"
        analyticsCard.setOnClickListener(v -> {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AnalyticsFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void exportCsvToDirectory(Uri directoryUri) {
        new Thread(() -> {
            try {
                // Uso DocumentFile per poter creare il file all'interno della directory selezionata
                DocumentFile pickedDir = DocumentFile.fromTreeUri(this, directoryUri);
                if (pickedDir == null) {
                    runOnUiThread(() -> Toast.makeText(this, "Errore: directory non valida!", Toast.LENGTH_LONG).show());
                    return;
                }

                // Creazione del file CSV
                DocumentFile csvFile = pickedDir.createFile("text/csv", "inventory_data.csv");
                if (csvFile == null) {
                    runOnUiThread(() -> Toast.makeText(this, "Errore durante la creazione del file!", Toast.LENGTH_LONG).show());
                    return;
                }

                try (OutputStream outputStream = getContentResolver().openOutputStream(csvFile.getUri())) {
                    if (outputStream != null) {
                        CsvExporter.exportToStream(this, outputStream);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "File esportato con successo!", Toast.LENGTH_LONG).show();

                            // per Tornare alla WelcomeActivity
                            Intent intent = new Intent(this, WelcomeActivity.class);
                            startActivity(intent);
                            finish();
                        });
                    }
                }
            } catch (IOException e) {
                runOnUiThread(() -> Toast.makeText(this, "Errore durante l'esportazione: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}
