package ifb.edu.br.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "Objeto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Objeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_objeto")
    private Integer id;

    @Column(name = "nome", length = 50)
    private String nome;

    @Column(name = "descricao", length = 50)
    private String descricao;

    @Column(name = "endereco_encontro", length = 150)
    private String enderecoEncontro;

    @Column(name = "data_encontro")
    private LocalDate dataEncontro;

    @ManyToOne
    @JoinColumn(name = "Posto_retirada_ID_posto")
    private PostoRetirada postoRetirada;

    @ManyToOne
    @JoinColumn(name = "Imagem_objeto_ID_imagem")
    private ImagemObjeto imagemObjeto;

    @Column(name = "geom", columnDefinition = "Geometry(Point,4326)")
    private Point geom;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusObjeto status;

    @ManyToMany
    @JoinTable(name = "Objeto_Categoria", joinColumns = @JoinColumn(name = "ID_objeto"), inverseJoinColumns = @JoinColumn(name = "ID_categoria"))
    private List<Categoria> categorias;
}