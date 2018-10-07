package com.example.welcome.aroundme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.midi.MidiDevice;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //view and viewGroup
    private ImageButton imageButton;
    private Button buttonUpload;
    private EditText editText;
    private ImageView imageView;
    private ListView listView;


    //firebase variable
    FirebaseVisionImage firebaseVisionImage;
    FirebaseVisionLabelDetector detector;





    //GloabalVariable
    Uri resultUri=null;
    Bitmap bitmap=null;
    List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(MainActivity.this);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri= result.getUri();
                imageView.setImageURI(resultUri);
                bitmap=decodeUriToBitmap(getApplicationContext(),resultUri);
                imageProcessing();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    private void imageProcessing() {
        if(bitmap!=null) {
            firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
            detector= FirebaseVision.getInstance().getVisionLabelDetector();
            Task<List<FirebaseVisionLabel>> result =
                    detector.detectInImage(firebaseVisionImage)
                            .addOnSuccessListener(
                                    new OnSuccessListener<List<FirebaseVisionLabel>>() {
                                        @Override
                                        public void onSuccess(List<FirebaseVisionLabel> labels) {
                                            readlable(labels);
                                                                                     }
                                    })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Task failed with an exception
                                            // ...
                                        }
                                    });

        }
        else{

        }

    }

    private void readlable(List<FirebaseVisionLabel> labels) {
        list=new ArrayList<>();
        for (FirebaseVisionLabel i:labels){
          list.add(i.getLabel());

        }
        ArrayAdapter adapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_dropdown_item_1line,list);
        listView.setAdapter(adapter);

    }



    public static Bitmap decodeUriToBitmap(Context mContext, Uri sendUri) {
        Bitmap getBitmap = null;
        try {
            InputStream image_stream;
            try {
                image_stream = mContext.getContentResolver().openInputStream(sendUri);
                getBitmap = BitmapFactory.decodeStream(image_stream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getBitmap;
    }

    private void init() {
        buttonUpload=findViewById(R.id.main_button_upload);
        imageView=findViewById(R.id.Main_ImageView_UploadImage);
        imageButton=findViewById(R.id.main_imagebutton_submit);
        listView=findViewById(R.id.main_listview);


    }
}
