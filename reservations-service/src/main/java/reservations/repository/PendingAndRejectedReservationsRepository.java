package reservations.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import reservations.model.PendingAndRejectedReservation;
import reservations.model.StatusType;

import java.sql.Date;
import java.util.List;

public interface PendingAndRejectedReservationsRepository extends CrudRepository<PendingAndRejectedReservation, Integer> {

    @Query("select P from PendingAndRejectedReservation P inner join P.facility F inner join P.user U where" +
            " P.fromHour = fromHour and P.toHour = toHour and P.date= date and F.id=facilityId and U.id=userId ")
    List<PendingAndRejectedReservation> findReservationsWithTheSameInformation(@Param("fromtHour") Integer fromHour, @Param("toHour")
            Integer toHour, @Param("date") Date date, @Param("facilityId") Integer facilityId, @Param("userId") Integer userId);

    @Query("select P from PendingAndRejectedReservation P inner join P.reservationStatus S inner join P.user U where" +
            " U.id= userId and S.statusType= status ")
    List<PendingAndRejectedReservation> findAllPendingReservationsByUser(@Param("userId") Integer userId, @Param("status") StatusType statusType);


    @Query("select P from PendingAndRejectedReservation P inner join P.reservationStatus S inner join P.user U inner join P.facility F where" +
            " U.id= userId and S.statusType= status and F.id= facilityId ")
    List<PendingAndRejectedReservation> findAllPendingReservationsByUserAndFacility
            (@Param("userId") Integer userId, @Param("facilityId") Integer facilityId, @Param("status") StatusType statusType);

    @Query("select P from PendingAndRejectedReservation P inner join P.reservationStatus S inner join P.facility F" +
            " inner join F.facilityOwner O where O.id= ownerId and S.statusType= status")
    List<PendingAndRejectedReservation> findAllPendingReservationsByOwnerId
            (@Param("ownerId") Integer userId, @Param("status") StatusType statusType);


}
