package ifb.edu.br.service;

import ifb.edu.br.model.Usuario;
import ifb.edu.br.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository userRepository;

    public UsuarioService(UsuarioRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Usuario criarUsuario(Usuario user) {
        user.setDataCadastro(LocalDate.now());
        return userRepository.save(user);
    }

    public List<Usuario> listarTodos() {
        return userRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Integer id) {
        return userRepository.findById(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<Usuario> buscarPorNome(String name) {
        return userRepository.findByName(name);
    }

    public List<Usuario> buscarPorDataCadastro(LocalDate data) {
        return userRepository.findByDataCadastro(data);
    }

    public List<Usuario> listarAdmins() {
        return userRepository.findByIsAdmin(true);
    }

    public Usuario atualizarUsuario(Integer id, Usuario userAtualizado) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setName(userAtualizado.getName());
                    user.setEmail(userAtualizado.getEmail());
                    user.setSenhaHash(userAtualizado.getSenhaHash());
                    user.setIsAdmin(userAtualizado.getIsAdmin());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public void deletarUsuario(Integer id) {
        userRepository.deleteById(id);
    }
}