package model;

public class MenuItem {
    private int menuId;
    private String productName;
    private double price;
    private int displayOrder;
    private String categoryName;

    public MenuItem(int menuId, String productName, double price, int displayOrder, String categoryName) {
        this.menuId = menuId;
        this.productName = productName;
        this.price = price;
        this.displayOrder = displayOrder;
        this.categoryName = categoryName;
    }

    public int getMenuId() {
        return menuId;
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
