package admin.repository;

import admin.model.WorkingHours;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by Hakan_Hyusein on 8/3/2016.
 */

@RepositoryRestResource(exported = false)
public interface WorkingHoursRepository extends CrudRepository<WorkingHours, Integer> {
    public WorkingHours findByStartHourAndEndHour(short startHour, short endHour);
}
