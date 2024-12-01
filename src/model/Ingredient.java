package model;

import java.math.BigDecimal;

public class Ingredient {
    private int ingredientId;
    private String name;
    private BigDecimal unitPrice;
    private String unit;
    private int categoryId;
    private String categoryName;

    // 기본 생성자
    public Ingredient() {}

    // 생성자 1 (카테고리 이름이 없는 경우)
    public Ingredient(int ingredientId, String name, BigDecimal unitPrice, String unit, int categoryId) {
        this.ingredientId = ingredientId;
        this.name = name;
        this.unitPrice = unitPrice;
        this.unit = unit;
        this.categoryId = categoryId;
    }

    // 생성자 2 (카테고리 이름이 포함된 경우)
    public Ingredient(int ingredientId, String name, BigDecimal unitPrice, String unit, int categoryId, String categoryName) {
        this.ingredientId = ingredientId;
        this.name = name;
        this.unitPrice = unitPrice;
        this.unit = unit;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    // Getter and Setter methods
    public int getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
