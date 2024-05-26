package com.example.recipeai;

import android.util.Log;

public class Leftover {

    private String name;
    private String uses;

    public Leftover(String name, String uses) {
        this.name = name;
        this.uses = uses;
    }

    public Leftover(String name) {
        this.name = name;
        this.uses = "";
    }

    public Leftover() {
        this.name = "";
        this.uses = "";
    }

    public static Leftover parseTextToLeftover(String text) {
        String[] lines = text.split("\n");
        Leftover l = new Leftover(lines[0].trim());

        String uses = "";
        for (int i = 1; i < lines.length; i++) {
            uses += lines[i].trim() + "\n";
        }

        l.setUses(uses);

        return l;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUses() {
        return this.uses;
    }

    public void setUses(String uses) {
        this.uses = uses;
    }

    public String toString() {
        return this.name + "\n" + this.uses;
    }
}
