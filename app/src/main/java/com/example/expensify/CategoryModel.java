package com.example.expensify;

import java.io.Serializable;
import java.util.List;

public class CategoryModel implements Serializable {
    private String id, group;
    private List<Object> detail;
    public CategoryModel(String id, String group, List<Object> detail) {
        this.id = id;
        this.group = group;
        this.detail = detail;
    }

    public String getId() {
        return id;
    }

    public String getGroup() {
        return group;
    }

    public List<Object> getDetail() {
        return detail;
    }
}
