package com.taskmanagement.dto.response;

public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private String colorCode;

    public CategoryResponse() {
    }

    public CategoryResponse(Long id, String name, String description, String colorCode) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.colorCode = colorCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }
}
