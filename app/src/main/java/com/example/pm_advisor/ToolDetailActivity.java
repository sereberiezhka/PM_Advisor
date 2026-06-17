package com.example.pm_advisor;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileOutputStream;
import android.net.Uri;
import androidx.core.content.FileProvider;

public class ToolDetailActivity extends AppCompatActivity {

    private String name, pros, cons, siteUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        findViewById(R.id.btnExport).setOnClickListener(v -> exportReportToFile());

        String methodologyName = getIntent().getStringExtra("methodology_name");

        // Подгружаем данные из SQLite (ЛР11)
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM methodologies WHERE name = ?", new String[]{methodologyName});

        if (cursor.moveToFirst()) {
            name = cursor.getString(1);
            siteUrl = cursor.getString(9);
            pros = cursor.getString(10);
            cons = cursor.getString(11);
        }
        cursor.close();

        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        tvTitle.setText(name);

        // Загружаем фрагменты динамически через Bundle (ЛР12) (стр. 18 примера)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.charContainer, CharacteristicsFragment.newInstance(pros))
                    .replace(R.id.riskContainer, RisksFragment.newInstance(cons))
                    .commit();
        }

        // Кнопка открытия сайта в WebView (ЛР9)
        findViewById(R.id.btnOpenSite).setOnClickListener(v -> {
            Intent intent = new Intent(this, WebActivity.class);
            intent.putExtra("url", siteUrl);
            startActivity(intent);
        });

        // Кнопка шаринга по Email (ЛР9) (стр. 12-13 примера)
        findViewById(R.id.btnShare).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Рекомендация ИТ-методологии: " + name);
            intent.putExtra(Intent.EXTRA_TEXT, "Рекомендую изучить методологию: " + name + ".\n\n" +
                    "Плюсы:\n" + pros + "\n\n" +
                    "Минусы и риски:\n" + cons + "\n\n" +
                    "Подробнее здесь: " + siteUrl);
            startActivity(Intent.createChooser(intent, "Порекомендовать коллеге"));
        });

    }
    // Метод экспорта отчета в текстовый файл (ЛР13)
    private void exportReportToFile() {
        try {
            // Получаем доступ к папке приложения во внешней памяти (ЛР13)
            File path = getExternalFilesDir(null);
            File file = new File(path, "methodology_report.txt");

            // Записываем данные в файл
            FileOutputStream fos = new FileOutputStream(file);
            String reportText = "ОТЧЕТ ПО МЕТОДОЛОГИИ: " + name + "\n\n" +
                    "ПРЕИМУЩЕСТВА:\n" + pros + "\n\n" +
                    "ОГРАНИЧЕНИЯ И РИСКИ:\n" + cons;

            fos.write(reportText.getBytes());
            fos.close();

            // Генерируем безопасный URI через FileProvider (ЛР13)
            String authority = getPackageName() + ".fileprovider";
            Uri fileUri = FileProvider.getUriForFile(this, authority, file);

            // Создаем интент просмотра файла (ЛР13)
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(fileUri, "text/plain");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Временные права на чтение

            startActivity(Intent.createChooser(intent, "Открыть отчет с помощью:"));

        } catch (Exception e) {
            e.printStackTrace();
            android.widget.Toast.makeText(this, "Ошибка экспорта файла!", android.widget.Toast.LENGTH_SHORT).show();
        }
    }
}