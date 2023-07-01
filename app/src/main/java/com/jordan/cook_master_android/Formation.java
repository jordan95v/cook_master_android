package com.jordan.cook_master_android;

public class Formation {
    private String name;
    private String description;

    private String image;

    private Integer courses_count;

    public Formation(String titre, String description, String image, Integer courses_count) {
        this.name = titre;
        this.description = description;
        this.image = image;
        this.courses_count = courses_count;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public Integer getCoursesCount() {
        return courses_count;
    }

    @Override
    public String toString() {
        return name;
    }
}
