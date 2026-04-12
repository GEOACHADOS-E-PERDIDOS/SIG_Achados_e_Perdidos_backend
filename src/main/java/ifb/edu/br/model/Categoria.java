package ifb.edu.br.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "Categoria")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_categoria")
    private Integer id;

    @Column(name = "nome", length = 50)
    private String nome;

    @Column(name = "descricao", length = 150)
    private String descricao;

    @ManyToMany(mappedBy = "categorias")
    @JsonIgnore
    private List<Objeto> objetos;
}