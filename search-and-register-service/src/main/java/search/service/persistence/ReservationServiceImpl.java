package search.service.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import search.model.Reservation;
import search.repository.ReservationRepository;

import java.sql.Date;
import java.util.List;

/**
 * Created by Trayan_Muchev on 7/22/2016.
 */
@Service("reservationService")
@Transactional
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation findById(Integer id) {
        return reservationRepository.findOne(id);
    }

    @Override
    public List<Reservation> findAll() {
        return (List<Reservation>) reservationRepository.findAll();
    }

    @Override
    public List<Reservation> findByFacilityId(Integer facilityId) {
        return reservationRepository.findByFacilityId(facilityId);
    }

    @Override
    public List<Reservation> findByUserId(Integer userId) {
        return reservationRepository.findByUserId(userId);
    }

    @Override
    public List<Reservation> findByDateAfter(Date date) {
        return reservationRepository.findByDateAfter(date);
    }

    @Override
    public List<Reservation> findByDateBefore(Date date) {
        return reservationRepository.findByDateBefore(date);
    }

    @Override
    public List<Reservation> findByDate(Date date) {
        return reservationRepository.findByDate(date);
    }

    @Override
    public List<Reservation> findByUserIdAndFacilityId(Integer userId, Integer facilityId) {
        return reservationRepository.findByUserIdAndFacilityId(userId, facilityId);
    }

    @Override
    public void delete(Integer reservationId) {
//        Reservation reservation=reservationRepository.findOne(reservationId);
//        reservation.setDeleted(TRUE);
//        reservationRepository.save(reservation);
        reservationRepository.delete(reservationId);
    }
}
