package ifb.edu.br.model; 

import jakarta.persistence.*; 
import lombok.*;
import java.time.LocalDate;
import org.locationtech.jts.geom.Point;


@Entity
@Table(name = "objeto_perdido")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ObjetoPerdido extends Objeto {

    @Column(name = "endereco_perda", length = 150)
    private String enderecoPerda;

    @Column(name = "data_perda")
    private LocalDate dataPerda;

    @Column(name = "geom_perdido", columnDefinition = "Geometry(Point,4326)")
    private Point geomPerdido;
}