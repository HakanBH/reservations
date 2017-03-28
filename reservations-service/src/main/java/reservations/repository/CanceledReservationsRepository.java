package reservations.repository;

import org.springframework.data.repository.CrudRepository;
import reservations.model.CanceledReservations;

/**
 * Created by Trayan_Muchev on 11/9/2016.
 */
public interface CanceledReservationsRepository extends CrudRepository<CanceledReservations, Integer> {
}
