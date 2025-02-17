package quizmasters.repository;

import quizmasters.model.Categoria;
import quizmasters.model.Pregunta;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PreguntaRepository extends CrudRepository<Pregunta, Long> {

    List<Pregunta> findByCategoria(Categoria categoria);
    Optional<Pregunta> findByEnunciado(String enunciado);
}
