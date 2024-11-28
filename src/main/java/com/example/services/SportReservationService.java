package com.example.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.SportReservation;
import com.example.model.User;
import com.example.repository.SportReservationrepository;

@Service
public class SportReservationService {

    @Autowired
    private SportReservationrepository sportReservationrepository;

    public void newReserve( User studentId, LocalDateTime date, int pista) {
        sportReservationrepository.save(new SportReservation(studentId, date, pista));

    }

    private List<SportReservation> getUserReservations(User user) {
        return sportReservationrepository.findByStudentIdOrderBySportReservationIdDesc(user);
    }

    public SportReservation getUserReserve(User user) {
        List<SportReservation> reservations = getUserReservations(user);
        if (!reservations.isEmpty()) {
            return reservations.get(0);
        } else {
            return null; // o lanzar una excepción específica si lo prefieres
        }
    }

    public boolean isreserveActive(User user) {
        if (getUserReserve(user) == null) {
            return false;
        }
        return getUserReserve(user).getDate().isAfter(LocalDateTime.now());
    }

    public List<SportReservation> getActivereservations(int pista, LocalDate date) { 
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        return sportReservationrepository.findByDateAndPista(startOfDay, endOfDay, pista);
    }


    public void deleteReservation(User user) {
        SportReservation reserve = getUserReserve(user);
        reserve.setDate(LocalDateTime.now().minusDays(7));
        sportReservationrepository.save(reserve);

    }
}
