package com.example.pm_advisor;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;

public class CatalogActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        ListView listView = findViewById(R.id.listCatalog);
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT name, project_type FROM methodologies", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put("name", cursor.getString(0));
                map.put("desc", "Рекомендуется для: " + cursor.getString(1));
                data.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                data,
                R.layout.item_result, // Повторно используем наш красивый макет элементов
                new String[]{"name", "desc"},
                new int[]{R.id.tvMethodName, R.id.tvMethodScore}
        );
        listView.setAdapter(adapter);

        // Клик на элемент списка открывает карточку детализации (ЛР12)
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String name = data.get(position).get("name");
            Intent intent = new Intent(this, ToolDetailActivity.class);
            intent.putExtra("methodology_name", name);
            startActivity(intent);
        });
    }
}