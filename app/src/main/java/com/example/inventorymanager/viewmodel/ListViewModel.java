package com.example.inventorymanager.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.inventorymanager.data.database.InventoryDatabase;
import com.example.inventorymanager.data.model.InventoryItem;

import java.util.List;

public class ListViewModel extends ViewModel {
    private LiveData<List<InventoryItem>> items;//contiene la lista degli articoli di inventario.
    //questa è una classe che permette di osservare i cambiamenti nei dati.

    public ListViewModel(Context context) {
        InventoryDatabase db = InventoryDatabase.getInstance(context);//recupera un'istanza del database locale.
        items = db.inventoryDao().getAllItems();//ritorna tutti gli articoli dell'inventario come LiveDatae e li mette in items.
    }

    //Getter per la lista degli articoli.
    //Questo metodo restituisce la lista di articoli in tempo reale.
    //La UI osserva questa lista e si aggiorna quando i dati cambiano.
    public LiveData<List<InventoryItem>> getItems() {
        return items;
    }
}

/* Problema Potenziale
Il costruttore di un ViewModel non dovrebbe ricevere un Context direttamente perché potrebbe causare Memory Leaks.
Soluzione: ho usato una ViewModelFactory per passare il contesto in modo sicuro.(ListViewModelFactory.java)
*/