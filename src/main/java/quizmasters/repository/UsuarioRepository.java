package quizmasters.repository;

import quizmasters.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String s);

    List<Usuario> findByIsAdmin(boolean isAdmin);
}
