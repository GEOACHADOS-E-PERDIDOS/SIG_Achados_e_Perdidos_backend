package ifb.edu.br.model; 

import jakarta.persistence.*; 
import lombok.*;
import java.time.LocalDate;
import org.locationtech.jts.geom.Point;


@Entity
@Table(name = "objeto_achado")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ObjetoAchado extends Objeto {

    @Column(name = "endereco_encontro", length = 150)
    private String enderecoEncontro;

    @Column(name = "data_encontro")
    private LocalDate dataEncontro;

    @ManyToOne
    @JoinColumn(name = "Posto_retirada_ID_posto")
    private PostoRetirada postoRetirada;

    @Column(name = "geom_achado", columnDefinition = "Geometry(Point,4326)")
    private Point geomAchado;

    @Column(name = "geom_atual", columnDefinition = "Geometry(Point,4326)")
    private Point geomAtual;
}