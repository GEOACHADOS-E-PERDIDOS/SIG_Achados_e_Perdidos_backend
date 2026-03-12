package ifb.edu.br.service;

import ifb.edu.br.model.User;
import ifb.edu.br.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User criarUsuario(User user) {
        user.setDataCadastro(LocalDate.now());
        return userRepository.save(user);
    }

    public List<User> listarTodos() {
        return userRepository.findAll();
    }

    public Optional<User> buscarPorId(Integer id) {
        return userRepository.findById(id);
    }

    public Optional<User> buscarPorEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> buscarPorNome(String name) {
        return userRepository.findByName(name);
    }

    public List<User> buscarPorDataCadastro(LocalDate data) {
        return userRepository.findByDataCadastro(data);
    }

    public List<User> listarAdmins() {
        return userRepository.findByIsAdmin(true);
    }

    public User atualizarUsuario(Integer id, User userAtualizado) {
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