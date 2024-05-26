package com.example.recipeai;

import android.graphics.Color;

import java.io.Serializable;

public class Food implements Serializable {
    private String name;
    private Color color;

    public Food(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Color getColor() {
        return this.color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
