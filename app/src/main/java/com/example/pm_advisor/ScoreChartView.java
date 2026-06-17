package com.example.pm_advisor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ScoreChartView extends View {
    private int[] values = new int[0];
    private String[] labels = new String[0];
    private float progress = 0.0f; // Для плавной анимации роста (ЛР8)
    private Paint barPaint;
    private Paint textPaint;

    public ScoreChartView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Настраиваем кисть для наших оранжевых столбиков (ЛР8)
        barPaint = new Paint();
        barPaint.setColor(Color.parseColor("#FF6D00")); // Янтарно-оранжевый акцент
        barPaint.setStyle(Paint.Style.FILL);

        // Настраиваем кисть для текста
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(34);
        textPaint.setAntiAlias(true);
    }

    public void setData(int[] values, String[] labels) {
        this.values = values;
        this.labels = labels;
        this.progress = 0.0f; // Сбрасываем анимацию при установке новых данных
        animateChart();
    }

    private void animateChart() {
        if (progress < 1.0f) {
            progress += 0.04f; // Шаг прироста высоты столбиков (ЛР8)
            if (progress > 1.0f) progress = 1.0f;
            postInvalidateDelayed(16); // Перерисовываем экран каждые 16 мс (ЛР8)
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (values == null || values.length == 0) return;

        int w = getWidth();
        int h = getHeight();
        int n = values.length;

        float barW = (w - 40f) / n;

        for (int i = 0; i < n; i++) {
            float left = i * barW + 30;
            float right = left + barW - 30;

            // Увеличиваем нижнее поле до 180 пикселей, чтобы поместился повернутый текст (ЛР8)
            float maxBarHeight = (h - 200f) * (values[i] / 100.0f);
            float currentHeight = maxBarHeight * progress; // Применяем текущий прогресс

            float top = h - 140f - currentHeight;
            float bottom = h - 140f;

            canvas.drawRect(left, top, right, bottom, barPaint);

            // Рисуем балл над столбиком
            canvas.drawText(String.valueOf((int) (values[i] * progress)), left + 10, top - 15f, textPaint);

            // ==========================================
            // РИСУЕМ ПОВЕРНУТЫЙ ТЕКСТ (ЛР8) (Устранение наложений!)
            // ==========================================
            canvas.save();
            // Поворачиваем холст на -25 градусов относительно начала текста под столбиком
            canvas.rotate(-25, left + 5, h - 90f);
            textPaint.setTextSize(26); // Уменьшаем шрифт подписей до 26sp, чтобы не улетало за границы экрана
            canvas.drawText(labels[i], left + 5, h - 90f, textPaint);
            canvas.restore(); // Возвращаем холст обратно

            textPaint.setTextSize(34); // Возвращаем стандартный размер для баллов над столбиками
            // ==========================================
        }

        // ЗАПУСКАЕМ СЛЕДУЮЩИЙ КАДР АНИМАЦИИ (ЛР8)
        if (progress < 1.0f) {
            progress += 0.04f; // Шаг анимации
            if (progress > 1.0f) progress = 1.0f;
            postInvalidateDelayed(16); // Перерисовываем через 16 мс (ЛР8)
        }
    }
}