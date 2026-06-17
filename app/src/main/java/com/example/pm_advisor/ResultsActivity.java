package com.example.pm_advisor;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ResultsActivity extends AppCompatActivity {

    // Класс-модель для удобной сортировки результатов (ЛР11)
    static class MethodologyResult {
        String name;
        int score;
        String pros;
        String cons;
        String url;

        MethodologyResult(String name, int score, String pros, String cons, String url) {
            this.name = name;
            this.score = score;
            this.pros = pros;
            this.cons = cons;
            this.url = url;
        }
    }

    private ArrayList<MethodologyResult> filteredResults = new ArrayList<>();
    private ScoreChartView scoreChartView;
    private ListView listResults;
    private Button btnSort;

    private String projectType;
    private int teamSize;
    private boolean flexibleReq;
    private boolean fixedDeadlines;
    private String companyProfile;
    private int topLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        scoreChartView = findViewById(R.id.scoreChartView);
        listResults = findViewById(R.id.listResults);
        btnSort = findViewById(R.id.btnSort);

        // Получаем параметры из Интента (ЛР9)
        projectType = getIntent().getStringExtra("project_type");
        teamSize = getIntent().getIntExtra("team_size", 5);
        flexibleReq = getIntent().getBooleanExtra("flexible_req", false);
        fixedDeadlines = getIntent().getBooleanExtra("fixed_deadlines", false);
        companyProfile = getIntent().getStringExtra("company_profile");
        topLimit = getIntent().getIntExtra("top_limit", 3);

        calculateScores(); // Высчитываем баллы по алгоритму взвешенной суммы (ЛР11)
        updateUI(); // Выводим данные в график и список (ЛР8, ЛР11)

        // Настраиваем PopupMenu для сортировки (ЛР7)
        btnSort.setOnClickListener(v -> showSortPopup(v));

        // Клик по элементу списка в результатах тоже открывает карточку (ЛР12)
        listResults.setOnItemClickListener((parent, view, position, id) -> {
            String name = filteredResults.get(position).name;
            Intent intent = new Intent(this, ToolDetailActivity.class);
            intent.putExtra("methodology_name", name);
            startActivity(intent);
        });
    }

    // Метод вычисления баллов по алгоритму взвешенной суммы (ЛР11)
    private void calculateScores() {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM methodologies", null);
        ArrayList<MethodologyResult> allList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(1);
                String dbType = cursor.getString(2);
                int dbSize = cursor.getInt(3);
                int dbFlex = cursor.getInt(4);
                int dbDeadline = cursor.getInt(5);
                int dbProd = cursor.getInt(6);
                int dbOut = cursor.getInt(7);
                int dbInc = cursor.getInt(8);
                String url = cursor.getString(9);
                String pros = cursor.getString(10);
                String cons = cursor.getString(11);

                // Вычисляем балл (Максимум 100) (РГЗ Алгоритм)
                int score = 0;
                if (dbType.equalsIgnoreCase(projectType)) score += 20; // 20% за тип проекта
                if (dbSize >= teamSize) score += 20; // 20% за вместимость команды
                if (dbFlex == (flexibleReq ? 1 : 0)) score += 20; // 20% за гибкость требований
                if (dbDeadline == (fixedDeadlines ? 1 : 0)) score += 20; // 20% за жесткость дедлайнов

                // Прибавляем веса профиля компании (дает еще до 20% баллов)
                int profileWeight = 0;
                if ("PRODUCT".equals(companyProfile)) profileWeight = dbProd;
                else if ("OUTSOURCE".equals(companyProfile)) profileWeight = dbOut;
                else if ("INCUBATOR".equals(companyProfile)) profileWeight = dbInc;
                score += (int) (profileWeight * 0.2);

                allList.add(new MethodologyResult(name, score, pros, cons, url));
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Сортируем по убыванию баллов (ЛР11)
        Collections.sort(allList, (o1, o2) -> Integer.compare(o2.score, o1.score));

        // Берем только выбранное количество топ-записей (topLimit)
        filteredResults.clear();
        for (int i = 0; i < Math.min(topLimit, allList.size()); i++) {
            filteredResults.add(allList.get(i));
        }
    }

    // Вывод результатов на экран
    private void updateUI() {
        // 1. Передаем данные в кастомный график (ЛР8)
        int n = filteredResults.size();
        int[] chartValues = new int[n];
        String[] chartLabels = new String[n];
        for (int i = 0; i < n; i++) {
            chartValues[i] = filteredResults.get(i).score;
            chartLabels[i] = filteredResults.get(i).name;
        }
        scoreChartView.setData(chartValues, chartLabels);

        // 2. Выводим данные в ListView (ЛР11)
        ArrayList<HashMap<String, String>> listData = new ArrayList<>();
        for (MethodologyResult res : filteredResults) {
            HashMap<String, String> map = new HashMap<>();
            map.put("name", res.name);
            map.put("score", "Соответствие: " + res.score + "%");
            listData.add(map);
        }

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                listData,
                R.layout.item_result,
                new String[]{"name", "score"},
                new int[]{R.id.tvMethodName, R.id.tvMethodScore}
        );
        listResults.setAdapter(adapter);
    }

    // Показываем PopupMenu сортировки результатов (ЛР7)
    private void showSortPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenu().add(1, 101, 1, "Сортировать по баллам (убывание)");
        popup.getMenu().add(1, 102, 2, "Сортировать по названию (А-Я)");

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 101) {
                // Сортировка по баллам
                Collections.sort(filteredResults, (o1, o2) -> Integer.compare(o2.score, o1.score));
            } else if (item.getItemId() == 102) {
                // Сортировка по названию
                Collections.sort(filteredResults, (o1, o2) -> o1.name.compareTo(o2.name));
            }
            updateUI(); // Обновляем экран
            return true;
        });
        popup.show();
    }
}