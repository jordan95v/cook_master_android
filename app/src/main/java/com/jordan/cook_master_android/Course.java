package com.jordan.cook_master_android;

public class Course {

    private String name;
    private String content;

    private String image;

    private Integer difficulty;

    public Course(String name, String content, String image, Integer difficulty) {
        this.name = name;
        this.content = content;
        this.image = image;
        this.difficulty = difficulty;
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

