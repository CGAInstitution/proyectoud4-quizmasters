package madstodolist.repository;

import madstodolist.model.Partida;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartidaRepository extends JpaRepository<Partida, Long> {
    List<Partida> findByJoinable(boolean joinable);
    List<Partida> findByFinished(boolean finished);
}
