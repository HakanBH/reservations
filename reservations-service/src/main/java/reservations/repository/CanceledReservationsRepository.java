package reservations.repository;

import org.springframework.data.repository.CrudRepository;
import reservations.model.CanceledReservations;

public interface CanceledReservationsRepository extends CrudRepository<CanceledReservations, Integer> {
}
