package com.example.pm_advisor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Получаем количество методологий из нашей заглушки БД (ЛР11)
        int count = new DBHelper(this).getAllMethodologies().size();
        TextView tvCount = findViewById(R.id.tvMethodologiesCount);
        tvCount.setText("В базе методологий: " + count);

        // Назначаем обработчики кликов на кнопки навигации (ЛР2)
        // (Классы ParamsActivity, CatalogActivity и MapsActivity мы создадим по шагам позже)
        findViewById(R.id.btnSelect).setOnClickListener(v -> {
            startActivity(new Intent(this, ParamsActivity.class));
        });

        findViewById(R.id.btnCatalog).setOnClickListener(v -> {
            startActivity(new Intent(this, CatalogActivity.class));
        });

        findViewById(R.id.btnMap).setOnClickListener(v -> {
            startActivity(new Intent(this, MapsActivity.class));
        });
    }
}