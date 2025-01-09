package com.example.inventorymanager.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "inventory_items")
public class InventoryItem implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private int quantity;
    private String category;
    private long eanCode = 0;

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getEanCode() {
        return eanCode;
    }

    public void setEanCode(long eanCode) {
        this.eanCode = eanCode;
    }
}
