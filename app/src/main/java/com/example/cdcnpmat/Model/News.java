package com.example.cdcnpmat.Model;

public class News {
    private String id; // ID của tin tức
    private String title; // Tên tin tức
    private String link; // Link RSS

    // Constructor mặc định (bắt buộc cho Firebase)
    public News() {
    }

    // Constructor đầy đủ
    public News(String id, String title, String link) {
        this.id = id;
        this.title = title;
        this.link = link;
    }

    // Getter và Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
