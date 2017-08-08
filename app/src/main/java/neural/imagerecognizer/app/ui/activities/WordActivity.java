package neural.imagerecognizer.app.ui.activities;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;

import neural.imagerecognizer.app.R;
import neural.imagerecognizer.app.RecognitionApp;
import neural.imagerecognizer.app.ui.views.ListViewAdapter;
import neural.imagerecognizer.app.ui.views.ListViewItem;

/**
 * Created by lab on 2017-08-02.
 */

public class WordActivity extends AppCompatActivity {

  //  static final String[] LIST_MENU = new String[3] ;

    ArrayList<String> LIST_MENU = new ArrayList<String>();
    ListViewItem items ;
    ArrayList<String> TEMPLIST = new ArrayList<String>();
  // private ArrayList<ListViewItem> newlistViewItemList = new ArrayList<ListViewItem>() ;
  //  String check []= new String[5];
   // TextView text;

    @Nullable
    private Bitmap recognBitmap;
    public RecognitionApp g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_layout);
       // text = (TextView)findViewById(R.id.text);

        //LIST_MENU.add("apple\n사과");

        g = (RecognitionApp) getApplication();

        StringBuffer buffer= new StringBuffer();

        final ArrayList<String> items = new ArrayList<String>() ;
        final ListView listview ;
        final ListViewAdapter adapter;

        // Adapter 생성
        adapter = new ListViewAdapter() ;

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(adapter);

        try {
            //FileInputStream 객체생성, 파일명 "data.txt"
            FileInputStream fis=openFileInput("data.txt");
            BufferedReader reader= new BufferedReader(new InputStreamReader(fis));

            String str = reader.readLine();//한 줄씩 읽어오기
            String str2 = reader.readLine();
            String word;
            String mean;
            String temp;

            int i =0;
            int wordindex;

            while(str!=null){
                LIST_MENU.add(str+"\n"+str2);
                buffer.append(str+"\n");

                str=reader.readLine();
                str2=reader.readLine();
            }

            while(LIST_MENU.get(i) != null){

                temp = LIST_MENU.get(i);
                wordindex= temp.indexOf("\n");
                word = temp.substring(0, wordindex);
                mean = temp.substring(wordindex+1);

        //        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.abclogo),word,mean) ;
                recognBitmap = g.getImg(i);
                Drawable d = new BitmapDrawable(getResources(), recognBitmap);
                adapter.addItem(d, word,mean);

                i++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Toast.makeText(this,          // 현재 화면의 제어권자
                "load 성공성공:\n"+ buffer.toString(), // 보여줄 메시지
                Toast.LENGTH_LONG)    // 보여줄 기간 (길게, 짧게)
                .show();    // 토스트를 화면에 보여주기  //
/*
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, LIST_MENU) ;

        ListView listview = (ListView) findViewById(R.id.listview1) ;
        listview.setAdapter(adapter) ;

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {

                // get TextView's Text.
                String strText = (String) parent.getItemAtPosition(position) ;

                // TODO : use strText

            }
        }) ;
*/
        Button deleteButton = (Button)findViewById(R.id.delete) ;
        deleteButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                SparseBooleanArray checkedItems = listview.getCheckedItemPositions();
                int count = adapter.getCount() ;
/*
                for(int i=0; i<6;i++) {
                    System.out.println(LIST_MENU.get(i));
                }
                System.out.println("\n\n");
*/
                for (int i = count-1; i >= 0; i--) {
                    if (checkedItems.get(i)) {
                        g.removeImg(i);
                        LIST_MENU.remove(i) ;
                        adapter.removeItem(i);
                    }
                }

                // 모든 선택 상태 초기화.
                listview.clearChoices() ;
                adapter.notifyDataSetChanged();
                saveItemsToFile();
            }
        }) ;


        Button selectAllButton = (Button)findViewById(R.id.selectAll) ;
        selectAllButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                int count = 0 ;
                count = adapter.getCount() ;

                for (int i=0; i<count; i++) {
                    listview.setItemChecked(i, true) ;
                }
            }
        }) ;
    }

    private void saveItemsToFile() {
        File file = new File(getFilesDir(), "data.txt") ;

        FileWriter fw = null ;
        BufferedWriter bufwr = null ;

        Toast.makeText(this,          // 현재 화면의 제어권자
                "count:\n"+ g.count, // 보여줄 메시지
                Toast.LENGTH_LONG)    // 보여줄 기간 (길게, 짧게)
                .show();    // 토스트를 화면에 보여주기

        int c = 0;
        try {
            for(int i=0;i<g.count;i++){
                if (g.getImg(i) == null) {
                    for(int j=i;j<g.count;j++) {
                        g.setImage(j, g.getImg(j+1));
                    }
                }
            }
        }
        catch(Exception e) {}
        try {
            // open file.
            fw = new FileWriter(file) ;
            bufwr = new BufferedWriter(fw) ;

            for (String str : LIST_MENU) {
                bufwr.write(str) ;
                bufwr.newLine() ;
            }
            // write data to the file.
            bufwr.flush() ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        try {
            // close file.
            if (bufwr != null) {
                bufwr.close();
            }
            if (fw != null) {
                fw.close();
            }
        } catch (Exception e) {
            e.printStackTrace() ;
        }
    }
}
