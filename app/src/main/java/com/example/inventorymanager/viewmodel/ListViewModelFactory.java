package com.example.inventorymanager.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ListViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public ListViewModelFactory(Context context) {
        this.context = context.getApplicationContext(); // Usa il contesto dell'applicazione
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ListViewModel.class)) {
            return (T) new ListViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
