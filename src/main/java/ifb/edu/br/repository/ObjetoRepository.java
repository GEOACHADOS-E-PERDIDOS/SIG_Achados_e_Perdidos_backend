package ifb.edu.br.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ifb.edu.br.model.Objeto;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ObjetoRepository extends JpaRepository<Objeto, Integer> {

    List<Objeto> findByNomeContainingIgnoreCase(String nome);

    List<Objeto> findByDataEncontro(LocalDate dataEncontro);

    List<Objeto> findByPostoRetirada_Id(Integer idPosto);

    List<Objeto> findByCategorias_Id(Integer idCategoria);

}