package ifb.edu.br.repository;

import ifb.edu.br.model.ImagemObjeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImagemObjetoRepository extends JpaRepository<ImagemObjeto, Integer> {
}