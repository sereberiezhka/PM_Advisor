package com.example.pm_advisor;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class ParamsActivity extends AppCompatActivity {

    private String selectedProjectType = null; // Выбранный тип проекта (ЛР3)
    private int teamSizeLimit = 5; // Размер команды по SeekBar (ЛР4)
    private int topLimit = 3; // Топ-выдача (ЛР2)
    private String companyProfile = "PRODUCT"; // Профиль компании по умолчанию (ЛР7)

    private Button[] projectTypeButtons;
    private TextView tvTeamSizeLabel;
    private TextView tvTopLabel;
    private SwitchCompat switchFlexibleReq;
    private SwitchCompat switchFixedDeadlines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_params);

        // Настройка Toolbar (ЛР7)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Связываем элементы интерфейса
        tvTeamSizeLabel = findViewById(R.id.tvTeamSizeLabel);
        tvTopLabel = findViewById(R.id.tvTopLabel);
        switchFlexibleReq = findViewById(R.id.switchFlexibleReq);
        switchFixedDeadlines = findViewById(R.id.switchFixedDeadlines);

        // 1. Настройка кнопок выбора типа проекта (ЛР3)
        projectTypeButtons = new Button[]{
                findViewById(R.id.btnStartup),
                findViewById(R.id.btnEnterprise),
                findViewById(R.id.btnGov),
                findViewById(R.id.btnResearch)
        };

        for (Button btn : projectTypeButtons) {
            btn.setOnClickListener(v -> selectProjectType(btn.getText().toString()));
        }

        // 2. Настройка SeekBar для размера команды (ЛР4)
        SeekBar seekBar = findViewById(R.id.seekBarTeamSize);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                teamSizeLimit = progress;
                tvTeamSizeLabel.setText("Размер команды: до " + progress + " человек");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // 3. Плюсик и минусик для размера топа (ЛР2)
        findViewById(R.id.btnPlus).setOnClickListener(v -> {
            if (topLimit < 5) {
                topLimit++;
                tvTopLabel.setText("Показать топ методологий: " + topLimit);
            }
        });

        findViewById(R.id.btnMinus).setOnClickListener(v -> {
            if (topLimit > 1) {
                topLimit--;
                tvTopLabel.setText("Показать топ методологий: " + topLimit);
            }
        });

        // 4. Настройка FAB (ЛР5-6)
        FloatingActionButton fab = findViewById(R.id.fabSelect);
        fab.setOnClickListener(view -> {
            if (selectedProjectType == null) {
                // Если тип проекта не выбран — показываем Toast (ЛР5)
                Toast.makeText(this, "Выберите тип ИТ-проекта!", Toast.LENGTH_SHORT).show();
            } else {
                // Если всё ок — показываем Snackbar и переходим на экран результатов (ЛР6, ЛР9)
                Snackbar.make(view, "Подбираем оптимальные методологии...", Snackbar.LENGTH_SHORT).show();

                // (ResultsActivity мы создадим на следующем шаге)
                Intent intent = new Intent(this, ResultsActivity.class);
                intent.putExtra("project_type", selectedProjectType);
                intent.putExtra("team_size", teamSizeLimit);
                intent.putExtra("flexible_req", switchFlexibleReq.isChecked());
                intent.putExtra("fixed_deadlines", switchFixedDeadlines.isChecked());
                intent.putExtra("company_profile", companyProfile);
                intent.putExtra("top_limit", topLimit);

                // Даем Снекбару показаться полсекунды перед переходом
                view.postDelayed(() -> startActivity(intent), 500);
            }
        });
    }

    // Метод цветовой подсветки нажатой кнопки (ЛР3)
    private void selectProjectType(String typeName) {
        selectedProjectType = typeName;
        for (Button btn : projectTypeButtons) {
            if (btn.getText().toString().equals(typeName)) {
                // Выбранная кнопка — темно-синяя с белым текстом (ЛР3)
                btn.setBackgroundColor(Color.parseColor("#1A237E"));
                btn.setTextColor(Color.WHITE);
            } else {
                // Остальные — серые с черным текстом (ЛР3)
                btn.setBackgroundColor(Color.parseColor("#E0E0E0"));
                btn.setTextColor(Color.BLACK);
            }
        }
    }

    // Инициализация Toolbar меню профилей (ЛР7)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_params, menu);
        return true;
    }

    // Обработка выбора профилей в меню (ЛР7)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Toolbar toolbar = findViewById(R.id.toolbar);

        if (id == R.id.profile_product) {
            companyProfile = "PRODUCT";
            toolbar.setTitle("Профиль: Продуктовая");
            return true;
        } else if (id == R.id.profile_outsource) {
            companyProfile = "OUTSOURCE";
            toolbar.setTitle("Профиль: Аутсорсинг");
            return true;
        } else if (id == R.id.profile_incubator) {
            companyProfile = "INCUBATOR";
            toolbar.setTitle("Профиль: Стартап-студия");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}