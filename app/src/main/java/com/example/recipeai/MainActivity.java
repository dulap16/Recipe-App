package com.example.recipeai;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CAMERA_CODE = 100;
    public static final int CAMERA_ACTION_CODE = 1;

    Button copyBtn;
    Button takePhotoBtn;
    ImageView imagePreview;
    TextView scannedTextView;
    ActivityResultLauncher<Intent> activityResultLauncher;

    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        copyBtn = findViewById(R.id.copyBtn);
        takePhotoBtn = findViewById(R.id.takePhotoBtn);
        imagePreview = findViewById(R.id.photoPreview);
        scannedTextView = findViewById(R.id.scannedTextView);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                /*Bitmap bitmap = getImageFromActivityResult(o);
                showImage(bitmap);
                String text = readPhotoText(bitmap);
                scannedTextView.setText(text);

                try {
                    saveBitmapToGallery(bitmap);

                    Toast.makeText(MainActivity.this, "Image saved to gallery", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(o.getResultCode() == Activity.RESULT_OK) {
                    try {
                        Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), imageUri);
                        imagePreview.setImageBitmap(thumbnail);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }*/

            }
        });


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA
            }, REQUEST_CAMERA_CODE);
        }

        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhotoActivity();
            }
        });

        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = imagePreview.getDrawingCache();
                String text = readPhotoText(bitmap);
                copyToClipboard(text);
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
        /*ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Audio.Media.TITLE, "Recent Picture");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "?");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        OutputStream outputStream = getContentResolver().openOutputStream(uri);

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        outputStream.flush();
        outputStream.close();*/

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        Uri imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        activityResultLauncher.launch(intent);
    }

    private String readPhotoText(Bitmap bitmap) {
        TextRecognizer recognizer = new TextRecognizer.Builder(this).build();
        if (!recognizer.isOperational()) {
            Toast.makeText(this, "Error occured", Toast.LENGTH_SHORT);
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> array = recognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < array.size(); i++) {
                TextBlock textBlock = array.valueAt(i);
                stringBuilder.append(textBlock.getValue());
                stringBuilder.append('\n');
            }
            return stringBuilder.toString();
        }
        return "Error";
    }

    private void copyToClipboard(String string) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", string);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
    }
}