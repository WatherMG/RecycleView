package com.drwat.lab2;

public class Grocery {
    private int id;
    private String name;
    private String count;
    private String imagePath;

    public Grocery(int id, String name, String count, String imagePath) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.imagePath = imagePath;
    }

    public Grocery(int id, String name, String count) {
        this.id = id;
        this.name = name;
        this.count = count;
    }

    public Grocery() {
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
