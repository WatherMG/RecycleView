package com.drwat.lab2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddActivity extends AppCompatActivity {
    public static final int ID_REQUEST_CODE = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_TAKE_PHOTO = 101;
    private ImageView mImageView;
    private String mCurrentPhotoPath;

    private EditText mNameEditText;
    private EditText mCountEditText;
    public static final String NAME = ".AddActivity.NAME";
    public static final String COUNT = ".AddActivity.Count";
    public static final String IMAGE_PATH = "AddActivity.ImagePath";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        mNameEditText = (EditText) findViewById(R.id.NameEditTextView);
        mCountEditText = (EditText) findViewById(R.id.CountEditTextView);
        mImageView = (ImageView) findViewById(R.id.imagePhotoThumbnail);

        Button button = (Button) findViewById(R.id.addButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mNameEditText != null ? mNameEditText.getText().toString() : null;
                String count = mCountEditText != null ? mCountEditText.getText().toString() : null;
                String image = mCurrentPhotoPath;
                Intent intent = new Intent();
                intent.putExtra(NAME, name);
                intent.putExtra(COUNT, count);
                if (mCurrentPhotoPath != null) {
                    intent.putExtra(IMAGE_PATH, image);
                }
                setResult(ID_REQUEST_CODE, intent);
                finish();
            }
        });
    }

    public void getPhoto(View view) {
        dispatchTakePictureIntent();
    }

    public void getPhotoFromGalery(View view) {
        takePictureFromGalery();
    }

    private void takePictureFromGalery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        /*if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                //photoFile = createImageFile();
            } catch (IOException ex) {
                Toast toast = Toast.makeText(this, "Ошибка при создании файла!", Toast.LENGTH_SHORT);
                toast.show();
            }
            //if (photoFile != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));*/
                startActivityForResult(Intent.createChooser(intent, "select File"), REQUEST_TAKE_PHOTO);
            //}
        //}
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast toast = Toast.makeText(this, "Ошибка при создании файла!", Toast.LENGTH_SHORT);
                toast.show();
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void setPicture() {
        int targetH = mImageView.getHeight();
        int targetW = mImageView.getWidth();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, options);
        int photoW = options.outWidth;
        int photoH = options.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);
        mImageView.setImageBitmap(bitmap);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                setPicture();
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            String selectedImagePath = mCurrentPhotoPath;

            Bitmap bm;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(selectedImagePath, options);
            final int REQUIRED_SIZE = 200;
            int scale = 1;
            while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                    && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;
            options.inSampleSize = scale;
            options.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeFile(selectedImagePath, options);
            mCurrentPhotoPath = selectedImagePath;

            mImageView.setImageBitmap(bm);
        }

        //super.onActivityResult(requestCode, resultCode, data);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        galleryAddPicture();
        return image;
    }

    private void galleryAddPicture() {
        Intent mediaScannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(file);
        mediaScannerIntent.setData(contentUri);
        this.sendBroadcast(mediaScannerIntent);

    }
}

