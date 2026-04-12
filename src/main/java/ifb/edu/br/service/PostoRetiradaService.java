package ifb.edu.br.service;

import ifb.edu.br.model.PostoRetirada;
import ifb.edu.br.repository.PostoRetiradaRepository;

import lombok.RequiredArgsConstructor;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostoRetiradaService {

    private final PostoRetiradaRepository postoRepository;

    // ➕ Salvar posto (com geom)
    public PostoRetirada salvar(PostoRetirada posto,
                               double latitude,
                               double longitude) {

        GeometryFactory geometryFactory = new GeometryFactory();
        Point ponto = geometryFactory.createPoint(
                new Coordinate(longitude, latitude)
        );

        posto.setGeom(ponto);

        return postoRepository.save(posto);
    }

    // 📋 Listar todos
    public List<PostoRetirada> listarTodos() {
        return postoRepository.findAll();
    }

    // 🔍 Buscar por ID
    public Optional<PostoRetirada> buscarPorId(Integer id) {
        return postoRepository.findById(id);
    }

    // ✏️ Atualizar
    public PostoRetirada atualizar(Integer id,
                                   PostoRetirada postoAtualizado,
                                   double latitude,
                                   double longitude) {

        GeometryFactory geometryFactory = new GeometryFactory();
        Point ponto = geometryFactory.createPoint(
                new Coordinate(longitude, latitude)
        );

        return postoRepository.findById(id)
                .map(posto -> {
                    posto.setNome(postoAtualizado.getNome());
                    posto.setEndereco(postoAtualizado.getEndereco());
                    posto.setTelefone(postoAtualizado.getTelefone());
                    posto.setEmail(postoAtualizado.getEmail());
                    posto.setGeom(ponto);

                    return postoRepository.save(posto);
                })
                .orElseThrow(() -> new RuntimeException("Posto não encontrado com ID: " + id));
    }

    public void deletar(Integer id) {
        if (!postoRepository.existsById(id)) {
            throw new RuntimeException("Posto não encontrado com ID: " + id);
        }
        postoRepository.deleteById(id);
    }

    // 🔍 Buscar por nome
    public List<PostoRetirada> buscarPorNome(String nome) {
        return postoRepository.findByNomeContainingIgnoreCase(nome);
    }

    // 🔍 Buscar por endereço
    public List<PostoRetirada> buscarPorEndereco(String endereco) {
        return postoRepository.findByEnderecoContainingIgnoreCase(endereco);
    }

}