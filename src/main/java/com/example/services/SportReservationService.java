package com.example.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.SportReservation;
import com.example.model.User;
import com.example.repository.SportReservationrepository;

@Service
public class SportReservationService {

    @Autowired
    private SportReservationrepository sportReservationrepository;

    public void newReserve( User studentId, LocalDate date, int startHour, int endHour,  boolean state) {
        sportReservationrepository.save(new SportReservation(studentId, date, startHour, endHour));

    }

    private List<SportReservation> getUserReservations(User user) {
        return sportReservationrepository.findByStudentIdOrderBySportReservationIdDesc(user);
    }

    public SportReservation getUserReserve(User user) {
        List<SportReservation> reservations = getUserReservations(user);
        return reservations.get(0);
    }

    public boolean isreserveActive(User user) {
        return getUserReserve(user).getDate().isAfter(LocalDate.now());
    }
    
}
