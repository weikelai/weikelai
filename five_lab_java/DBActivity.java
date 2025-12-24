package com.example.chapter03;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class DBActivity extends AppCompatActivity {
    Myopenhelper myopenhelper;
    SQLiteDatabase database;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_db);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. 初始化数据库帮助类和数据库
        myopenhelper = new Myopenhelper(this, "lgy.db", null, 1);
        database = myopenhelper.getWritableDatabase();
        // 确保表存在 (onUpgrade/onCreate在Myopenhelper中处理，但此处保留以防万一或作为冗余)
        // 实际表结构已在Myopenhelper中定义

        TextView textView4;
        Button button1, button2, button3, button4;
        final EditText exitText1, exitText2, editText3;

        button1 = findViewById(R.id.dbbutton1);
        button2 = findViewById(R.id.dbbutton2);
        button3 = findViewById(R.id.dbbutton3);
        button4 = findViewById(R.id.dbbutton4);

        exitText1 = findViewById(R.id.editText1);
        exitText2 = findViewById(R.id.editText2);
        editText3 = findViewById(R.id.editText3);
        textView4 = findViewById(R.id.dbtextView4);

        // 2. 增加数据 (Insert)
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmpty(exitText1) || isEmpty(exitText2)) {
                    Toast.makeText(DBActivity.this, "姓名和年龄不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                String name = exitText1.getText().toString().trim();
                String ageStr = exitText2.getText().toString().trim();

                try {
                    ContentValues values = new ContentValues();
                    values.put("name", name);
                    values.put("age", Integer.parseInt(ageStr));
                    long id = database.insert("person", null, values);
                    if (id != -1) {
                        Toast.makeText(DBActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                        exitText1.setText("");
                        exitText2.setText("");
                        if (editText3 != null) editText3.setText("");
                    } else {
                         Toast.makeText(DBActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(DBActivity.this, "年龄必须是数字", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(DBActivity.this, "操作失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 3. 删除数据 (Delete)
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempStr1 = exitText1.getText().toString().trim();
                String tempStr2 = exitText2.getText().toString().trim();

                final String str1 = "Name".equalsIgnoreCase(tempStr1) ? "" : tempStr1;
                final String str2 = "Age".equalsIgnoreCase(tempStr2) ? "" : tempStr2;

                if (str1.isEmpty() && str2.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "请至少输入一个条件", Toast.LENGTH_SHORT).show();
                    return;
                }

                new AlertDialog.Builder(DBActivity.this)
                    .setTitle("确认删除")
                    .setMessage("确定要删除符合条件的数据吗？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        performDelete(str1, str2, exitText1, exitText2);
                    })
                    .setNegativeButton("取消", null)
                    .show();
            }
        });

        // 4. 修改数据 (Update)
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmpty(editText3)) {
                    Toast.makeText(getApplicationContext(), "请输入要修改的新年龄", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                String tempStr1 = exitText1.getText().toString().trim();
                String tempStr2 = exitText2.getText().toString().trim();
                
                final String str1 = "Name".equalsIgnoreCase(tempStr1) ? "" : tempStr1;
                final String str2 = "Age".equalsIgnoreCase(tempStr2) ? "" : tempStr2;
                
                if (str1.isEmpty() && str2.isEmpty()) {
                     Toast.makeText(getApplicationContext(), "请至少输入一个查找条件（姓名或原年龄）", Toast.LENGTH_SHORT).show();
                     return;
                }

                new AlertDialog.Builder(DBActivity.this)
                    .setTitle("确认修改")
                    .setMessage("确定要修改符合条件的数据吗？")
                    .setPositiveButton("确定", (dialog, which) -> {
                         performUpdate(str1, str2, editText3);
                    })
                    .setNegativeButton("取消", null)
                    .show();
            }
        });

        // 5. 查询数据 (Query)
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str1 = exitText1.getText().toString().trim();
                if ("Name".equalsIgnoreCase(str1)) { str1 = ""; }
                String str2 = exitText2.getText().toString().trim();
                if ("Age".equalsIgnoreCase(str2)) { str2 = ""; }
                
                if (str1.isEmpty() && str2.isEmpty()) {
                    Toast.makeText(DBActivity.this, "未输入条件，已查询全部数据", Toast.LENGTH_SHORT).show();
                }

                StringBuilder whereClause = new StringBuilder();
                List<String> whereArgsList = new ArrayList<>();

                if (!str1.isEmpty()) {
                    whereClause.append("name=?");
                    whereArgsList.add(str1);
                }

                if (!str2.isEmpty()) {
                    if (whereClause.length() > 0) {
                        whereClause.append(" AND ");
                    }
                    whereClause.append("age=?");
                    whereArgsList.add(str2);
                }
                
                String selection = whereClause.length() > 0 ? whereClause.toString() : null;
                String[] selectionArgs = whereClause.length() > 0 ? whereArgsList.toArray(new String[0]) : null;

                Cursor cursor = database.query("person", null, selection, selectionArgs, null, null, null);
                
                textView4.setText("姓名\t\t\t年龄\n--------------------\n"); // 增加表头

                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        @SuppressLint("Range")
                        String name = cursor.getString(cursor.getColumnIndex("name"));
                        @SuppressLint("Range")
                        int age = cursor.getInt(cursor.getColumnIndex("age"));
                        textView4.append(name + "\t\t\t" + age + "\n");
                    } while (cursor.moveToNext());
                    cursor.close();
                } else {
                     textView4.append("未查询到数据");
                     if (cursor != null) cursor.close();
                }
            }
        });
    }

    private boolean isEmpty(EditText et) {
        return et.getText().toString().trim().isEmpty();
    }

    private void performDelete(String str1, String str2, EditText exitText1, EditText exitText2) {
        StringBuilder whereClause = new StringBuilder();
        List<String> whereArgsList = new ArrayList<>();

        if (!str1.isEmpty()) {
            whereClause.append("name=?");
            whereArgsList.add(str1);
        }
        if (!str2.isEmpty()) {
            if (whereClause.length() > 0) {
                whereClause.append(" AND ");
            }
            whereClause.append("age=?");
            whereArgsList.add(str2);
        }

        String[] whereArgs = whereArgsList.toArray(new String[0]);
        int rows = database.delete("person", whereClause.toString(), whereArgs);
        if (rows > 0) {
            Toast.makeText(getApplicationContext(), "删除成功，共删除 " + rows + " 条", Toast.LENGTH_SHORT).show();
            exitText1.setText("");
            exitText2.setText("");
        } else {
            Toast.makeText(getApplicationContext(), "未找到匹配数据", Toast.LENGTH_SHORT).show();
        }
    }

    private void performUpdate(String str1, String str2, EditText editText3) {
         String newAgeStr = editText3.getText().toString().trim();
         StringBuilder whereClause = new StringBuilder();
         List<String> whereArgsList = new ArrayList<>();

         if (!str1.isEmpty()) {
             whereClause.append("name=?");
             whereArgsList.add(str1);
         }
         if (!str2.isEmpty()) {
             if (whereClause.length() > 0) {
                 whereClause.append(" AND ");
             }
             whereClause.append("age=?");
             whereArgsList.add(str2);
         }
         
         try {
             String[] whereArgs = whereArgsList.toArray(new String[0]);
             ContentValues values = new ContentValues();
             values.put("age", Integer.parseInt(newAgeStr));
             int rows = database.update("person", values, whereClause.toString(), whereArgs);
             if (rows > 0) {
                 Toast.makeText(getApplicationContext(), "更新成功，共更新 " + rows + " 条", Toast.LENGTH_SHORT).show();
                 editText3.setText("");
             } else {
                 Toast.makeText(getApplicationContext(), "未找到匹配数据", Toast.LENGTH_SHORT).show();
             }
         } catch (NumberFormatException e) {
             Toast.makeText(getApplicationContext(), "新年龄必须是数字", Toast.LENGTH_SHORT).show();
         }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null && database.isOpen()) {
            database.close();
        }
        if (myopenhelper != null) {
            myopenhelper.close();
        }
    }
}
