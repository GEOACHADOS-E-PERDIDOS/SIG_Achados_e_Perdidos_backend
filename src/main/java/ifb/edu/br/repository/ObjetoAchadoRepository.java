package ifb.edu.br.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ifb.edu.br.model.ObjetoAchado;
import ifb.edu.br.model.StatusObjeto;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ObjetoAchadoRepository extends JpaRepository<ObjetoAchado, Integer> {

    List<ObjetoAchado> findByNomeContainingIgnoreCase(String nome);

    List<ObjetoAchado> findByDataEncontro(LocalDate dataEncontro);

    List<ObjetoAchado> findByPostoRetirada_Id(Integer idPosto);

    List<ObjetoAchado> findByCategorias_Id(Integer idCategoria);

    @Query("""
                SELECT DISTINCT oa FROM ObjetoAchado oa
                LEFT JOIN oa.categorias c
                WHERE (
                    :termo = '' OR
                    LOWER(oa.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR
                    LOWER(oa.descricao) LIKE LOWER(CONCAT('%', :termo, '%')) OR
                    LOWER(oa.enderecoEncontro) LIKE LOWER(CONCAT('%', :termo, '%'))
                )
                AND oa.dataEncontro = COALESCE(:data, oa.dataEncontro)
                AND c.id = COALESCE(:categoria, c.id)
                AND oa.status = COALESCE(:status, oa.status)
            """)
    List<ObjetoAchado> buscarDinamico(
            @Param("termo") String termo,
            @Param("data") LocalDate data,
            @Param("categoria") Integer categoria,
            @Param("status") StatusObjeto status);

    @Query("""
                SELECT oa FROM ObjetoAchado oa
                WHERE LOWER(oa.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
            """)
    List<ObjetoAchado> buscarParaMapa(@Param("nome") String nome);
}