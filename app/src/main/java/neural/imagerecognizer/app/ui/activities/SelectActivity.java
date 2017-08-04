package neural.imagerecognizer.app.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.desmond.squarecamera.CameraActivity;

import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.Bind;
import butterknife.OnClick;
import neural.imagerecognizer.app.R;
import neural.imagerecognizer.app.ui.views.PaintView;
import neural.imagerecognizer.app.util.Tool;
import android.app.Activity;

public class SelectActivity extends BaseActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_layout);
        Button cam = (Button) findViewById(R.id.cambtn);
        Button gal = (Button) findViewById(R.id.galbtn);
        cam.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                requestPermission(new PermissionCallback() {
                    @Override
                    public void onPermissionGranted() {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("key", "cam");
                        startActivity(intent);
                    }

                    @Override
                    public void onFail() {
                        Tool.showToast(SelectActivity.this, "Please give camera permission!");
                    }

                    @NonNull
                    @Override
                    public String getPermissionName() {
                        return Manifest.permission.CAMERA;
                    }
                });
            }
        });
        gal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("key", "gal");
                startActivity(intent);
            }
        });
    }
}