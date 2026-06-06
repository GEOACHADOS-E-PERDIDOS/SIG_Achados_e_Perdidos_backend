package ifb.edu.br.controller;

import ifb.edu.br.model.Usuario;
import ifb.edu.br.service.UsuarioService;
import lombok.RequiredArgsConstructor;

import ifb.edu.br.security.UsuarioLogin;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<String> criarUsuario(@RequestBody Usuario user) {
        boolean emailExiste = userService.buscarPorEmail(user.getEmail()).isPresent();
        if (emailExiste) {
            return ResponseEntity.badRequest().body("Email já cadastrado");
        }

        user.setDataCadastro(LocalDate.now());
        user.setIsAdmin(false);
        user.setSenhaTemporaria(false);
        user.setIsPosto(false);
        userService.criarUsuario(user);

        return ResponseEntity.ok("Usuário registrado com sucesso!");
    }

    @GetMapping
    public List<Usuario> listarTodos() {
        return userService.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Usuario> buscarPorId(@PathVariable Integer id) {
        return userService.buscarPorId(id);
    }

    @GetMapping("/email/{email}")
    public Optional<Usuario> buscarPorEmail(@PathVariable String email) {
        return userService.buscarPorEmail(email);
    }

    @GetMapping("/name/{name}")
    public List<Usuario> buscarPorNome(@PathVariable String name) {
        return userService.buscarPorNome(name);
    }

    @GetMapping("/data/{data}")
    public List<Usuario> buscarPorDataCadastro(@PathVariable String data) {
        LocalDate dataCadastro = LocalDate.parse(data);
        return userService.buscarPorDataCadastro(dataCadastro);
    }

    @GetMapping("/admins")
    public List<Usuario> listarAdmins() {
        return userService.listarAdmins();
    }

    @PutMapping("/{id}")
    public Usuario atualizarUsuario(@PathVariable Integer id, @RequestBody Usuario user) {
        return userService.atualizarUsuario(id, user);
    }

    @PutMapping("/{id}/tornar-admin")
    public ResponseEntity<String> tornarAdmin(
            @PathVariable Integer id) {

        userService.tornarAdmin(id);

        return ResponseEntity.ok(
                "Usuário promovido para admin com sucesso!");
    }

    @PutMapping("/{id}/tornar-posto")
    public ResponseEntity<String> tornarPosto(
            @PathVariable Integer id) {

        userService.tornarUsuarioPosto(id);

        return ResponseEntity.ok(
                "Usuário promovido para posto com sucesso!");
    }

    @DeleteMapping("/{id}")
    public void deletarUsuario(@PathVariable Integer id) {
        userService.deletarUsuario(id);
    }

    @PutMapping("/{id}/resetar-senha")
    public ResponseEntity<String> resetarSenha(
            @PathVariable Integer id) {
        Optional<Usuario> usuarioOpt = userService.buscarPorId(id);

        if (usuarioOpt.isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Usuário não encontrado");
        }
        Usuario usuario = usuarioOpt.get();
        String senhaTemp = gerarSenhaTemporaria();
        usuario.setSenhaHash(
                passwordEncoder.encode(
                        senhaTemp));
        usuario.setSenhaTemporaria(
                true);
        userService.atualizarUsuario(
                usuario.getId(),
                usuario);
        return ResponseEntity.ok(
                "Senha temporária: "
                        + senhaTemp);
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

    @GetMapping("/me")
    public ResponseEntity<Usuario> meuPerfil(
            @AuthenticationPrincipal UsuarioLogin usuarioLogado) {

        if (usuarioLogado == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(usuarioLogado.getUser());
    }

    @PutMapping("/me")
    public ResponseEntity<Usuario> atualizarMeuPerfil(
            @AuthenticationPrincipal UsuarioLogin usuarioLogado,
            @RequestBody Usuario user) {

        if (usuarioLogado == null) {
            return ResponseEntity.status(401).build();
        }

        Usuario atual = usuarioLogado.getUser();

        atual.setName(user.getName());
        atual.setEmail(user.getEmail());

        return ResponseEntity.ok(userService.atualizarUsuario(atual.getId(), atual));
    }
}