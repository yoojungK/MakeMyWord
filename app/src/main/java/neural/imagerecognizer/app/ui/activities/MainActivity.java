package neural.imagerecognizer.app.ui.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.desmond.squarecamera.CameraActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import neural.imagerecognizer.app.R;
import neural.imagerecognizer.app.RecognitionApp;
import neural.imagerecognizer.app.nn.NNManager;
import neural.imagerecognizer.app.ui.views.PaintView;
import neural.imagerecognizer.app.ui.views.WhatisButton;
import neural.imagerecognizer.app.util.TTS;
import neural.imagerecognizer.app.util.Tool;

import static neural.imagerecognizer.app.R.id.engtext;

public class MainActivity extends BaseActivity {



    public String ans[];

    private Intent i;
    private final int GOOGLE_STT = 1000;
    private NaverTTSTask mNaverTTSTask;
    private String[] mTextString;

    private ArrayList<String> mResult;
    private String mSelectedString;
    EditText object;

    private NaverTranslateTask mNaverTranslateTask;
    public TextView bbobject;

    @Bind(R.id.btnWhatis)
    WhatisButton btnWhatis;

    @Bind(R.id.paintView)
    PaintView paintView;

    @Nullable
    private Bitmap recognBitmap;

    public RecognitionApp g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        g = (RecognitionApp) getApplication();

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Intent intent = getIntent();
        String key = intent.getStringExtra("key");
        //Toast.makeText(getApplicationContext(),key, Toast.LENGTH_LONG).show();
        if (key.equals("cam")) {
            //Toast.makeText(getApplicationContext(),key, Toast.LENGTH_LONG).show();
            Intent startCustomCameraIntent = new Intent(MainActivity.this, CameraActivity.class);
            startActivityForResult(startCustomCameraIntent, new CallbackResult() {
                @Override
                public void onResult(@NonNull Intent data) {
                    setImageFromIntent(data);
                }
            });
        }
        else if (key.equals("gal")) {
            //Toast.makeText(getApplicationContext(),key, Toast.LENGTH_LONG).show();
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, new CallbackResult() {
                @Override
                public void onResult(@NonNull Intent data) {
                    setImageFromIntent(data);
                }
            });
        }
    }

    @OnClick(R.id.btnWhatis)
    public void whatisClick(View v) {

        if (paintView.isModePhoto())
            if (recognBitmap == null)
                return;

        btnWhatis.startAnimation();
        NNManager.shared().identifyImage(recognBitmap, new NNManager.Callback() {
            @Override
            public void onResult(@NonNull String description) {
                final String msg = description;
                btnWhatis.endAnimation();
                //set image description....

                final Dialog mDialog = new Dialog(MainActivity.this);
                mDialog.setContentView(R.layout.input_layout);
                mDialog.setTitle("입력 화면");

                Button ok = (Button) mDialog.findViewById(R.id.okbtn);
                Button cancel = (Button) mDialog.findViewById(R.id.closeicon);
                Button mic = (Button) mDialog.findViewById(R.id.micbtn);

                object = (EditText) mDialog.findViewById(R.id.inputtext);

                mic.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
                        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
                        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "말을 하세요.");

                        startActivityForResult(i, GOOGLE_STT);
                    }
                });
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (object.getText().toString().trim().length() > 0 ) {

                            ans = msg.split(",");

                            if (isCorrect(object.getText().toString()) == 1) {

                                //Toast.makeText(getApplicationContext(),"성공 입력" + object.getText().toString() + "정답 " + msg, Toast.LENGTH_LONG).show();
                                final Dialog aDialog = new Dialog(MainActivity.this);
                                aDialog.setContentView(R.layout.success_layout);
                                aDialog.setTitle("정답");

                                Button restart = (Button) aDialog.findViewById(R.id.rebtn);
                                Button check = (Button) aDialog.findViewById(R.id.checkbtn);
                                final TextView aobject = (TextView) aDialog.findViewById(R.id.successtext);
                                Button acancel = (Button) aDialog.findViewById(R.id.closeicon);

                                aobject.append("정답: " + object.getText().toString());

                                acancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        aDialog.dismiss();
                                    }
                                });
                                restart.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        aDialog.dismiss();
                                    }
                                });
                                check.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final Dialog checkDialog = new Dialog(MainActivity.this);
                                        checkDialog.setContentView(R.layout.check_layout);
                                        checkDialog.setTitle("정답 확인");

                                        Button sound = (Button) checkDialog.findViewById(R.id.soundbtn);
                                        Button save = (Button) checkDialog.findViewById(R.id.savebtn);
                                        Button checkcancel = (Button) checkDialog.findViewById(R.id.closeicon);
                                        final TextView aaobject = (TextView) checkDialog.findViewById(engtext);
                                        bbobject = (TextView) checkDialog.findViewById(R.id.kortext);

                                        final String engtext = object.getText().toString();

                                        NaverTranslateTask asyncTask = new NaverTranslateTask();
                                        asyncTask.execute(engtext);

                                        aaobject.append(engtext);

                                        checkcancel.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                checkDialog.dismiss();
                                            }
                                        });

                                        sound.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                String mText = aaobject.getText().toString();
                                                mTextString = new String[]{mText};

                                                mNaverTTSTask = new NaverTTSTask();
                                                mNaverTTSTask.execute(mTextString);
                                            }
                                        });

                                        save.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                 String data = engtext +"\n"+bbobject.getText().toString();
                                             //   String data= object.getText().toString(); //EditText에서 Text 얻어오기

                                                try {
                                                    //FileOutputStream 객체생성, 파일명 "data.txt", 새로운 텍스트 추가하기 모드
                                                    FileOutputStream fos=openFileOutput("data.txt", Context.MODE_APPEND);
                                                    PrintWriter writer= new PrintWriter(fos);

                                                    writer.println(data);
                                                    g.setImg(recognBitmap);

                                                    writer.close();

                                                } catch (FileNotFoundException e) {
                                                    e.printStackTrace();
                                                }

                                                Toast.makeText(getApplicationContext(),          // 현재 화면의 제어권자
                                                        "저장성공"+data, // 보여줄 메시지
                                                        Toast.LENGTH_LONG)    // 보여줄 기간 (길게, 짧게)
                                                        .show();    // 토스트를 화면에 보여주기
                                            } //save onClick
                                        });

                                        checkDialog.show();
                                    }
                                });

                                aDialog.show();
                            }
                            else {
                                //Toast.makeText(getApplicationContext(),"실패 입력" + object.getText().toString() + "정답 " + msg, Toast.LENGTH_LONG).show();
                                final Dialog bDialog = new Dialog(MainActivity.this);
                                bDialog.setContentView(R.layout.error_layout);
                                bDialog.setTitle("오답");

                                Button restart = (Button) bDialog.findViewById(R.id.rebtn);
                                Button check = (Button) bDialog.findViewById(R.id.checkbtn);
                                final TextView bobject = (TextView) bDialog.findViewById(R.id.errortext);
                                Button bcancel = (Button) bDialog.findViewById(R.id.closeicon);

                                bobject.append("오답: " + object.getText().toString());

                                bcancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        bDialog.dismiss();
                                    }
                                });
                                restart.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                         bDialog.dismiss();
                                    }
                                });
                                check.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final Dialog checkDialog = new Dialog(MainActivity.this);
                                        checkDialog.setContentView(R.layout.check_layout);
                                        checkDialog.setTitle("정답 확인");

                                        Button sound = (Button) checkDialog.findViewById(R.id.soundbtn);
                                        Button save = (Button) checkDialog.findViewById(R.id.savebtn);
                                        final TextView aaobject = (TextView) checkDialog.findViewById(engtext);
                                        bbobject = (TextView) checkDialog.findViewById(R.id.kortext);

                                        String t = ans[0];

                                        NaverTranslateTask asyncTask = new NaverTranslateTask();
                                        asyncTask.execute(t);

                                        aaobject.append(t);

                                        sound.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mTextString = new String[]{msg};

                                                mNaverTTSTask = new NaverTTSTask();
                                                mNaverTTSTask.execute(mTextString);
                                            }
                                        });

                                        save.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                //Internal Storage에 file 저장하기
                                                String data= ans[0] +"\n"+bbobject.getText().toString();//.getText().toString(); //EditText에서 Text 얻어오기

                                                try {
                                                    //FileOutputStream 객체생성, 파일명 "data.txt", 새로운 텍스트 추가하기 모드
                                                    FileOutputStream fos=openFileOutput("data.txt", Context.MODE_APPEND);
                                                    PrintWriter writer= new PrintWriter(fos);

                                                    writer.println(data);
                                                    g.setImg(recognBitmap);

                                                    writer.close();
                                                } catch (FileNotFoundException e) {
                                                    e.printStackTrace();
                                                }
                                                Toast.makeText(getApplicationContext(),          // 현재 화면의 제어권자
                                                        "저장성공"+data, // 보여줄 메시지
                                                        Toast.LENGTH_LONG)    // 보여줄 기간 (길게, 짧게)
                                                        .show();    // 토스트를 화면에 보여주기  //
                                            }
                                        });
                                        checkDialog.show();
                                    }
                                });

                                bDialog.show();
                            }
                            //    ToastImageDescription.show(MainActivity.this, msg);

                            mDialog.dismiss();

                        } else {
                            //Toast.makeText(getApplicationContext(),"다시 입력하시오", Toast.LENGTH_LONG).show();
                            final Dialog cDialog = new Dialog(MainActivity.this);
                            cDialog.setContentView(R.layout.null_layout);
                            cDialog.setTitle("공란");

                            Button c_cancel = (Button) cDialog.findViewById(R.id.closeicon);

                            c_cancel.setOnClickListener(new View.OnClickListener() {
                                @Override public void onClick(View v) {
                                    cDialog.dismiss();
                                }
                            });

                            cDialog.show();
                        }
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });

                mDialog.show();
            }
        });

    }

    @OnClick(R.id.ivGallery)
    public void selectFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, new CallbackResult() {
            @Override
            public void onResult(@NonNull Intent data) {
                setImageFromIntent(data);
            }

        });
    }

    @OnClick(R.id.ivCamera)
    public void selectFromCamera() {
        requestPermission(new PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                Intent startCustomCameraIntent = new Intent(MainActivity.this, CameraActivity.class);
                startActivityForResult(startCustomCameraIntent, new CallbackResult() {
                    @Override
                    public void onResult(@NonNull Intent data) {
                        setImageFromIntent(data);
                    }
                });
            }

            @Override
            public void onFail() {
                Tool.showToast(MainActivity.this, "Please give camera permission!");
            }

            @NonNull
            @Override
            public String getPermissionName() {
                return Manifest.permission.CAMERA;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            Tool.shareText(this, Tool.generateGooglePlayLink());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setImageFromIntent(Intent data) {
        try {
            Uri imageUri = data.getData();
            InputStream imageStream = getContentResolver().openInputStream(imageUri);

            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            this.recognBitmap = bitmap;
            paintView.setPhoto(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int isCorrect(String _input) {
        String input = _input.trim();
        input = input.toLowerCase();
        String answer;

        for(int i=0;i<ans.length;i++){
            answer = ans[i].trim();
            answer = answer.toLowerCase();
            if(answer.equals(input))
                return 1;
        }
        return 0;
    }

    private class NaverTTSTask extends AsyncTask<String[], Void, String> {
        @Override
        protected String doInBackground(String[]... strings) {
            TTS.main(mTextString);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if( resultCode == RESULT_OK  && (requestCode == GOOGLE_STT) ){
            showSelectDialog(requestCode, data);
        }
        else{
            String msg = null;

            switch(resultCode){
                case SpeechRecognizer.ERROR_AUDIO:
                    msg = "ERROR_AUDIO";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    msg = "ERROR_CLIENT";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    msg = "ERROR_INSUFFICIENT_PERMISSIONS";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    msg = "ERROR_NETWORK";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    msg = "ERROR_NO_MATCH";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    msg = "ERROR_RECOGNIZER_BUSY";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    msg = "ERROR_SERVER";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    msg = "ERROR_SPEECH_TIMEOUT";
                    break;
            }

            if(msg != null)
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }
    private void showSelectDialog(int requestCode, Intent data){
        String key = "";
        if(requestCode == GOOGLE_STT)
            key = RecognizerIntent.EXTRA_RESULTS;

        mResult = data.getStringArrayListExtra(key);
        String[] result = new String[mResult.size()];
        mResult.toArray(result);

        AlertDialog ad = new AlertDialog.Builder(this).setTitle("선택하세요")
                .setSingleChoiceItems(result, -1, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        mSelectedString = mResult.get(which);
                    }
                })
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        object.setText(mSelectedString);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        object.setText("");
                        mSelectedString = null;
                    }
                }).create();
        ad.show();
    }

    private class NaverTranslateTask extends AsyncTask<String, Void, String> {

        public String resultText;

        String clientId = "j_GzNrMbhDVSUrRxRb8W";
        String clientSecret = "PtBTF78NAQ";

        String sourceLang = "en";
        String targetLang = "ko";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            String sourceText = strings[0];

            try {
                String text = URLEncoder.encode(sourceText, "UTF-8");
                String apiURL = "https://openapi.naver.com/v1/language/translate";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                // post request
                String postParams = "source="+sourceLang+"&target="+targetLang+"&text=" + text;
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode==200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                con.disconnect();
                return response.toString();

            } catch (Exception e) {
                //System.out.println(e);
                Log.d("error", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Gson gson = new GsonBuilder().create();
            JsonParser parser = new JsonParser();
            JsonElement rootObj = parser.parse(s.toString())
                    .getAsJsonObject().get("message")
                    .getAsJsonObject().get("result");
            TranslatedItem items = gson.fromJson(rootObj.toString(), TranslatedItem.class);
            bbobject.setText(items.getTranslatedText());

        }

        private class TranslatedItem {
            String translatedText;

            public String getTranslatedText() {
                return translatedText;
            }
        }
    }
}