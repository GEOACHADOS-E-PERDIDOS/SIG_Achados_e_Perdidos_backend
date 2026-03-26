package ifb.edu.br.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Imagem_objeto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImagemObjeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_imagem")
    private Integer id;

    @Column(name = "caminho_imagem")
    private String caminhoImagem;
}