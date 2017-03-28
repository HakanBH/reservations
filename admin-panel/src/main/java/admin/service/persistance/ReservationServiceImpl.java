package admin.service.persistance;

import admin.model.CanceledReservations;
import admin.model.Reservation;
import admin.repository.CanceledReservationsRepository;
import admin.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private CanceledReservationsRepository canceledReservationsRepository;

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
    public List<Reservation> findByFacilityIdAndDateAfter(Integer facilityId, Date date) {
        return reservationRepository.findByFacilityIdAndDateAfter(facilityId, date);
    }

    @Override
    public List<Reservation> findByUserIdAndDateAfter(Integer userId, Date date) {
        return reservationRepository.findByUserIdAndDateAfter(userId, date);
    }

    @Override
    public List<Reservation> delete(Integer reservationId) {

        Reservation reservation = reservationRepository.findOne(reservationId);
        reservationRepository.delete(reservationId);
        CanceledReservations canceledReservations = new CanceledReservations();
        canceledReservations.setDate(reservation.getDate());
        canceledReservations.setFacility(reservation.getFacility());
        canceledReservations.setFromHour(reservation.getFromHour());
        canceledReservations.setToHour(reservation.getToHour());
        canceledReservations.setUser(reservation.getUser());
        canceledReservationsRepository.save(canceledReservations);
        return (List<Reservation>) reservationRepository.findAll();
    }

    @Override
    public List<Reservation> deleteList(List<Integer> reservationsId) {
        for (Integer id : reservationsId) {
            Reservation reservation = reservationRepository.findOne(id);
            reservationRepository.delete(id);
            CanceledReservations canceledReservations = new CanceledReservations();
            canceledReservations.setDate(reservation.getDate());
            canceledReservations.setFacility(reservation.getFacility());
            canceledReservations.setFromHour(reservation.getFromHour());
            canceledReservations.setToHour(reservation.getToHour());
            canceledReservations.setUser(reservation.getUser());
            canceledReservationsRepository.save(canceledReservations);
        }
        return (List<Reservation>) reservationRepository.findAll();
    }

    @Override
    public Reservation update(Integer id, Reservation reservation) {
        Reservation entity = reservationRepository.findOne(id);
        entity.setDate(reservation.getDate());
        entity.setFromHour(reservation.getFromHour());
        entity.setToHour(reservation.getToHour());
        entity = reservationRepository.save(entity);
        return entity;
    }
}
