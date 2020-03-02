package com.dev.nunua.Users;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.dev.nunua.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.Frame;
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

import java.util.List;

public class ScanBarcodeActivity extends AppCompatActivity {
    CameraView cameraView;
    boolean isDetected = false;
    Button scan_btn;

    FirebaseVisionBarcodeDetectorOptions options;
    FirebaseVisionBarcodeDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);

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
        scan_btn =  findViewById(R.id.scan_btn);
        scan_btn.setEnabled(isDetected);
        scan_btn.setOnClickListener(view -> isDetected = !isDetected);

        cameraView = findViewById(R.id.cameraview);
        cameraView.setLifecycleOwner(this);
        cameraView.addFrameProcessor(frame -> processImage(getVisionImageFromFrame(frame)));

        options = new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE)
                .build();
        detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);
    }

    private FirebaseVisionImage getVisionImageFromFrame(com.otaliastudios.cameraview.frame.Frame frame) {
        byte[] data =frame.getData();
        FirebaseVisionImageMetadata metadata = new FirebaseVisionImageMetadata.Builder()
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                .setHeight(frame.getSize().getHeight())
                .setWidth(frame.getSize().getWidth())
                //.setRotation(frame.getRotation())  //for landcape mode
                .build();

        return FirebaseVisionImage.fromByteArray(data, metadata);

    }

    private void processImage(FirebaseVisionImage image) {
        if (!isDetected){
            detector.detectInImage(image)
                    .addOnSuccessListener(firebaseVisionBarcodes -> processResult(firebaseVisionBarcodes))
                    .addOnFailureListener(e -> Toast.makeText(ScanBarcodeActivity.this, "" +e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
    private void processResult(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
        if(firebaseVisionBarcodes.size() > 0)
        {
            isDetected = true;
            scan_btn.setEnabled(isDetected);
            for (FirebaseVisionBarcode item: firebaseVisionBarcodes)
            {
                int value_type = item.getValueType();
                switch (value_type)
                {
                    case FirebaseVisionBarcode.TYPE_TEXT:
                    {

                        Intent intent = new Intent(this,ScannedProductActivity.class);
                        intent.putExtra("product_id",item.getRawValue());
                        startActivity(intent);

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
        builder.setMessage(text)
                .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
