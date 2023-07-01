package com.jordan.cook_master_android;

public class Course {

    private int id;
    private String name;
    private String content;

    private String image;

    private Integer difficulty;

    public Course(int id, String name, String content, String image, Integer difficulty) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.image = image;
        this.difficulty = difficulty;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public String getImage() {
        return image;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    @Override
    public String toString() {
        return name;
    }
}

