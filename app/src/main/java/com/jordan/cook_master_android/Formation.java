package com.jordan.cook_master_android;

public class Formation {

    private int id;
    private String name;
    private String description;

    private String image;

    private Integer courses_count;

    public Formation(Integer id, String name, String description, String image, Integer courses_count) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.courses_count = courses_count;
    }

    public Integer getId() {
        return id;
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
