package com.example.cdcnpmat.Model.Bean;

public class Comments {
    private int id;
    private String userId;
    private int articleId;
    private String comment;
    private String date;

    public Comments(int id, String userId, int articleId, String comment, String date) {
        this.id = id;
        this.userId = userId;
        this.articleId = articleId;
        this.comment = comment;
        this.date = date;
    }

    // Getters và Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public int getArticleId() {
        return articleId;
    }
    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
}
