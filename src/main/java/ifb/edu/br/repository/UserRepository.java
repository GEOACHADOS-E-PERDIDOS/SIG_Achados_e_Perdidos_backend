package ifb.edu.br.repository;

import ifb.edu.br.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    List<User> findByName(String name);

    List<User> findByDataCadastro(LocalDate dataCadastro);

    List<User> findByIsAdmin(Boolean isaAdmin);
}