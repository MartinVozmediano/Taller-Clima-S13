package repositories;

import models.LecturaContaminacion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ContaminacionRepository extends MongoRepository<LecturaContaminacion, String>{
    Optional<LecturaContaminacion> findFirstbyZonaIdOrderByFechaHoraDesc(String zonaId);  //Query method
}
