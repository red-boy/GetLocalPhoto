package com.example.hw.getlocalphoto;

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhotoListActivity extends AppCompatActivity {
    private Button look;
    private Button add;
    ListView show_list;
    ArrayList names = null;
    ArrayList descs = null;
    ArrayList fileNames = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);

        look = (Button) findViewById(R.id.look);
        add = (Button) findViewById(R.id.add);
        show_list = (ListView) findViewById(R.id.show_list);
        look.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                names = new ArrayList();
                descs = new ArrayList();
                fileNames = new ArrayList();
                Cursor cursor = getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
                while (cursor.moveToNext()) {
                    //获取图片的名称
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                    //获取图片的生成日期
                    byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    //获取图片的详细信息
                    String desc = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DESCRIPTION));
                    names.add(name);
                    descs.add(desc);
                    fileNames.add(new String(data, 0, data.length - 1));
                }
                List<Map<String, Object>> listItems = new ArrayList<>();
                for (int i = 0; i < names.size(); i++) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", names.get(i));
                    map.put("desc", descs.get(i));
                    listItems.add(map);
                }
                //设置adapter
                SimpleAdapter adapter = new SimpleAdapter(PhotoListActivity.this, listItems,
                        R.layout.line, new String[]{"name", "desc"}, new int[]{R.id.name, R.id.desc});

                show_list.setAdapter(adapter);
            }
        });

        ///list的点击事件
        show_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                View viewDiag = getLayoutInflater().inflate(R.layout.view, null);
                ImageView image = (ImageView) viewDiag.findViewById(R.id.image);
                image.setImageBitmap(BitmapFactory.decodeFile((String) fileNames.get(i)));
                new AlertDialog.Builder(PhotoListActivity.this).setView(viewDiag)
                        .setPositiveButton("确定", null).show();
            }
        });
    }
}
