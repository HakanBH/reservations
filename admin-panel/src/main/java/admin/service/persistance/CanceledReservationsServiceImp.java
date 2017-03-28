package admin.service.persistance;

import admin.model.CanceledReservations;
import admin.repository.CanceledReservationsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by Trayan_Muchev on 11/10/2016.
 */
public class CanceledReservationsServiceImp implements CanceledReservationsService {

    @Autowired
    CanceledReservationsRepository canceledReservationsRepository;

    @Override
    public List<CanceledReservations> findAll() {
        return (List<CanceledReservations>) canceledReservationsRepository.findAll();
    }
}
