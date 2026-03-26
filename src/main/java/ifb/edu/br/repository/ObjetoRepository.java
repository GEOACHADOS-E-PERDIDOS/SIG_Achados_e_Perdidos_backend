package ifb.edu.br.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ifb.edu.br.model.Objeto;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ObjetoRepository extends JpaRepository<Objeto, Integer> {

    List<Objeto> findByNomeContainingIgnoreCase(String nome);

    // 🔎 Buscar por data de encontro
    List<Objeto> findByDataEncontro(LocalDate dataEncontro);

    // 🔎 Buscar por posto de retirada
    List<Objeto> findByPostoRetirada_Id(Integer idPosto);

}