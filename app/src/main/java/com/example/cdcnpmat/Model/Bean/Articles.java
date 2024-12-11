package com.example.cdcnpmat.Model.Bean;

public class Articles {
    public int id;
    public String title;
    public String publishDate;
    public int views;
    public String abstractContent;
    public String content;
    public int categoriesId;
    public int kindsId;
    public String writerId;
    public int statusId;

    public Articles(int id, String title, String publishDate, int views, String abstractContent, String content, int categoriesId, int kindsId, String writerId, int statusId) {
        this.id = id;
        this.title = title;
        this.publishDate = publishDate;
        this.views = views;
        this.abstractContent = abstractContent;
        this.content = content;
        this.categoriesId = categoriesId;
        this.kindsId = kindsId;
        this.writerId = writerId;
        this.statusId = statusId;

    }

    // Getters và Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getPublishDate() {
        return publishDate;
    }
    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }
    public int getViews() {
        return views;
    }
    public void setViews(int views) {
        this.views = views;
    }
    public String getAbstractContent() {
        return abstractContent;
    }
    public void setAbstractContent(String abstractContent) {
        this.abstractContent = abstractContent;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public int getCategoriesId() {
        return categoriesId;
    }
    public void setCategoriesId(int categoriesId) {
        this.categoriesId = categoriesId;
    }
    public int getKindsId() {
        return kindsId;
    }
    public void setKindsId(int kindsId) {
        this.kindsId = kindsId;
    }
    public String getWriterId() {
        return writerId;
    }
    public void setWriterId(String writerId) {
        this.writerId = writerId;
    }
    public int getStatusId() {
        return statusId;
    }
    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

}
