package ifb.edu.br.repository;

import ifb.edu.br.model.PostoRetirada;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostoRetiradaRepository extends JpaRepository<PostoRetirada, Integer> {

    // Buscar por nome (igual ao padrão do objeto)
    List<PostoRetirada> findByNomeContainingIgnoreCaseOrEnderecoContainingIgnoreCase(
        String nome,
        String endereco
);


}