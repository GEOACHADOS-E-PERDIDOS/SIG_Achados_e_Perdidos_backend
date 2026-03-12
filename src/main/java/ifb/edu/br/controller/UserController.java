package ifb.edu.br.controller;

import ifb.edu.br.model.User;
import ifb.edu.br.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User criarUsuario(@RequestBody User user) {
        return userService.criarUsuario(user);
    }

    @GetMapping
    public List<User> listarTodos() {
        return userService.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<User> buscarPorId(@PathVariable Integer id) {
        return userService.buscarPorId(id);
    }

    @GetMapping("/email/{email}")
    public Optional<User> buscarPorEmail(@PathVariable String email) {
        return userService.buscarPorEmail(email);
    }

    @GetMapping("/name/{name}")
    public List<User> buscarPorNome(@PathVariable String name) {
        return userService.buscarPorNome(name);
    }

    @GetMapping("/data/{data}")
    public List<User> buscarPorDataCadastro(@PathVariable String data) {
        LocalDate dataCadastro = LocalDate.parse(data);
        return userService.buscarPorDataCadastro(dataCadastro);
    }

    @GetMapping("/admins")
    public List<User> listarAdmins() {
        return userService.listarAdmins();
    }

    @PutMapping("/{id}")
    public User atualizarUsuario(@PathVariable Integer id, @RequestBody User user) {
        return userService.atualizarUsuario(id, user);
    }

    @DeleteMapping("/{id}")
    public void deletarUsuario(@PathVariable Integer id) {
        userService.deletarUsuario(id);
    }
}