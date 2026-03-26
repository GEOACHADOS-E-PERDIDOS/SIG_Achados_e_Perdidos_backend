package ifb.edu.br.model;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "Posto_retirada")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostoRetirada {

    @Id
    @Column(name = "ID_posto")
    private Integer id;

    private String nome;
    private String endereco;
    private String telefone;
    private String email;
    
    @Column(name = "geom", columnDefinition = "Geometry(Point,4326)")
private Point geom;
}