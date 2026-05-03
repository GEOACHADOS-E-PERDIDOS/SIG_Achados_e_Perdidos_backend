package ifb.edu.br.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ifb.edu.br.model.Objeto;
import ifb.edu.br.model.ObjetoPerdido;
import ifb.edu.br.model.StatusObjeto;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ObjetoPerdidoRepository extends JpaRepository<ObjetoPerdido, Integer> {

    List<ObjetoPerdido> findByNomeContainingIgnoreCase(String nome);

    List<ObjetoPerdido> findByDataPerda(LocalDate dataPerda);

    List<ObjetoPerdido> findByCategorias_Id(Integer idCategoria);

    @Query("""
                SELECT DISTINCT op FROM ObjetoPerdido op
                LEFT JOIN op.categorias c
                WHERE (
                    :termo = '' OR
                    LOWER(op.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR
                    LOWER(op.descricao) LIKE LOWER(CONCAT('%', :termo, '%')) OR
                    LOWER(op.enderecoPerda) LIKE LOWER(CONCAT('%', :termo, '%'))
                )
                AND (
                    :data IS NULL OR op.dataPerda = :data
                )
                AND (
                    :categoria = -1 OR c.id = :categoria
                )
                AND (
                    :status IS NULL OR op.status = :status
                )
            """)
    List<ObjetoPerdido> buscarDinamico(
            @Param("termo") String termo,
            @Param("data") LocalDate data,
            @Param("categoria") Integer categoria,
            @Param("status") StatusObjeto status);
}