package com.example.inventorymanager.ui.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventorymanager.R;
import com.example.inventorymanager.data.model.InventoryItem;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private List<InventoryItem> items = new ArrayList<>();
    private final Context context;
    private OnItemClickListener clickListener;
    private OnItemDeleteListener deleteListener;

    public ItemAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<InventoryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.deleteListener = listener;
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
        holder.quantityTextView.setText("Quantità: " + item.getQuantity());
        holder.categoryTextView.setText("Categoria: " + item.getCategory());

        // Listener per cliccare sull'articolo (modifica)
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(item);
            }
        });

        // Listener per il pulsante "Elimina"
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

    public interface OnItemClickListener {
        void onItemClick(InventoryItem item);
    }

    public interface OnItemDeleteListener {
        void onItemDelete(InventoryItem item);
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, quantityTextView, categoryTextView;
        ImageButton deleteButton;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.item_name);
            quantityTextView = itemView.findViewById(R.id.item_quantity);
            categoryTextView = itemView.findViewById(R.id.item_category);
            deleteButton = itemView.findViewById(R.id.button_delete);
        }
    }
}
