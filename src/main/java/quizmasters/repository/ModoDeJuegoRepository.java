package quizmasters.repository;

import quizmasters.model.ModoDeJuego;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ModoDeJuegoRepository extends CrudRepository<ModoDeJuego, Long> {

    @Override
    Optional<ModoDeJuego> findById(Long aLong);

    Optional<ModoDeJuego> findByNombre(String nombre);

    List<ModoDeJuego> findAll();

}
