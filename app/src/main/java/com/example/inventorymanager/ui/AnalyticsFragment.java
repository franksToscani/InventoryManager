package com.example.inventorymanager.ui.analytics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.inventorymanager.R;
import com.example.inventorymanager.data.database.InventoryDatabase;
import com.example.inventorymanager.data.model.InventoryItem;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analytics, container, false);

        // Trova il grafico a barre
        BarChart barChart = view.findViewById(R.id.bar_chart);

        // Carica i dati dal database in un thread separato
        new Thread(() -> {
            InventoryDatabase db = InventoryDatabase.getInstance(requireContext());
            List<InventoryItem> items = db.inventoryDao().getItemsForCsvExport();

            // Esegui l'aggiornamento del grafico sulla UI thread
            requireActivity().runOnUiThread(() -> {
                if (items == null || items.isEmpty()) {
                    barChart.setNoDataText("Nessun dato disponibile");
                } else {
                    // Prepara i dati per il grafico
                    List<BarEntry> entries = new ArrayList<>();
                    List<String> labels = new ArrayList<>();

                    for (int i = 0; i < items.size(); i++) {
                        InventoryItem item = items.get(i);
                        entries.add(new BarEntry(i, item.getQuantity()));
                        labels.add(item.getCategory());
                    }

                    BarDataSet dataSet = new BarDataSet(entries, "Quantità per Categoria");
                    dataSet.setColor(getResources().getColor(R.color.blue)); // Colore delle barre

                    BarData barData = new BarData(dataSet);
                    barData.setBarWidth(0.9f); // Larghezza delle barre

                    barChart.setData(barData);
                    barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                    barChart.getXAxis().setGranularity(1f); // Assicura che i valori siano allineati
                    barChart.getXAxis().setGranularityEnabled(true);

                    barChart.getDescription().setText("Analisi Quantità");
                    barChart.getDescription().setTextSize(12f);
                    barChart.setFitBars(true); // Assicura che le barre siano visibili
                    barChart.invalidate(); // Aggiorna il grafico
                }
            });
        }).start();

        return view;
    }
}
