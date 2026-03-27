package ifb.edu.br.model;

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

//    @JsonIgnore - Implementar isso depois 
    @Column(name = "senha_hash", length = 50, nullable = false)
    private String senhaHash;

    @Column(name = "data_cadastro")
    private LocalDate dataCadastro;

    @Column(name = "is_admin")
    private Boolean isAdmin;

    @Column(name = "senha_temporaria")
    private Boolean senhaTemporaria;
}