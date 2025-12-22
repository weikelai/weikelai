package com.example.chapter03;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DBQueryActivity extends AppCompatActivity {

    private Myopenhelper myopenHelper;
    private SQLiteDatabase db;
    private TextView tvQueryResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbquery);

        tvQueryResult = findViewById(R.id.tv_query_result);

        myopenHelper = new Myopenhelper(this, "lgy.db", null, 1);
        db = myopenHelper.getReadableDatabase();

        queryAndDisplayData();
    }

    private void queryAndDisplayData() {
        Cursor cursor = db.query("person", null, null, null, null, null, null);

        StringBuilder stringBuilder = new StringBuilder();

        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex("name");
            int ageIndex = cursor.getColumnIndex("age");
            int idIndex = cursor.getColumnIndex("_id");

            do {
                if (idIndex != -1) {
                    stringBuilder.append(cursor.getInt(idIndex)).append("\t\t\t");
                }
                if (nameIndex != -1) {
                    stringBuilder.append(cursor.getString(nameIndex)).append("\t\t\t");
                }
                if (ageIndex != -1) {
                    stringBuilder.append(cursor.getInt(ageIndex));
                }
                stringBuilder.append("\n");

            } while (cursor.moveToNext());
            cursor.close();
        }

        // db.close(); // 保持打开状态或在onDestroy关闭，这里查询完不一定要立即关，视情况而定，但为了防止泄漏建议在Activity销毁时关

        if (stringBuilder.length() > 0) {
            tvQueryResult.setText(stringBuilder.toString());
        } else {
            tvQueryResult.setText("暂无数据");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();
        }
        if (myopenHelper != null) {
            myopenHelper.close();
        }
    }
}
