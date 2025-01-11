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
import com.example.inventorymanager.utils.NotificationUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> directoryPickerLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Creazione del canale di notifica
        NotificationUtils.createNotificationChannel(this);

        // Collegamento a tutti i nostri pulsanti
        setupCardListeners();

        // Configurazione del selettore di directory
        setupDirectoryPicker();
    }

    /**
     * Configura i listener per i CardView.
     */
    private void setupCardListeners() {
        CardView startInventoryCard = findViewById(R.id.card_start_inventory);
        CardView exportDataCard = findViewById(R.id.card_export_data);
        CardView analyticsCard = findViewById(R.id.card_analytics);

        // 📦 Avvia Inventario
        startInventoryCard.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });

        // 📤 Esporta Dati
        exportDataCard.setOnClickListener(v -> checkAndExportData());

        // 📊 Visualizza Analisi
        analyticsCard.setOnClickListener(v -> getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new AnalyticsFragment())
                .addToBackStack(null)
                .commit());
    }

    /**
     * Configura il selettore di directory per l'esportazione.
     */
    private void setupDirectoryPicker() {
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
    }

    /**
     * Verifica se ci sono dati da esportare prima di avviare la selezione della directory.
     */
    private void checkAndExportData() {
        new Thread(() -> {
            InventoryDatabase db = InventoryDatabase.getInstance(this);
            List<InventoryItem> items = db.inventoryDao().getItemsForCsvExport();

            runOnUiThread(() -> {
                if (items == null || items.isEmpty()) {
                    Toast.makeText(this, "Non ci sono articoli nel database!", Toast.LENGTH_LONG).show();
                } else {
                    // 📂 Avvia la selezione della directory
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    directoryPickerLauncher.launch(intent);
                }
            });
        }).start();
    }

    /**
     * Gestisce l'esportazione dei dati in CSV nella directory selezionata.
     */
    private void exportCsvToDirectory(Uri directoryUri) {
        new Thread(() -> {
            try {
                DocumentFile pickedDir = DocumentFile.fromTreeUri(this, directoryUri);
                if (pickedDir == null) {
                    showToastOnUiThread("Errore: directory non valida!");
                    return;
                }

                DocumentFile csvFile = pickedDir.createFile("text/csv", "inventory_data.csv");
                if (csvFile == null) {
                    showToastOnUiThread("Errore durante la creazione del file!");
                    return;
                }

                try (OutputStream outputStream = getContentResolver().openOutputStream(csvFile.getUri())) {
                    if (outputStream != null) {
                        CsvExporter.exportToStream(this, outputStream);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "File esportato con successo!", Toast.LENGTH_LONG).show();
                            NotificationUtils.showExportSuccessNotification(this);
                        });
                    }
                }
            } catch (IOException e) {
                showToastOnUiThread("Errore durante l'esportazione: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Mostra un Toast dal thread UI.
     */
    private void showToastOnUiThread(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }
}
