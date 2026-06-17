package com.example.pm_advisor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "pm_advisor.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Создаем таблицу методологий (ЛР11)
        db.execSQL("CREATE TABLE methodologies (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "project_type TEXT, " +
                "team_size_max INTEGER, " +
                "is_flexible INTEGER, " +
                "is_fixed_deadline INTEGER, " +
                "product_weight INTEGER, " +
                "outsource_weight INTEGER, " +
                "incubator_weight INTEGER, " +
                "site_url TEXT, " +
                "pros TEXT, " +
                "cons TEXT)");

        // 2. Создаем таблицу викторины для карты (ЛР10, ЛР11)
        db.execSQL("CREATE TABLE quiz (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "city TEXT, " +
                "question TEXT, " +
                "ans0 TEXT, " +
                "ans1 TEXT, " +
                "ans2 TEXT, " +
                "ans3 TEXT, " +
                "correct_index INTEGER)");

        // Наполняем базу реальными методологиями (Везде строго по 4 предложения в Плюсах и Минусах!)
        insertMethodology(db, "Scrum", "Стартап", 9, 1, 0, 90, 70, 95,
                "https://ru.wikipedia.org/wiki/Scrum",
                "Данный фреймворк значительно повышает прозрачность процессов за счет регулярных встреч и демонстраций. Он позволяет быстро адаптироваться к изменениям требований клиента на основе коротких итераций (спринтов). Команда получает высокую степень самоорганизации и мотивации благодаря отсутствию микроменеджмента. Конечный пользователь начинает получать ценность от работающего продукта уже после первых недель разработки.",
                "Процесс плохо масштабируется на очень крупные проекты без внедрения сложных дополнительных фреймворков. Итоговый результат и сроки сильно зависят от постоянной вовлеченности и обратной связи со стороны заказчика. Отсутствие строгого планирования на долгий срок может привести к раздуванию рамок проекта. Методология требует высокой квалификации и сознательности от каждого члена команды.");

        insertMethodology(db, "Kanban", "Энтерпрайз", 20, 1, 0, 80, 85, 75,
                "https://ru.wikipedia.org/wiki/Канбан",
                "Визуализация процессов на доске помогает быстро выявлять узкие места в потоке задач. Гибкость планирования позволяет менять приоритеты задач в любой момент без ожидания окончания спринта. Отсутствие жестких дедлайнов снижает уровень стресса в коллективе и повышает качество кода. Фокус на непрерывном улучшении (Кайдзен) помогает постоянно оптимизировать рабочие процессы компании.",
                "При отсутствии жестких дедлайнов бывает крайне сложно спрогнозировать точную дату релиза для бизнеса. Методология требует исключительной самодисциплины и зрелости от всех разработчиков. В условиях хаотичного потока новых требований без четких приоритетов работа может парализоваться. Она малоэффективна в проектах с фиксированным бюджетом и фиксированными сроками.");

        insertMethodology(db, "Waterfall", "Гос. сектор", 100, 0, 1, 40, 90, 30,
                "https://ru.wikipedia.org/wiki/Каскадная_модель",
                "Четкое планирование на старте позволяет заранее рассчитать точный бюджет и сроки проекта. Строгая последовательность этапов гарантирует, что к следующему шагу приступят только после завершения предыдущего. Подробная техническая документация облегчает поддержку и передачу проекта другим специалистам. Данный подход идеально подходит для работы с государственными контрактами и строгими ТЗ.",
                "Нулевая гибкость требований делает невозможным внесение изменений в середине процесса разработки. Заказчик видит готовый продукт только в самом конце, что повышает риск его несоответствия ожиданиям. Ошибки проектирования обнаруживаются слишком поздно, на этапе тестирования, что удорожает их исправление. Тестировщики подключаются к работе только в конце проекта, создавая пиковые нагрузки.");

        insertMethodology(db, "XP (Extreme)", "Научные R&D", 12, 1, 1, 85, 60, 80,
                "https://ru.wikipedia.org/wiki/Экстремальное_программирование",
                "Постоянный упор на качество кода (TDD, рефакторинг) сводит количество багов к минимуму. Парное программирование способствует быстрому обмену опытом и обучению младших специалистов. Короткие циклы обратной связи позволяют мгновенно реагировать на пожелания заказчика. Простота архитектурных решений облегчает дальнейшую поддержку и модификацию системы.",
                "Методология требует наивысшей квалификации и дисциплины от всех участников команды. Парное кодирование и написание автотестов существенно увеличивают затраты на разработку на начальных этапах. Почти полное отсутствие формальной документации усложняет передачу проекта сторонним командам. Подход очень тяжело масштабировать на команды численностью более 15 человек.");

        insertMethodology(db, "Lean (Бережливая)", "Стартап", 15, 1, 0, 85, 70, 90,
                "https://ru.wikipedia.org/wiki/Бережливая_разработка_программного_обеспечения",
                "Постоянное устранение любых видов потерь (мусора) максимизирует экономическую эффективность разработки. Быстрая доставка ценных функций пользователю позволяет оперативно проверять бизнес-гипотезы. Делегирование полномочий и доверие к разработчикам сильно повышают мотивацию в коллективе. Фокус на качестве встроенных проверок минимизирует необходимость долгого тестирования.",
                "Для успешного внедрения требуется исключительно опытная и высокоорганизованная команда разработчиков. В жестко регулируемых отраслях со строгим контролем требований этот подход применить крайне тяжело. Избыточный фокус на скорости поставки без должного планирования может привести к техническому долгу. Методология требует кардинальной перестройки всей организационной культуры.");

        // Заполняем вопросы викторины (Задание 2)
        insertQuiz(db, "Kanban", "Какая концепция бережливого производства лежит в основе Kanban?", "Just In Time (Точно в срок)", "Six Sigma", "Kaizen (Непрерывное улучшение)", "TQM (Управление качеством)", 0);
        insertQuiz(db, "Waterfall", "Какова главная особенность каскадной модели (Waterfall)?", "Итеративность", "Строгая последовательность этапов", "Отсутствие планирования", "Самоорганизация команды", 1);
        insertQuiz(db, "Scrum", "В каком году был подписан Манифест гибкой разработки в Snowbird?", "1995 год", "2001 год", "2010 год", "1986 год", 1);
        insertQuiz(db, "Agile", "Какая ценность НЕ входит в Манифест гибкой разработки (Agile)?", "Люди и взаимодействие важнее процессов", "Работающий продукт важнее документации", "Следование плану важнее изменений", "Сотрудничество с заказчиком важнее контракта", 2);
    }

    private void insertMethodology(SQLiteDatabase db, String name, String type, int size, int flex, int deadline, int prod, int out, int inc, String url, String pros, String cons) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("project_type", type);
        cv.put("team_size_max", size);
        cv.put("is_flexible", flex);
        cv.put("is_fixed_deadline", deadline);
        cv.put("product_weight", prod);
        cv.put("outsource_weight", out);
        cv.put("incubator_weight", inc);
        cv.put("site_url", url);
        cv.put("pros", pros);
        cv.put("cons", cons);
        db.insert("methodologies", null, cv);
    }

    private void insertQuiz(SQLiteDatabase db, String city, String question, String ans0, String ans1, String ans2, String ans3, int correct) {
        ContentValues cv = new ContentValues();
        cv.put("city", city);
        cv.put("question", question);
        cv.put("ans0", ans0);
        cv.put("ans1", ans1);
        cv.put("ans2", ans2);
        cv.put("ans3", ans3);
        cv.put("correct_index", correct);
        db.insert("quiz", null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS methodologies");
        db.execSQL("DROP TABLE IF EXISTS quiz");
        onCreate(db);
    }

    public ArrayList<String> getAllMethodologies() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM methodologies", null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}