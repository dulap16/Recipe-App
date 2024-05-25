package com.example.recipeai;

import java.util.ArrayList;

public class FavouriteRecipeManager {

    private ArrayList<Recipe> favouriteRecipes;

    public FavouriteRecipeManager() {
        favouriteRecipes = new ArrayList<>();
    }

    public void addRecipe(Recipe recipe) {
        favouriteRecipes.add(recipe);
    }

    public void removeRecipe(Recipe recipe) {
        favouriteRecipes.remove(recipe);
    }

    public void removeRecipe(int index) {
        favouriteRecipes.remove(index);
    }

    public ArrayList<Recipe> getFavouriteRecipes() {
        return favouriteRecipes;
    }

    public void setFavouriteRecipes(ArrayList<Recipe> favouriteRecipes) {
        this.favouriteRecipes = favouriteRecipes;
    }
}
