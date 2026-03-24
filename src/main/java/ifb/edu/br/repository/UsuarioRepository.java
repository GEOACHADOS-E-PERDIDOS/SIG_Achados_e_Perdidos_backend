package ifb.edu.br.repository;

import ifb.edu.br.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByName(String name);

    List<Usuario> findByDataCadastro(LocalDate dataCadastro);

    List<Usuario> findByIsAdmin(Boolean isAdmin);
}