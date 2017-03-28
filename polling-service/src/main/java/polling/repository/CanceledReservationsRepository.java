package polling.repository;

import org.springframework.data.repository.CrudRepository;
import polling.model.CanceledReservations;

/**
 * Created by Toncho_Petrov on 1/4/2017.
 */
public interface CanceledReservationsRepository extends CrudRepository<CanceledReservations, Integer> {
}
