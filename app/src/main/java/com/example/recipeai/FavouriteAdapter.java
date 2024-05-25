package com.example.recipeai;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FavouriteAdapter extends ArrayAdapter<Recipe> {

    private List<Recipe> list;
    private Context context;

    TextView currentRecipeName, recipeIngredients, recipeInstructions, colorIndicator, unfavoriteRecipe,
            INGREDIENTS, INSTRUCTIONS;

    public FavouriteAdapter(Context context, List<Recipe> myRecipes) {
        super(context, 0, myRecipes);
        this.list = myRecipes;
        this.context = context;
    }


}
