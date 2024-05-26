package com.example.recipeai;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class TakePhotoFragment extends Fragment {

    Button takePhotoButton;
    TextView scannedTextView;
    ImageView photoImageView;

    public TakePhotoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_take_photo, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        scannedTextView = getActivity().findViewById(R.id.scannedTextView);
        takePhotoButton = getActivity().findViewById(R.id.takePhotoBtn);
        photoImageView = getActivity().findViewById(R.id.photoPreview);

        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).takePhoto();
            }
        });

    }

    public void changePhotoPreview(Bitmap bitmap) {
        photoImageView.setImageBitmap(bitmap);
    }

    public void changeScannedText(String text) {
        scannedTextView.setText(text);
    }

}