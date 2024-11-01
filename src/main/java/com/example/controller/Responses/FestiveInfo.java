package com.example.controller.Responses;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FestiveInfo {
    private int day;
    private int month;
    private int year;
    private String color;
    private int startedXDaysAgo;
    private String local;

    public FestiveInfo(int day, int month, int year, String color, int startedXDaysAgo, String local) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.color = color;
        this.startedXDaysAgo = startedXDaysAgo;
        this.local = local;
    }

    public static List<FestiveInfo> generateFestivesFromRange(FestiveInfo festive) {
        List<FestiveInfo> festiveList = new ArrayList<>();

        // Crear la fecha final (el día del festivo especificado)
        Calendar endDate = Calendar.getInstance();
        endDate.set(festive.getYear(), festive.getMonth() - 1, festive.getDay());
        
        // Calcular la fecha de inicio restando los xdaysAgo
        Calendar startDate = (Calendar) endDate.clone();
        startDate.add(Calendar.DAY_OF_MONTH, -festive.getStartedXDaysAgo());
        
        // Iterar desde la fecha de inicio hasta la fecha final
        Calendar currentDate = (Calendar) startDate.clone();
        while (!currentDate.after(endDate)) {
            // Crear un nuevo objeto FestiveInfo para cada día
            festiveList.add(new FestiveInfo(
                currentDate.get(Calendar.DAY_OF_MONTH),
                currentDate.get(Calendar.MONTH) + 1, // Ajustar el mes al rango 1-12
                currentDate.get(Calendar.YEAR),
                festive.getColor(), // Usar el mismo color
                festive.getStartedXDaysAgo(),
                festive.getLocal() // Usar la misma localidad
            ));
            
            // Avanzar un día
            currentDate.add(Calendar.DAY_OF_MONTH, 1);
        }
        return festiveList;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public String getColor() {
        return color;
    }

    public int getStartedXDaysAgo() {
        return startedXDaysAgo;
    }

    public String getLocal() {
        return local;
    }
}
