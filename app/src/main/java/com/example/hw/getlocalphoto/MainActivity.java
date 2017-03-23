package com.example.hw.getlocalphoto;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.hw.getlocalphoto.view.DragImageView;

import java.io.IOException;

/**
 * 从本地获取一张图，并显示
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String INTENT_TYPE = "image/*";
    private int REQUESTCODE = 100;

    private DragImageView imageView;
    private Button button, button2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindView();
    }

    private void bindView() {
        button = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button.setOnClickListener(this);
        button2.setOnClickListener(this);
        imageView = (DragImageView) findViewById(R.id.imageView1);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                //使用intent调用系统提供的相册功能，
                //使用startActivityForResult是为了获取用户选择的图片
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(INTENT_TYPE);
                startActivityForResult(intent, REQUESTCODE);
                break;
            case R.id.button2:
                startActivity(new Intent(MainActivity.this, PhotoListActivity.class));
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Log.e("TAG--->onresult", "ActivityResult resultCode error");
            return;
        }

        //获得图片
        Bitmap bitmap = null;
        ContentResolver resolver = getContentResolver();
        if (requestCode == REQUESTCODE) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(resolver, uri);//获得图片
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imageView.setImageBitmap(bitmap);


        //获得路径
        if (requestCode == REQUESTCODE) {
            Uri uri = data.getData();
            uri = geturi(data);//解决方案
            String[] pro = {MediaStore.Images.Media.DATA};
            //好像是android多媒体数据库的封装接口，具体的看Android文档
            Cursor cursor = managedQuery(uri, pro, null, null, null);
            Cursor cursor1 = getContentResolver().query(uri, pro, null, null, null);
            //拿到引索
            int index = cursor1.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            //移动到光标开头
            cursor.moveToFirst();
            //最后根据索引值获取图片路径
            String path = cursor.getString(index);
            Log.d("Tag--->path", path);

        }
    }

    public Uri geturi(android.content.Intent intent) {
        Uri uri = intent.getData();
        String type = intent.getType();
        if (uri.getScheme().equals("file") && (type.contains("image/"))) {
            String path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = this.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=")
                        .append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.ImageColumns._ID},
                        buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    // set _id value
                    index = cur.getInt(index);
                }
                if (index == 0) {
                    // do nothing
                } else {
                    Uri uri_temp = Uri
                            .parse("content://media/external/images/media/"
                                    + index);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }
        return uri;
    }
}
