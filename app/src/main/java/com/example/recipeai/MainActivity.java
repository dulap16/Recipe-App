package com.example.recipeai;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CAMERA_CODE = 100;
    public static final int CAMERA_ACTION_CODE = 1;

    Button copyBtn;
    Button takePhotoBtn;
    ImageView imagePreview;
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        copyBtn = findViewById(R.id.copyBtn);
        takePhotoBtn = findViewById(R.id.takePhotoBtn);
        imagePreview = findViewById(R.id.photoPreview);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                Bitmap bitmap = getImageFromActivityResult(o);
                showImage(bitmap);

                try {
                    saveBitmapToGallery(bitmap);

                    Toast.makeText(MainActivity.this, "Image saved to gallery", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhotoActivity();
            }
        });
    }

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