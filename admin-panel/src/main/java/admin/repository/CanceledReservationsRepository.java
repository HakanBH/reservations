package admin.repository;

import admin.model.CanceledReservations;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Trayan_Muchev on 11/10/2016.
 */
public interface CanceledReservationsRepository extends CrudRepository<CanceledReservations, Integer> {
}
