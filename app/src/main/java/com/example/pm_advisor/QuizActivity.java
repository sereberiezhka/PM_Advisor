package com.example.pm_advisor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class QuizActivity extends AppCompatActivity {
    private String city;
    private int correctIndex;
    private Button[] buttons;
    private String questionText;
    private String[] answers = new String[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        city = getIntent().getStringExtra("city_name");
        if (city == null) city = "Kanban";

        TextView tvTitle = findViewById(R.id.tvQuizTitle);
        tvTitle.setText("Викторина: " + city);

        TextView tvQuestion = findViewById(R.id.tvQuestion);

        buttons = new Button[]{
                findViewById(R.id.btnAns0),
                findViewById(R.id.btnAns1),
                findViewById(R.id.btnAns2),
                findViewById(R.id.btnAns3)
        };

        // Загружаем вопрос и ответы из базы данных SQLite (ЛР11) (стр. 15 примера)
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM quiz WHERE city = ?", new String[]{city});

        if (cursor.moveToFirst()) {
            questionText = cursor.getString(2);
            answers[0] = cursor.getString(3);
            answers[1] = cursor.getString(4);
            answers[2] = cursor.getString(5);
            answers[3] = cursor.getString(6);
            correctIndex = cursor.getInt(7);

            tvQuestion.setText(questionText);
            for (int i = 0; i < 4; i++) {
                buttons[i].setText(answers[i]);
                final int idx = i;
                buttons[i].setOnClickListener(v -> checkAnswer(idx));
            }
        }
        cursor.close();
    }

    private void checkAnswer(int selectedIndex) {
        if (selectedIndex == correctIndex) {
            // Зеленый для верного ответа (стр. 14 примера)
            buttons[selectedIndex].setBackgroundColor(Color.parseColor("#4CAF50"));
            buttons[selectedIndex].setTextColor(Color.WHITE);

            // Сохраняем прогресс в SharedPreferences (ЛР10) (стр. 15 примера)
            SharedPreferences prefs = getSharedPreferences("PM_Quiz_Prefs", MODE_PRIVATE);
            prefs.edit().putBoolean(city, true).apply();

            showDialog("Верно!", "Вы правильно ответили на исторический вопрос!");
        } else {
            // Красный для неверного ответа (стр. 14 примера)
            buttons[selectedIndex].setBackgroundColor(Color.parseColor("#F44336"));
            buttons[selectedIndex].setTextColor(Color.WHITE);
            buttons[selectedIndex].setEnabled(false);
        }
    }

    private void showDialog(String title, String msg) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Вернуться к карте", (dialog, which) -> {
                    // Возвращаемся на карту (стр. 15 примера)
                    startActivity(new Intent(this, MapsActivity.class));
                    finish();
                })
                .show();
    }
}