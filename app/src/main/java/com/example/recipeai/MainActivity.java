package com.example.recipeai;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    public static final int CAMERA_ACTION_CODE = 1;
    Button takePhotoBtn;
    ImageView imagePreview;
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        takePhotoBtn = findViewById(R.id.takePhotoBtn);
        imagePreview = findViewById(R.id.photoPreview);

        });
    private void takePhotoActivity() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activityResultLauncher.launch(intent);
    }

    private Bitmap getImageFromActivityResult(ActivityResult result) {
        Bundle bundle = result.getData().getExtras();
        Bitmap bitmap = (Bitmap) bundle.get("data");
        return bitmap;
    }

    private void showImage(Bitmap bitmap) {
        imagePreview.setImageBitmap(bitmap);
    }

    private void saveBitmapToGallery(Bitmap bitmap) throws IOException {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Audio.Media.TITLE, "Recent Picture");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "?");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        OutputStream outputStream = getContentResolver().openOutputStream(uri);

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        outputStream.flush();
        outputStream.close();
    }
}