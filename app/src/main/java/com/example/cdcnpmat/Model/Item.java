package com.example.cdcnpmat.Model;

import java.util.UUID;

public class Item {
    private UUID id; // id của bài viết
    private String title; // tiêu đề bài viết
    private String content; // nội dung bài viết
    private UUID authorId; // id tác giả
    private String status; // trạng thái bài viết (e.g., "published", "draft")
    private String imageUrl; // link hình ảnh
    private UUID categoryId; // id danh mục

    // Constructor
    public Item(UUID id, String title, String content, UUID authorId, String status, String imageUrl, UUID categoryId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.status = status;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;

    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public void setAuthorId(UUID authorId) {
        this.authorId = authorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }
}
