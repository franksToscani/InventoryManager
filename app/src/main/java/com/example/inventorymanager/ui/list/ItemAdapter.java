package com.example.inventorymanager.ui.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventorymanager.R;
import com.example.inventorymanager.data.model.InventoryItem;
import com.example.inventorymanager.ui.edititem.EditItemDialog;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<InventoryItem> items = new ArrayList<>();
    private List<InventoryItem> allItems = new ArrayList<>();  // Lista completa per il filtro
    private final Context context;
    private OnItemClickListener clickListener;
    private OnItemDeleteListener deleteListener;

    // Costruttore
    public ItemAdapter(Context context) {
        this.context = context;
    }

    // Imposta gli articoli e aggiorna la lista completa per la ricerca
    public void setItems(List<InventoryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        allItems.clear();
        allItems.addAll(newItems);
        notifyDataSetChanged();
    }

    // Listener per click sugli articoli
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    // Listener per la cancellazione degli articoli
    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.deleteListener = listener;
    }

    // Metodo per filtrare la lista
    public void filter(String query) {
        items.clear();
        if (query == null || query.isEmpty()) {
            items.addAll(allItems);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (InventoryItem item : allItems) {
                if (item.getName().toLowerCase().contains(lowerCaseQuery) ||
                        item.getCategory().toLowerCase().contains(lowerCaseQuery) ||
                        String.valueOf(item.getEanCode()).contains(lowerCaseQuery)) {
                    items.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_inventory, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        InventoryItem item = items.get(position);

        holder.nameTextView.setText(item.getName());
        holder.categoryTextView.setText(item.getCategory());
        holder.eanTextView.setText("EAN: " + item.getEanCode());
        holder.quantityTextView.setText(String.valueOf(item.getQuantity()));

        // Applica animazione al tocco
        applyClickAnimation(holder.itemView);

        // Listener per il click sull'articolo (modifica)
        holder.itemView.setOnClickListener(v -> {
            EditItemDialog dialog = new EditItemDialog(item);
            dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "EditItemDialog");
        });
        // Listener per il click sul pulsante option (modifica)
        holder.button_options.setOnClickListener(v -> {
            EditItemDialog dialog = new EditItemDialog(item);
            dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "EditItemDialog");
        });


        // Listener per il pulsante di eliminazione
        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onItemDelete(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Interfaccia per il click sugli articoli
    public interface OnItemClickListener {
        void onItemClick(InventoryItem item);
    }

    // Interfaccia per la cancellazione degli articoli
    public interface OnItemDeleteListener {
        void onItemDelete(InventoryItem item);
    }

    // ViewHolder per gestire i componenti della card
    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, categoryTextView, eanTextView, quantityTextView;
        ImageButton deleteButton;
        ImageButton button_options;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.item_name);
            categoryTextView = itemView.findViewById(R.id.item_category);
            eanTextView = itemView.findViewById(R.id.item_ean);
            quantityTextView = itemView.findViewById(R.id.item_quantity);
            deleteButton = itemView.findViewById(R.id.button_delete);
            button_options = itemView.findViewById(R.id.button_options);
        }
    }

    // Metodo per gestire l'animazione al tocco
    private void applyClickAnimation(View view) {
        view.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Animation scaleUp = AnimationUtils.loadAnimation(context, R.anim.scale_up);
                v.startAnimation(scaleUp);
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                Animation scaleDown = AnimationUtils.loadAnimation(context, R.anim.scale_down);
                v.startAnimation(scaleDown);
            }
            return false;
        });
    }
}
