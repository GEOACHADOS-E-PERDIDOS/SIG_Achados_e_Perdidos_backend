package ifb.edu.br.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ifb.edu.br.model.Objeto;
import ifb.edu.br.model.ObjetoAchado;
import ifb.edu.br.model.ObjetoPerdido;
import ifb.edu.br.model.StatusObjeto;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ObjetoAchadoRepository extends JpaRepository<ObjetoAchado, Integer> {

    List<ObjetoAchado> findByNomeContainingIgnoreCase(String nome);

    List<ObjetoAchado> findByDataEncontro(LocalDate dataEncontro);

    List<ObjetoAchado> findByPostoRetirada_Id(Integer idPosto);

    List<ObjetoPerdido> findByCategorias_Id(Integer idCategoria);

    @Query("""
                SELECT DISTINCT oa FROM ObjetoAchado oa
                LEFT JOIN oa.categorias c
                WHERE (
                    :termo = '' OR
                    LOWER(oa.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR
                    LOWER(oa.descricao) LIKE LOWER(CONCAT('%', :termo, '%')) OR
                    LOWER(oa.enderecoEncontro) LIKE LOWER(CONCAT('%', :termo, '%'))
                )
                AND (
                    :data IS NULL OR oa.dataEncontro = :data
                )
                AND (
                    :categoria = -1 OR c.id = :categoria
                )
                AND (
                    :status IS NULL OR oa.status = :status
                )
            """)
    List<ObjetoAchado> buscarDinamico(
            @Param("termo") String termo,
            @Param("data") LocalDate data,
            @Param("categoria") Integer categoria,
            @Param("status") StatusObjeto status);
}