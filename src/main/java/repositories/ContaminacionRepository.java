package repositories;

import models.LecturaContaminacion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ContaminacionRepository extends MongoRepository<LecturaContaminacion, String>{
    //Query methods
    Optional<LecturaContaminacion> findFirstByZonaIdOrderByFechaHoraDesc(String zonaId);
    List<LecturaContaminacion> findByZonaIdAndFechaHoraBetween(String zonaId, LocalDateTime inicio, LocalDateTime fin);
    List<String> findByZonaIdOrderByFechaHoraAsc(String zonaId);
}
