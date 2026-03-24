package ifb.edu.br.controller;

import ifb.edu.br.model.Usuario;
import ifb.edu.br.security.LoginRequest;
import ifb.edu.br.security.UsuarioLogin;
import ifb.edu.br.security.TokenService;
import ifb.edu.br.security.UsuarioDetailsServiceImpl;
import ifb.edu.br.service.UsuarioService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioDetailsServiceImpl userDetailsService;
    private final TokenService tokenService;
    private final UsuarioService userService;

@PostMapping("/login")
public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
    try {
        var authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.email(),
                loginRequest.senha()
        );
        authenticationManager.authenticate(authToken);

        UsuarioLogin loginUser = (UsuarioLogin) userDetailsService.loadUserByUsername(loginRequest.email());
        Usuario user = loginUser.getUser();

        String token = tokenService.gerarToken(
                user.getEmail(),
                Map.of("name", user.getName(), "isAdmin", user.getIsAdmin())
        );

        return ResponseEntity.ok("Login realizado com sucesso! Usuário: " + user.getName() 
                + "\nToken: " + token);

    } catch (AuthenticationException ex) {
        return ResponseEntity.status(401).body("Usuário ou senha inválidos");
    }
}
    @PostMapping("/register")
    public ResponseEntity<String> registro(@RequestBody Usuario user) {
        boolean emailExiste = userService.buscarPorEmail(user.getEmail()).isPresent();
        if (emailExiste) {
            return ResponseEntity.badRequest().body("Email já cadastrado");
        }

        user.setDataCadastro(LocalDate.now());
        user.setIsAdmin(false);
        userService.criarUsuario(user); 

        return ResponseEntity.ok("Usuário registrado com sucesso!");
    }

}