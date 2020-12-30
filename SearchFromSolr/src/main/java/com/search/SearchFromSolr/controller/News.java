package com.search.SearchFromSolr.controller;

public class News {
    private String id;
    private String address;
    private String body;
    private String title;

    public News(String id, String body, String title, String address){
        this.id = id;
        this.body = body;
        this.title = title;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "News {id:" + id + ", body:" + body + ", title:" + title +", address:" + address+ "}";
    }

}
