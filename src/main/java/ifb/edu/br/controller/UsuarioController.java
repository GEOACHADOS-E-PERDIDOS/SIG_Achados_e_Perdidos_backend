package ifb.edu.br.controller;

import ifb.edu.br.model.Usuario;
import ifb.edu.br.service.UsuarioService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService userService;

    @PostMapping
    public ResponseEntity<String> criarUsuario(@RequestBody Usuario user) {
        boolean emailExiste = userService.buscarPorEmail(user.getEmail()).isPresent();
        if (emailExiste) {
            return ResponseEntity.badRequest().body("Email já cadastrado");
        }

        user.setDataCadastro(LocalDate.now());
        user.setIsAdmin(false);
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

    @DeleteMapping("/{id}")
    public void deletarUsuario(@PathVariable Integer id) {
        userService.deletarUsuario(id);
    }
}