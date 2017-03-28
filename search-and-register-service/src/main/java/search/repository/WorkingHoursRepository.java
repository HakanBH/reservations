package search.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import search.model.WorkingHours;

/**
 * Created by Hakan_Hyusein on 8/3/2016.
 */

@RepositoryRestResource(exported = false)
public interface WorkingHoursRepository extends CrudRepository<WorkingHours, Integer> {
    public WorkingHours findByStartHourAndEndHour(short startHour, short endHour);
}
