package model;

import java.math.BigDecimal;

public class ProductIngredient {
    private int productId;
    private int ingredientId;
    private String ingredientName; // 재료 이름
    private BigDecimal requiredAmount;

    public ProductIngredient(int productId, int ingredientId, String ingredientName, BigDecimal requiredAmount) {
        this.productId = productId;
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.requiredAmount = requiredAmount;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

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

    public BigDecimal getRequiredAmount() {
        return requiredAmount;
    }

    public void setRequiredAmount(BigDecimal requiredAmount) {
        this.requiredAmount = requiredAmount;
    }
}
