package repositories;

import models.Zona;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

public interface ZonaRepository extends MongoRepository<Zona, String>{

    //Se heredará de MongoRepository, que ya tiene métodos para CRUD básico (save, findById, findAll, deleteById, etc.)
}
