package com.example.bhagavathi_camera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BurstCameraActivity extends Activity implements TextureView.SurfaceTextureListener {

    private static final int REQUEST_CAMERA_PERMISSION = 1001;
    private Camera camera;
    private TextureView textureView;
    private Button captureButton;
    private int photoCount = 0;
    private final List<String> photoPaths = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            setupCameraView();
        }
    }

    private void setupCameraView() {
        FrameLayout layout = new FrameLayout(this);

        textureView = new TextureView(this);
        textureView.setSurfaceTextureListener(this);

        captureButton = new Button(this);
        captureButton.setText("Capture (0/5)");
        captureButton.setBackgroundColor(0xAA000000); // semi-transparent black
        captureButton.setTextColor(0xFFFFFFFF); // white

        FrameLayout.LayoutParams btnParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        btnParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        btnParams.setMargins(0, 0, 0, 60);

        layout.addView(textureView);
        layout.addView(captureButton, btnParams);

        setContentView(layout);

        captureButton.setOnClickListener(v -> takeOnePhoto());
    }

    private void takeOnePhoto() {
        if (photoCount >= 5) {
            Toast.makeText(this, "Already captured 5 photos", Toast.LENGTH_SHORT).show();
            return;
        }

        camera.takePicture(null, null, (data, cam) -> {
            try {
                File dir = new File(getExternalFilesDir(null), "bhagavathi");
                if (!dir.exists()) dir.mkdirs();

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
                File file = new File(dir, "IMG_" + timeStamp + "_" + photoCount + ".jpg");

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(data);
                fos.close();

                photoPaths.add(file.getAbsolutePath());
                photoCount++;

                cam.startPreview(); // restart preview for next shot
                captureButton.setText("Capture (" + photoCount + "/5)");

                if (photoCount == 5) {
                    Toast.makeText(this, "Captured 5 photos", Toast.LENGTH_SHORT).show();
                    finishWithResult();
                }
            } catch (Exception e) {
                e.printStackTrace();
                finishWithResult();
            }
        });
    }

    private void finishWithResult() {
        Intent resultIntent = new Intent();
        resultIntent.putStringArrayListExtra("paths", new ArrayList<>(photoPaths));
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupCameraView();
            } else {
                Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        try {
            camera = Camera.open();
            camera.setPreviewTexture(surface);
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
            finishWithResult();
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        return true;
    }

    @Override public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {}
    @Override public void onSurfaceTextureUpdated(SurfaceTexture surface) {}
}
