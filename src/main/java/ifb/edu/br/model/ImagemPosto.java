package ifb.edu.br.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Imagem_posto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImagemPosto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_imagem")
    private Integer id;

    @Column(name = "caminho_imagem")
    private String caminhoImagem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_posto")
    @JsonBackReference
    private PostoRetirada posto;
}