package com.example.recipeai;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class LeftoversFragment extends Fragment {

    private ArrayList<Leftover> leftovers;
    private ListView leftoversList;
    private TextView leftoversTitle;

    public LeftoversFragment() {
        // Required empty public constructor
    }

    public static LeftoversFragment newInstance() {
        LeftoversFragment fragment = new LeftoversFragment();
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
        return inflater.inflate(R.layout.fragment_leftovers, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        leftoversList = view.findViewById(R.id.leftover_list);
        leftoversTitle = view.findViewById(R.id.leftovers_title);
        leftovers = new ArrayList<>();
        setLoading();
    }

    private void setLeftoversTitle(String text) {
        leftoversTitle.setText(text);
    }

    public void setLoading() {
        setLeftoversTitle("Loading...");
    }

    public void responseReceived(ArrayList<Leftover> leftovers) {
        this.leftovers = leftovers;
        setLeftoversTitle("Your leftovers!");
        updateList();
    }

    public void updateList() {
        LeftoversAdapter adapter = new LeftoversAdapter(getContext(), leftovers);
        leftoversList.setAdapter(adapter);
    }
}