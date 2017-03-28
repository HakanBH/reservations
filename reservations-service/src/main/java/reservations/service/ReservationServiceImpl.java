package reservations.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reservations.model.*;
import reservations.repository.CanceledReservationsRepository;
import reservations.repository.PendingAndRejectedReservationsRepository;
import reservations.repository.ReservationRepository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.FALSE;

/**
 * Created by Trayan_Muchev on 9/9/2016.
 */
@Service("reservationService")
@Transactional
public class ReservationServiceImpl implements ReservationService {
    private static final boolean NOT_DELETED_FIELDS = FALSE;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PendingAndRejectedReservationsRepository pendingAndRejectedReservationsRepository;

    @Autowired
    private CanceledReservationsRepository canceledReservationsRepository;


    @Override
    public Reservation save(Reservation reservation) {
        reservation = reservationRepository.save(reservation);
        return reservationRepository.findOne(reservation.getId());
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
        List<Reservation> reservations = reservationRepository.findByUserIdAndDateAfter(userId, date);
        for (Reservation reservation : reservations) {
            if (reservation.getFacility().getPictures() == null || reservation.getFacility().getPictures().isEmpty()) {
                List<Picture> pics = new ArrayList<>();
                Picture picture = new Picture();
                picture.setId(1);
                picture.setPath("/opt/reservations/backend/images/defaultFacility.png");
                pics.add(picture);
                reservation.getFacility().setPictures(pics);
            }
        }
        return reservations;
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
        return reservationRepository.findByFacilityId(reservation.getFacility().getId());
    }

    // Pending and Rejected Reservations

    @Override
    public Reservation approveReservation(Integer id) {
        PendingAndRejectedReservation pendingReservation = pendingAndRejectedReservationsRepository.findOne(id);
        pendingAndRejectedReservationsRepository.delete(id);
        List<PendingAndRejectedReservation> pendingReservations = pendingAndRejectedReservationsRepository.findReservationsWithTheSameInformation(pendingReservation.getFromHour()
                , pendingReservation.getToHour(), pendingReservation.getDate(), pendingReservation.getFacility().getId(), pendingReservation.getUser().getId());
        for (PendingAndRejectedReservation reservation : pendingReservations) {
            reservation.getReservationStatus().setId(2);
            reservation.getReservationStatus().setStatusType(StatusType.STATUS_REJECTED);
            pendingAndRejectedReservationsRepository.save(reservation);
        }
        Reservation reservation = new Reservation(pendingReservation.getFacility(), pendingReservation.getUser(),
                pendingReservation.getFromHour(), pendingReservation.getToHour(), pendingReservation.getDate(), pendingReservation.getDeposit());
        reservationRepository.save(reservation);
        return reservation;
    }

    @Override
    public List<PendingAndRejectedReservation> findAllPendingAndRejectedReservations() {
        return (List<PendingAndRejectedReservation>) pendingAndRejectedReservationsRepository.findAll();
    }

    @Override
    public List<PendingAndRejectedReservation> findPendingReservationsByUserId(Integer userId) {
        return pendingAndRejectedReservationsRepository.findAllPendingReservationsByUser(userId, StatusType.STATUS_PENDING);
    }

    @Override
    public List<PendingAndRejectedReservation> findPendingReservationsByUserIdAndFacilityId(Integer userId, Integer facilityId) {
        return pendingAndRejectedReservationsRepository.findAllPendingReservationsByUserAndFacility(userId, facilityId, StatusType.STATUS_PENDING);
    }

    @Override
    public PendingAndRejectedReservation savePendingReservation(Reservation reservation) {
        PendingAndRejectedReservation pendingReservation = new PendingAndRejectedReservation(reservation.getFacility(),
                reservation.getUser(), reservation.getFromHour(), reservation.getToHour(), reservation.getDate(),
                reservation.getDeposit(), new ReservationStatus(1, StatusType.STATUS_PENDING));
        return pendingAndRejectedReservationsRepository.save(pendingReservation);

    }

    @Override
    public List<PendingAndRejectedReservation> findPendingReservationsByOwnerId(Integer ownerId) {
        return pendingAndRejectedReservationsRepository.findAllPendingReservationsByOwnerId(ownerId, StatusType.STATUS_PENDING);
    }


}
