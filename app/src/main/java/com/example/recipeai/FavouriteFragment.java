package com.example.recipeai;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class FavouriteFragment extends Fragment {

    private FavouriteRecipeManager favouriteRecipeManager;
    private ListView favouriteListView;

    public FavouriteFragment() {
        // Required empty public constructor
    }

    public static FavouriteFragment newInstance() {
        FavouriteFragment fragment = new FavouriteFragment();
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
        return inflater.inflate(R.layout.fragment_favourite, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        favouriteListView = view.findViewById(R.id.favourite_list);

        updateList();
    }

    public void updateList() {
        FavouriteAdapter adapter = new FavouriteAdapter(getContext(), getMAFavouriteRecipeManager().getFavouriteRecipes());
        favouriteListView.setAdapter(adapter);
    }

    public FavouriteRecipeManager getMAFavouriteRecipeManager() {
        return ((MainActivity)getContext()).getFavouriteRecipeManager();
    }

}