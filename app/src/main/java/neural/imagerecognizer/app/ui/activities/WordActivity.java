package neural.imagerecognizer.app.ui.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;

import neural.imagerecognizer.app.R;
import neural.imagerecognizer.app.ui.views.ListViewAdapter;
import neural.imagerecognizer.app.ui.views.ListViewItem;

/**
 * Created by lab on 2017-08-02.
 */

public class WordActivity extends AppCompatActivity {

    ArrayList<String> LIST_MENU = new ArrayList<String>();
    ListViewItem items ;
    ArrayList<String> TEMPLIST = new ArrayList<String>();

    @Nullable
    private Bitmap recognBitmap;

    String sdPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_layout);

        StringBuffer buffer= new StringBuffer();

        final ArrayList<String> items = new ArrayList<String>() ;
        final ListView listview ;
        final ListViewAdapter adapter;

        adapter = new ListViewAdapter() ;

        listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(adapter);

        try {
            FileInputStream fis=openFileInput("data.txt");
            BufferedReader reader= new BufferedReader(new InputStreamReader(fis));

            String str = reader.readLine();
            String str2 = reader.readLine();
            String str3 = reader.readLine();
            String word;
            String mean;
            String filename;
            String temp;

            int i =0;
            int wordindex;
            int meanindex;

            while(str!=null){
                LIST_MENU.add(str+"\n"+str2 + "\n" + str3);
                buffer.append(str+"\n");

                str = reader.readLine();
                str2 = reader.readLine();
                str3 = reader.readLine();
            }

            while(LIST_MENU.get(i) != null){
                temp = LIST_MENU.get(i);
                wordindex = temp.indexOf("\n");
                meanindex = temp.indexOf("\n", wordindex + 1);
                word = temp.substring(0, wordindex);
                mean = temp.substring(wordindex + 1, meanindex);
                filename = temp.substring(meanindex + 1);

                File imgFile = new File(filename);
                Drawable d;

                if(imgFile.exists()){
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    d = new BitmapDrawable(getResources(), myBitmap);
                }
                else {
                    d = null;
                }
                adapter.addItem(d, word, mean);
                i++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Button deleteButton = (Button)findViewById(R.id.delete) ;
        deleteButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                SparseBooleanArray checkedItems = listview.getCheckedItemPositions();
                int count = adapter.getCount() ;

                for (int i = count-1; i >= 0; i--) {
                    if (checkedItems.get(i)) {
                        LIST_MENU.remove(i) ;
                        adapter.removeItem(i);
                    }
                }

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

        FileWriter fw = null;
        BufferedWriter bufwr = null;

        try {
            fw = new FileWriter(file) ;
            bufwr = new BufferedWriter(fw) ;

            for (String str : LIST_MENU) {
                bufwr.write(str) ;
                bufwr.newLine() ;
            }
            bufwr.flush() ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        try {
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
