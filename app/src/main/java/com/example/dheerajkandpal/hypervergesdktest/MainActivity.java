package com.example.dheerajkandpal.hypervergesdktest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

import co.hyperverge.hyperdocssdk.workflows.ocr.activities.CameraActivity;




public class MainActivity extends AppCompatActivity implements BaseApplication.PermissionCallInterface {
    private static final int REQ_CAMERA_IMAGE = 10;
    public BaseApplication.PermissionCallInterface mPermissionCallInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String RootDir = Environment.getExternalStorageDirectory() + File.separator + "PicturesNR2";

        File RootFile = new File(RootDir);


        RootFile.mkdir();
        mPermissionCallInterface = this;
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(RunTimePermissionUtility.doWeHaveCameraPermission(MainActivity.this)) {

                    CameraActivity.startForPassport(getBaseContext(), RootDir+"/passport_front.jpg", RootDir+"/passport_back.jpg", CameraActivity.DocumentSide.FRONT_BACK, new CameraActivity.ImageListener() {
                        @Override
                        public void onOCRComplete(JSONObject result) {

                            Toast.makeText(getBaseContext(),result+"",Toast.LENGTH_LONG).show();
                            ((TextView)findViewById(R.id.textView2)).setText(result+"");
                            Log.d("result",result+"");
                            //result JSONObject will have the detected text and the other information needed. We have documented each key in the object below
                            //Implement relevant logic from the info detected by our OCR here
                        }
                    });

//                    CameraActivity.startForPan(getBaseContext(), "/storage/emulated/0/DCIM/Camera/IMG_20171121_203524268.jpg", new CameraActivity.ImageListener() {
//                        @Override
//                        public void onOCRComplete(JSONObject result) {
//                            //result JSONObject will have the detected text and the other information needed. We have documented each key in the object below
//                            //Implement relevant logic from the info detected by our OCR here
//                            Toast.makeText(getBaseContext(),result+"",Toast.LENGTH_LONG).show();
//                        }
//                    });


//                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//                    startActivityForResult(intent, REQ_CAMERA_IMAGE);

                } else {
                    RunTimePermissionUtility.requestWriteCameraPermission(MainActivity.this, 45);
                }


            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                                    CameraActivity.startForPan(getBaseContext(), RootDir+"/pan_card.jpg", new CameraActivity.ImageListener() {
                        @Override
                        public void onOCRComplete(JSONObject result) {
                            //result JSONObject will have the detected text and the other information needed. We have documented each key in the object below
                            //Implement relevant logic from the info detected by our OCR here
                            Toast.makeText(getBaseContext(),result+"",Toast.LENGTH_LONG).show();
                            ((TextView)findViewById(R.id.textView2)).setText(result+"");
                            try {
                                ((TextView)findViewById(R.id.tv_name)).setText(((TextView)findViewById(R.id.tv_name)).getText()+result.getString("name"));
                                ((TextView)findViewById(R.id.tv_father)).setText(((TextView)findViewById(R.id.tv_father)).getText()+result.getString("father"));
                                ((TextView)findViewById(R.id.tv_pan_no)).setText(((TextView)findViewById(R.id.tv_pan_no)).getText()+result.getString("pan_no"));
                                ((TextView)findViewById(R.id.tv_from_url)).setText( ((TextView)findViewById(R.id.tv_from_url)).getText()+result.getString("front_url"));
                                ((TextView)findViewById(R.id.tv_date)).setText(((TextView)findViewById(R.id.tv_date)).getText()+result.getString("date"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });


            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CAMERA_IMAGE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ((ImageView)findViewById(R.id.imageView)).setImageBitmap(photo);

            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
            Uri tempUri = getImageUri(getApplicationContext(), photo);

            // CALL THIS METHOD TO GET THE ACTUAL PATH
            File finalFile = new File(getRealPathFromURI(tempUri));

            System.out.println(finalFile);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.e("main activity", "requestCode : " + requestCode + ", permissions :" + permissions + ",grantResults : " + grantResults);
        switch (requestCode){
            case 45:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Permission", "WRITE_EXTERNAL permission has now been granted. Showing result.");
                    //registerEvent();
                    mPermissionCallInterface.permissionSuccessCallback();
                } else {
                    Log.e("Permission", "WRITE_EXTERNAL permission was NOT granted.");
                    RunTimePermissionUtility.showReasonBoxForCameraPermission(this, 45);
                }
                break;


        }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void permissionSuccessCallback() {
//        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//        startActivity(intent);

//        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//        startActivityForResult(intent, REQ_CAMERA_IMAGE);
    }



}
