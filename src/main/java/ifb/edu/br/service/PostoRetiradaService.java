package ifb.edu.br.service;

import ifb.edu.br.model.PostoRetirada;
import ifb.edu.br.repository.PostoRetiradaRepository;
import ifb.edu.br.model.ImagemPosto;
import ifb.edu.br.repository.ImagemPostoRepository;

import lombok.RequiredArgsConstructor;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class PostoRetiradaService {

    private final PostoRetiradaRepository postoRepository;

    private final ImagemPostoRepository imagemPostoRepository;

    private final ImagemPostoService imagemPostoService;

    // ➕ Salvar posto (com geom)
    public PostoRetirada salvarComImagem(PostoRetirada posto, double latitude, double longitude, List<MultipartFile> imagens) {

            GeometryFactory geometryFactory =
                    new GeometryFactory();

            Point ponto = geometryFactory.createPoint(

                    new Coordinate(longitude, latitude)
            );

            posto.setGeom(ponto);

            /* ====================================== */
            /* SALVA POSTO PRIMEIRO */
            /* ====================================== */

            PostoRetirada postoSalvo =
                    postoRepository.save(posto);

            /* ====================================== */
            /* PROCESSA IMAGENS */
            /* ====================================== */

            List<ImagemPosto> listaImagens =
                    new ArrayList<>();

            if (imagens != null && !imagens.isEmpty()) {

                for (MultipartFile imagem : imagens) {

                    String caminhoImagem =

                            imagemPostoService
                                    .salvarImagemArquivo(imagem);

                    ImagemPosto img =
                            new ImagemPosto();

                    img.setCaminhoImagem(caminhoImagem);

                    img.setPosto(postoSalvo);

                    listaImagens.add(img);
                }

                imagemPostoRepository.saveAll(
                        listaImagens
                );
            }

            postoSalvo.setImagens(listaImagens);

            return postoRepository.save(postoSalvo);
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
    public List<PostoRetirada> buscarPorTermo(String termo) {

    if (termo == null || termo.isBlank()) {
        return postoRepository.findAll();
    }

    return postoRepository.findByNomeContainingIgnoreCaseOrEnderecoContainingIgnoreCase(
            termo,
            termo
    );
    }

}