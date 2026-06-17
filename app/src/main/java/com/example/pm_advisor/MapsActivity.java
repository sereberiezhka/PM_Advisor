package com.example.pm_advisor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int score = 0;

    // Флаги прохождения викторин из SharedPreferences (ЛР10) (стр. 15 примера)
    private boolean isKanbanDone, isWaterfallDone, isScrumDone, isAgileDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Инициализируем карту
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        loadProgress(); // Загружаем прогресс из SharedPreferences (ЛР10)
    }

    private void loadProgress() {
        SharedPreferences prefs = getSharedPreferences("PM_Quiz_Prefs", MODE_PRIVATE);
        isKanbanDone = prefs.getBoolean("Kanban", false);
        isWaterfallDone = prefs.getBoolean("Waterfall", false);
        isScrumDone = prefs.getBoolean("Scrum", false);
        isAgileDone = prefs.getBoolean("Agile", false);

        score = 0;
        if (isKanbanDone) score++;
        if (isWaterfallDone) score++;
        if (isScrumDone) score++;
        if (isAgileDone) score++;

        Toolbar toolbar = findViewById(R.id.toolbarMap);
        toolbar.setTitle("Угадано создателей: " + score + " из 4");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Координаты исторических мест (ЛР10)
        LatLng toyotaCity = new LatLng(35.0824, 137.1562); // Родина Kanban (Япония)
        LatLng pmiHQ = new LatLng(39.9926, -75.4053); // Родина Waterfall (Пенсильвания, США)
        LatLng snowbird = new LatLng(40.5828, -111.6558); // Родина Scrum/Agile (Юта, США)
        LatLng zurich = new LatLng(47.3769, 8.5417); // Европейский Agile (Швейцария)

        // 1. Маркер Kanban (Япония)
        MarkerOptions markerKanban = new MarkerOptions().position(toyotaCity).title("Kanban");
        if (isKanbanDone) {
            // Если угадано — красим маркер в зеленый (ЛР10) (стр. 16 примера)
            markerKanban.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }
        mMap.addMarker(markerKanban);

        // 2. Маркер Waterfall (США)
        MarkerOptions markerWaterfall = new MarkerOptions().position(pmiHQ).title("Waterfall");
        if (isWaterfallDone) {
            markerWaterfall.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }
        mMap.addMarker(markerWaterfall);

        // 3. Маркер Scrum (США)
        MarkerOptions markerScrum = new MarkerOptions().position(snowbird).title("Scrum");
        if (isScrumDone) {
            markerScrum.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }
        mMap.addMarker(markerScrum);

        // 4. Маркер Agile (Швейцария)
        MarkerOptions markerAgile = new MarkerOptions().position(zurich).title("Agile");
        if (isAgileDone) {
            markerAgile.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }
        mMap.addMarker(markerAgile);

        // Камера на центр Земли, зум 1.0f
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(20, 0), 1.0f));

        // Вешаем слушатель кликов на маркеры (ЛР10) (стр. 15 примера)
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String title = marker.getTitle();

                // Проверяем, пройдена ли уже игра для этого маркера
                if ("Kanban".equals(title) && isKanbanDone) return false;
                if ("Waterfall".equals(title) && isWaterfallDone) return false;
                if ("Scrum".equals(title) && isScrumDone) return false;
                if ("Agile".equals(title) && isAgileDone) return false;

                // Если не пройдена — летим на экран вопросов (ЛР10)
                Intent intent = new Intent(MapsActivity.this, QuizActivity.class);
                intent.putExtra("city_name", title);
                startActivity(intent);
                finish(); // Закрываем карту, чтобы она пересоздалась с новым прогрессом при возврате
                return true;
            }
        });
    }
}