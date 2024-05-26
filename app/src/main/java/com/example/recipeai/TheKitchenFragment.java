package com.example.recipeai;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class TheKitchenFragment extends Fragment {

    private ArrayList<Recipe> recipes;
    private ListView recipesListView;
    private Button loadRecipesButton;

    public TheKitchenFragment() {

    }

    public static TheKitchenFragment newInstance() {
        TheKitchenFragment fragment = new TheKitchenFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_the_kitchen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recipesListView = view.findViewById(R.id.recipes_list_view);
        loadRecipesButton = view.findViewById(R.id.load_recipes_button);
        recipes = new ArrayList<>();

        loadRecipesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipes = new ArrayList<>();
                setLoading();
                loadRecipes();
            }
        });
    }

    private void setLoading() {
        loadRecipesButton.setText("Loading...");
    }

    private void setLoaded() {
        loadRecipesButton.setText("Load Recipes");
    }

    public void loadRecipes() {
        getMainActivity().getRecipes();
    }

    public void setRecipes(ArrayList<Recipe> recipes) {
        setLoaded();
        this.recipes = recipes;
        updateList();
    }

    public MainActivity getMainActivity() {
        return (MainActivity)getActivity();
    }

    public void updateList() {
        RecipeAdapter adapter = new RecipeAdapter(getContext(), recipes);
        recipesListView.setAdapter(adapter);
    }
}