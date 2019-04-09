package com.rockgecko.qtests;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    static final String ACCESS_MEDIA_LOCATION = "android.permission.ACCESS_MEDIA_LOCATION";
    TextView text1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text1 = findViewById(R.id.text1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
    private void updateUI(){
        boolean hasMediaLoc = checkSelfPermission(ACCESS_MEDIA_LOCATION)== PackageManager.PERMISSION_GRANTED;
        String text = "Has ACCESS_MEDIA_LOCATION permission: " + hasMediaLoc;
        if(imgFile!=null && imgFile.exists()) try {
            ExifInterface exif = new ExifInterface(imgFile.getAbsolutePath());

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_UNDEFINED);
            if(orientation==ExifInterface.ORIENTATION_UNDEFINED){
                text+="Orientation is redacted";
            }
            else{
                for(String tag : new String[]{ExifInterface.TAG_MAKE, ExifInterface.TAG_MODEL, ExifInterface.TAG_SHUTTER_SPEED_VALUE}) {
                    text += tag + ": "+ exif.getAttribute(tag)+ "\n";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            text+="\n"+e.getMessage();
        }
        text1.setText(text);
    }

    public void requestPermission(View view) {
        requestPermissions(new String[]{ACCESS_MEDIA_LOCATION}, 102);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==102){
            Toast.makeText(this, "onRequestPermissionsResult: "+permissions[0], Toast.LENGTH_SHORT).show();
            updateUI();
        }
    }
File imgFile;
    public void takePhoto(View view) {
        if(checkSelfPermission(ACCESS_MEDIA_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "ACCESS_MEDIA_LOCATION is required", Toast.LENGTH_SHORT).show();
            //requestPermissions(new String[]{ACCESS_MEDIA_LOCATION}, 102);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dir = getExternalFilesDir(null);
        imgFile=new File(dir, "image.jpg");
        Uri uri = androidx.core.content.FileProvider.getUriForFile(this, getPackageName()+".provider",imgFile);
        if(isAtLeastQBeta1())try{
            //simply change to the below if compileSdk is Q
            // uri = MediaStore.setRequireOriginal(uri);
            uri = (Uri) MediaStore.class.getDeclaredMethod("setRequireOriginal", Uri.class).invoke(null, uri);
        }catch(Exception e){
            e.printStackTrace();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        startActivityForResult(intent, 101);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode==101 && resultCode==RESULT_OK){
            Toast.makeText(this, "Image capture success", Toast.LENGTH_SHORT).show();
            updateUI();
        }
    }
    public static boolean isAtLeastQBeta1(){
        return Build.VERSION.SDK_INT>28 || (Build.VERSION.SDK_INT==28 && Build.VERSION.PREVIEW_SDK_INT>0);
    }
}
