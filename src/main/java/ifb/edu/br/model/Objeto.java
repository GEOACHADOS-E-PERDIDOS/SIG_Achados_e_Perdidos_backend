package ifb.edu.br.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusObjeto status;

    @ManyToMany
    @JoinTable(
        name = "Objeto_Categoria",
        joinColumns = @JoinColumn(name = "ID_objeto"),
        inverseJoinColumns = @JoinColumn(name = "ID_categoria")
    )
    private List<Categoria> categorias;

    @OneToMany(
        mappedBy = "objeto",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.EAGER
    )
    @JsonManagedReference
    private List<ImagemObjeto> imagens;
}