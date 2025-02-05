package madstodolist.repository;

import madstodolist.model.Categoria;
import madstodolist.model.Pregunta;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PreguntaRepository extends CrudRepository<Pregunta, Long> {

    List<Pregunta> findByCategoria(Categoria categoria);
}
