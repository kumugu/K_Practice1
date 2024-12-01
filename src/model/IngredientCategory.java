package model;

public class IngredientCategory {
    private int categoryId; // 카테고리 ID
    private String categoryName; // 카테고리 이름

    // 매개변수 생성자
    public IngredientCategory(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    // 기본 생성자 (필요한 경우 추가)
    public IngredientCategory() {
    }

    // Getters and Setters
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
