package ifb.edu.br.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ifb.edu.br.model.Objeto;
import ifb.edu.br.model.StatusObjeto;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ObjetoRepository extends JpaRepository<Objeto, Integer> {

    List<Objeto> findByNomeContainingIgnoreCase(String nome);

    List<Objeto> findByDataEncontro(LocalDate dataEncontro);

    List<Objeto> findByPostoRetirada_Id(Integer idPosto);

    List<Objeto> findByCategorias_Id(Integer idCategoria);

    @Query("""
            SELECT o FROM Objeto o
            LEFT JOIN o.categorias c
            WHERE (:termo IS NULL OR
                   LOWER(o.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR
                   LOWER(o.descricao) LIKE LOWER(CONCAT('%', :termo, '%')) OR
                   LOWER(o.enderecoEncontro) LIKE LOWER(CONCAT('%', :termo, '%')))
            AND (:data IS NULL OR o.dataEncontro = :data)
            AND (:categoria IS NULL OR c.id = :categoria)
            AND (:status IS NULL OR o.status = :status)
            """)
    List<Objeto> buscarDinamico(
            @Param("termo") String termo,
            @Param("data") LocalDate data,
            @Param("categoria") Integer categoria,
            @Param("status") StatusObjeto status);
}