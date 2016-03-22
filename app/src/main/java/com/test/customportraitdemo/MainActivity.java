package com.test.customportraitdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.camera.CropImageIntentBuilder;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_CONTENT = 0;
    public static final int REQUEST_CODE_CAPTURE_CAMEIA = 1;
    private Button mButton, mButton2;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = (Button) findViewById(R.id.btton_id);
        mButton2 = (Button) findViewById(R.id.btton2_id);
        mImageView = (ImageView) findViewById(R.id.image);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_CONTENT);
            }
        });
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(getImageByCamera, REQUEST_CODE_CAPTURE_CAMEIA);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        File croppedImageFile = new File(getFilesDir(), "test.jpg");
        File bufferFile = new File(getFilesDir(), "buffer.jpg");
        if(requestCode == REQUEST_CODE_CONTENT && data != null){
            Uri uri = data.getData();
            Log.d("a",uri.toString());
            if(uri!=null){
                Log.d("file",croppedImageFile.getPath());
                CropImageIntentBuilder cropImage = getCropImage(uri, croppedImageFile);
                startActivityForResult(cropImage.getIntent(this), 2);
            }
        }else if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA && data != null){
            Uri uri = data.getData();
            if(uri == null){
                //use bundle to get data
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    Bitmap  photo = (Bitmap) bundle.get("data"); //get bitmap
                    //spath :生成图片取个名字和路径包含类型
//                    saveImage(Bitmap photo, String spath);
                    Uri croppedImage = Uri.fromFile(bufferFile);
                    //将图片保存到指定位置
                    MyUtils.saveBitmapToFile(getContentResolver(), photo, croppedImage);
                    CropImageIntentBuilder cropImage = getCropImage(croppedImage, croppedImageFile);
                    startActivityForResult(cropImage.getIntent(this), 2);
                } else {
                    Toast.makeText(getApplicationContext(), "err****", Toast.LENGTH_LONG).show();
                    return;
                }
            }else{
                //to do find the path of pic by uri
            }
        }

        if(requestCode == 2) {
            mImageView.setImageBitmap(MyUtils.toRoundBitmap(BitmapFactory.decodeFile(croppedImageFile.getAbsolutePath())));
        }
    }

    @NonNull
    private CropImageIntentBuilder getCropImage(Uri uri, File croppedImageFile) {
        Uri croppedImage = Uri.fromFile(croppedImageFile);
        CropImageIntentBuilder cropImage = new CropImageIntentBuilder(200, 200, croppedImage);
        cropImage.setOutlineColor(0xFF03A9F4);
        cropImage.setSourceImage(uri);
        return cropImage;
    }

}
