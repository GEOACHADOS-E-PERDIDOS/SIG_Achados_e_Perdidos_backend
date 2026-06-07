package ifb.edu.br.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;


@Entity
@Table(name = "Usuarios")
@Data                   
@NoArgsConstructor      
@AllArgsConstructor     
@Builder                
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_User")
    private Integer id;

    @Column(name = "Name", length = 50, nullable = false)
    private String name;

    @Column(name = "email", length = 50, nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(name = "senha_hash", length = 200, nullable = false)
    private String senhaHash;

    @Column(name = "data_cadastro")
    private LocalDate dataCadastro;

    @Column(name = "is_admin")
    private Boolean isAdmin;

    @JsonIgnore
    @Column(name = "senha_temporaria")
    private Boolean senhaTemporaria;

    @Column(name = "is_posto")
    private Boolean isPosto;

    @ManyToOne
    @JoinColumn(name = "posto_id")
    @JsonIgnore
    private PostoRetirada postoRetirada;

    @JsonProperty("postoId")
    public Integer getPostoId() {
        return postoRetirada != null
                ? postoRetirada.getId()
                : null;
    }
}