package neural.imagerecognizer.app.ui.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;

import neural.imagerecognizer.app.R;
import neural.imagerecognizer.app.util.Tool;

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
                        final Dialog camDialog = new Dialog(SelectActivity.this);
                        camDialog.setContentView(R.layout.camera_layout);
                        camDialog.setTitle("카메라 경고");
                        Button cam_cancel = (Button) camDialog.findViewById(R.id.closeicon);

                        cam_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override public void onClick(View v) {
                                camDialog.dismiss();
                            }
                        });
                        camDialog.show();
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

