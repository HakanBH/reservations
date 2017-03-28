package admin.service.persistance;

import admin.model.CanceledReservations;

import java.util.List;

/**
 * Created by Trayan_Muchev on 11/10/2016.
 */
public interface CanceledReservationsService {
    List<CanceledReservations> findAll();
}
