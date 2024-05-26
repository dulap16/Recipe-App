package com.example.recipeai;

import android.util.Log;

import java.util.ArrayList;

public class Recipe {

    private String name;
    private int cookingTime = 0;
    private int calories = 0;
    private ArrayList<String> ingredients;
    private ArrayList<String> instructions;

    private int totalIngredients = 0;
    private int totalInstructions = 0;

    public Recipe(String name, int cookingTime, int calories, ArrayList<String> ingredients, ArrayList<String> instructions) {
        this.name = name;
        this.cookingTime = cookingTime;
        this.calories = calories;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public Recipe(String name, int cookingTime, int calories) {
        this.name = name;
        this.cookingTime = cookingTime;
        this.calories = calories;

        ingredients = new ArrayList<String>();
        instructions = new ArrayList<String>();
    }

    public Recipe() {
        this.name = "New Recipe";

        ingredients = new ArrayList<String>();
        instructions = new ArrayList<String>();
    }

    public static Recipe parseTextToRecipe(String text) {
        String[] lines = text.split("\n");
        String[] firstLine = lines[0].split(" - ");
        Recipe newRecipe = new Recipe(firstLine[0].trim(), Integer.parseInt(firstLine[1].split(" ")[0].trim()), Integer.parseInt(firstLine[2].split(" ")[0].trim()));
        boolean isIngredients = true;
        for(int i = 2; i < lines.length; i++)
        {
            if(lines[i].trim().equals("INSTRUCTIONS")) {
                isIngredients = false;

                continue;
            }

            if(lines[i].trim().equals("SECOND") || lines[i].trim().equals("THIRD"))
                continue;

            if(isIngredients) {
                newRecipe.ingredients.add(lines[i].trim());
                newRecipe.totalIngredients++;
            } else {
                newRecipe.instructions.add(lines[i].trim());
                newRecipe.totalInstructions++;
            }

        }

        return newRecipe;
    }

    public String getName() {
        return this.name;
    }

    public int getCookingTime() {
        return this.cookingTime;
    }

    public int getCalories() {
        return this.calories;
    }

    public ArrayList<String> getIngredients() {
        return this.ingredients;
    }

    public ArrayList<String> getInstructions() {
        return this.instructions;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInstructions( ArrayList<String> instructions) {
        this.instructions = instructions;
    }

    public void setIngredients( ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public String toString() {
        return this.name + " - " + this.cookingTime + " minutes - " + this.calories + " calories";
    }

    public void printWholeRecipe() {
        Log.i("Recipes", this.toString());
        for(String ingredient : this.ingredients)
            Log.i("Recipes", ingredient);
        for(String instruction : this.instructions)
            Log.i("Recipes", instruction);
    }
}
