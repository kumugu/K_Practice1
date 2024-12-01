package model;

import java.sql.Timestamp;

public class Stock {
    private int ingredientId;        // 재료 ID
    private String ingredientName;   // 재료명
    private double currentStock;     // 현재 재고
    private String unit;             // 단위
    private Timestamp lastOrderDate; // 최근 주문 일시

    // Getters and Setters
    public int getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public double getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(double currentStock) {
        this.currentStock = currentStock;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Timestamp getLastOrderDate() {
        return lastOrderDate;
    }

    public void setLastOrderDate(Timestamp lastOrderDate) {
        this.lastOrderDate = lastOrderDate;
    }
}
