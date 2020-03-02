package com.dev.nunua.Users;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.dev.nunua.Model.Users;
import com.dev.nunua.Prevalent.Prevalent;
import com.dev.nunua.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.controls.Preview;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;
import com.otaliastudios.cameraview.size.Size;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ScanRecycleActivity extends AppCompatActivity {
    CameraView cameraView;
    boolean isDetected = false;
    Button scan_btn;


    FirebaseVisionBarcodeDetectorOptions options;
    FirebaseVisionBarcodeDetector detector;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
    public static final  String TAG = "Barcode scan";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_recycle);

        //ask for permission when activity starts for the first time
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        setupCamera();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();


    }


    private void setupCamera(){
        scan_btn =  findViewById(R.id.scan_recycle_btn);
        scan_btn.setEnabled(isDetected);
        scan_btn.setOnClickListener(view -> isDetected = !isDetected);

        cameraView = findViewById(R.id.recycle_cameraview);
        cameraView.setLifecycleOwner(this);
        cameraView.addFrameProcessor(frame -> processImage(getVisionImageFromFrame(frame)));


        options = new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE)
                .build();
        detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);


    }

    public void stop(){
        try {
            detector.close();
        } catch (IOException e)
        {
            Log.e(TAG,"Exception thrown while closing detector"+e);
        }
    }

    private FirebaseVisionImage getVisionImageFromFrame(Frame frame) {
       final byte[] data =frame.getData();
        FirebaseVisionImageMetadata metadata = new FirebaseVisionImageMetadata.Builder()
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                .setHeight(frame.getSize().getHeight())
                .setWidth(frame.getSize().getWidth())
                .build();

        return FirebaseVisionImage.fromByteArray(data, metadata);

    }

    private void processImage(FirebaseVisionImage image) {
        if (!isDetected){
            detector.detectInImage(image)
                    .addOnSuccessListener(firebaseVisionBarcodes -> {processResult(firebaseVisionBarcodes); stop();})
                    .addOnFailureListener(e -> Toast.makeText(ScanRecycleActivity.this, "" +e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }





   private void processResult(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
        if(firebaseVisionBarcodes.size() > 0)
        {

            cameraView.close();
            isDetected = true;
            scan_btn.setEnabled(isDetected);
            for (FirebaseVisionBarcode item: firebaseVisionBarcodes)
            {
                int value_type = item.getValueType();
                switch (value_type)
                {
                    case FirebaseVisionBarcode.TYPE_TEXT:
                    {

                        createDialog(item.getRawValue());


                        long points = Integer.parseInt(item.getRawValue());
                        DatabaseReference reference = ref.child(Prevalent.currentOnlineUsers.getPhone());

                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                Users users = dataSnapshot.getValue(Users.class);

                                    assert users != null;
                                    long storedPoints = users.getPoints();

                                long totalPoints = (points + storedPoints);

                                reference.child("points").setValue(totalPoints);


                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                    break;

                    default:
                        break;
                }
            }
        }
    }


    private void createDialog(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You have received "+text+ " recycle points. Thank you for Keeping the environment Clean ")
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    ScanRecycleActivity.this.finish();
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}

