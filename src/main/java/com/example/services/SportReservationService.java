package com.example.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public void newReserve( User studentId, LocalDateTime date) {
        sportReservationrepository.save(new SportReservation(studentId, date));

    }

    private List<SportReservation> getUserReservations(User user) {
        return sportReservationrepository.findByStudentIdOrderBySportReservationIdDesc(user);
    }

    public SportReservation getUserReserve(User user) {
        List<SportReservation> reservations = getUserReservations(user);
        return reservations.get(0);
    }

    public boolean isreserveActive(User user) {
        return getUserReserve(user).getDate().isAfter(LocalDateTime.now());
    }

    public List<SportReservation> getActivereservations() {
        return sportReservationrepository.findByDateBeforeOrEqual(LocalDate.now());
    }
    
}
