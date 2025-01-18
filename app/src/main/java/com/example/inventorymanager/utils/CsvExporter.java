package com.example.inventorymanager.utils;

import android.content.Context;

import com.example.inventorymanager.data.database.InventoryDatabase;
import com.example.inventorymanager.data.model.InventoryItem;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class CsvExporter {

    /**
     * Esporta i dati dell'inventario in uno stream specificato (es. file CSV in una directory scelta).
     * @param context      Il contesto dell'applicazione.
     * @param outputStream Lo stream in cui scrivere i dati CSV.
     */
    public static void exportToStream(Context context, OutputStream outputStream) throws IOException {
        // Ottieni il database e i dati degli articoli
        InventoryDatabase db = InventoryDatabase.getInstance(context);
        List<InventoryItem> items = db.inventoryDao().getItemsForCsvExport();

        // Costruisci il contenuto CSV
        StringBuilder csvContent = new StringBuilder();
        csvContent.append("ID,Nome,Quantità,Categoria\n"); // Intestazioni delle colonne

        for (InventoryItem item : items) {
            csvContent.append(item.getId()).append(",");
            csvContent.append(item.getName()).append(",");
            csvContent.append(item.getQuantity()).append(",");
            csvContent.append(item.getCategory()).append("\n");
            csvContent.append(item.getEanCode()).append("\n");
        }

        // Scrivi i dati nello stream
        outputStream.write(csvContent.toString().getBytes());
        outputStream.flush();
    }
}
