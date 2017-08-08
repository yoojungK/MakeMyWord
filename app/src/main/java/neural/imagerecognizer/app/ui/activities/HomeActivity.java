package neural.imagerecognizer.app.ui.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import neural.imagerecognizer.app.R;

/**
 * Created by lab on 2017-07-28.
 */

public class HomeActivity extends Activity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);


        Button startbtn = (Button) findViewById(R.id.startbtn);
        startbtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectActivity.class);
                startActivity(intent);
            }
        });

        Button vocabtn = (Button) findViewById(R.id.vocabtn);
        vocabtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WordActivity.class);
                startActivity(intent);
            }
        });

        Button helpbtn = (Button) findViewById(R.id.helpbtn);
        helpbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Dialog dDialog = new Dialog(HomeActivity.this);
                dDialog.setContentView(R.layout.doum_layout);
                dDialog.setTitle("도움말");

                Button cancel = (Button) dDialog.findViewById(R.id.closeicon);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        dDialog.dismiss();
                    }
                });
                dDialog.show();
            }
        });


    }
}