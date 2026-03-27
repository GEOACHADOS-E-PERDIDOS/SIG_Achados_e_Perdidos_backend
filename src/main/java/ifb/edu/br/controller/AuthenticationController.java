package ifb.edu.br.controller;

import ifb.edu.br.dto.LoginRequest;
import ifb.edu.br.dto.NovaSenhaRequest;
import ifb.edu.br.model.Usuario;
import ifb.edu.br.security.UsuarioLogin;
import ifb.edu.br.security.TokenService;
import ifb.edu.br.security.UsuarioDetailsServiceImpl;
import ifb.edu.br.service.UsuarioService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioDetailsServiceImpl userDetailsService;
    private final TokenService tokenService;
    private final UsuarioService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        try {
            var authToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.email(),
                    loginRequest.senha());
            authenticationManager.authenticate(authToken);

            UsuarioLogin loginUser = (UsuarioLogin) userDetailsService.loadUserByUsername(loginRequest.email());
            Usuario user = loginUser.getUser();

            String token = tokenService.gerarToken(
                    user.getEmail(),
                    Map.of("name", user.getName(), "isAdmin", user.getIsAdmin()));

            Map<String, Object> resposta = new HashMap<>();
            resposta.put("mensagem", "Login realizado com sucesso!");
            resposta.put("usuario", user.getName());
            resposta.put("token", token);
            resposta.put("isTemp", user.getSenhaTemporaria());

            return ResponseEntity.ok(resposta);

        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body(Map.of("erro", "Usuário ou senha inválidos"));
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<String> registro(@RequestBody Usuario user) {
        boolean emailExiste = userService.buscarPorEmail(user.getEmail()).isPresent();
        if (emailExiste) {
            return ResponseEntity.badRequest().body("Email já cadastrado");
        }

        user.setDataCadastro(LocalDate.now());
        user.setIsAdmin(false);
        user.setSenhaTemporaria(false);
        userService.criarUsuario(user);

        return ResponseEntity.ok("Usuário registrado com sucesso!");
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<String> recuperarSenha(@RequestParam String email) {
        var usuarioOpt = userService.buscarPorEmail(email);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Email não cadastrado");
        }

        Usuario usuario = usuarioOpt.get();

        String senhaTemp = gerarSenhaTemporaria();
        usuario.setSenhaHash(senhaTemp);
        usuario.setSenhaTemporaria(true);
        userService.atualizarUsuario(usuario.getId(), usuario);
        return ResponseEntity.ok("Senha temporária: " + senhaTemp);
    }

    @PostMapping("/trocar-senha")
    public ResponseEntity<String> trocarSenha(@RequestBody NovaSenhaRequest request,
            @AuthenticationPrincipal UsuarioLogin usuarioLogado) {

        Usuario usuario = usuarioLogado.getUser();

        usuario.setSenhaHash(request.novaSenha());
        usuario.setSenhaTemporaria(false);
        userService.atualizarUsuario(usuario.getId(), usuario);

        return ResponseEntity.ok("Senha atualizada com sucesso!");
    }

    private String gerarSenhaTemporaria() {
        int tamanho = 8;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tamanho; i++) {
            int idx = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(idx));
        }
        return sb.toString();
    }
}