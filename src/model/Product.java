package model;

import java.math.BigDecimal;

public class Product {
    private int productId;
    private int categoryId;
    private String categoryName; // 카테고리 이름
    private String name;
    private BigDecimal price;

    public Product(int productId, int categoryId, String categoryName, String name, BigDecimal price) {
        this.productId = productId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.name = name;
        this.price = price;
    }


    // 생성자 오버로드 (카테고리 이름 없는 경우)
    public Product(int productId, int categoryId, String name, BigDecimal price) {
        this(productId, categoryId, null, name, price);
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name; // 상품명 반환
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }


}
