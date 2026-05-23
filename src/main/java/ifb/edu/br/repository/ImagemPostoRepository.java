package ifb.edu.br.repository;

import ifb.edu.br.model.ImagemPosto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImagemPostoRepository
        extends JpaRepository<ImagemPosto, Integer> {
}