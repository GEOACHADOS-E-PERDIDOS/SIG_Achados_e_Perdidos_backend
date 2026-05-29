package ifb.edu.br.service;

import ifb.edu.br.model.ImagemObjeto;
import ifb.edu.br.model.ObjetoAchado;
import ifb.edu.br.model.StatusObjeto;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ifb.edu.br.repository.CategoriaRepository;
import ifb.edu.br.repository.ObjetoAchadoRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ObjetoAchadoService {

    private final ObjetoAchadoRepository objetoRepository;
    private final ImagemObjetoService imagemObjetoService;
    private final CategoriaRepository categoriaRepository;

    public ObjetoAchado salvarComImagem(
            ObjetoAchado objeto,
            double latitudeAchado,
            double longitudeAchado,
            List<MultipartFile> imagens) {

        GeometryFactory geometryFactory = new GeometryFactory();

        Point pontoAchado = geometryFactory.createPoint(
                new Coordinate(longitudeAchado, latitudeAchado));

        objeto.setGeomAchado(pontoAchado);

        /* ===================================================== */
        /* IMAGENS */
        /* ===================================================== */

        if (imagens != null && !imagens.isEmpty()) {

            List<ImagemObjeto> listaImagens = imagens.stream()

                    .filter(img -> !img.isEmpty())

                    .map(img -> {

                        String nomeArquivo = imagemObjetoService.salvarImagemArquivo(img);

                        return ImagemObjeto.builder()
                                .caminhoImagem(nomeArquivo)
                                .objeto(objeto)
                                .build();
                    })

                    .collect(Collectors.toList());

            objeto.setImagens(listaImagens);
        }

        /* ===================================================== */
        /* CATEGORIAS */
        /* ===================================================== */

        if (objeto.getCategorias() != null
                && !objeto.getCategorias().isEmpty()) {

            objeto.setCategorias(

                    objeto.getCategorias().stream()

                            .map(cat -> categoriaRepository.findById(cat.getId())

                                    .orElseThrow(() -> new RuntimeException(
                                            "Categoria não encontrada: "
                                                    + cat.getId())))

                            .collect(java.util.stream.Collectors.toList()));
        }

        return objetoRepository.save(objeto);
    }

    public List<ObjetoAchado> listarTodos() {
        return objetoRepository.findAll();
    }

    public Optional<ObjetoAchado> buscarPorId(Integer id) {
        return objetoRepository.findById(id);
    }

    public ObjetoAchado atualizar(
            Integer id,
            ObjetoAchado objetoAtualizado,
            double latitudeAchado,
            double longitudeAchado) {

        GeometryFactory geometryFactory = new GeometryFactory();

        Point pontoAchado = geometryFactory.createPoint(
                new Coordinate(longitudeAchado, latitudeAchado));

        return objetoRepository.findById(id)

                .map(objeto -> {

                    objeto.setNome(objetoAtualizado.getNome());

                    objeto.setDescricao(
                            objetoAtualizado.getDescricao());

                    objeto.setEnderecoEncontro(
                            objetoAtualizado.getEnderecoEncontro());

                    objeto.setDataEncontro(
                            objetoAtualizado.getDataEncontro());

                    objeto.setGeomAchado(pontoAchado);

                    objeto.setPostoRetirada(
                            objetoAtualizado.getPostoRetirada());

                    /* ========================================= */
                    /* CATEGORIAS */
                    /* ========================================= */

                    if (objetoAtualizado.getCategorias() != null
                            && !objetoAtualizado.getCategorias().isEmpty()) {

                        objeto.setCategorias(

                                objetoAtualizado.getCategorias().stream()

                                        .map(cat -> categoriaRepository
                                                .findById(cat.getId())

                                                .orElseThrow(() -> new RuntimeException(
                                                        "Categoria não encontrada: "
                                                                + cat.getId())))

                                        .collect(java.util.stream.Collectors.toList()));

                    } else {

                        objeto.setCategorias(new ArrayList<>());
                    }

                    return objetoRepository.save(objeto);
                })

                .orElseThrow(() -> new RuntimeException(
                        "Objeto não encontrado com ID: " + id));
    }

    public void deletar(Integer id) {

        if (!objetoRepository.existsById(id)) {

            throw new RuntimeException(
                    "Objeto não encontrado com ID: " + id);
        }

        objetoRepository.deleteById(id);
    }

    public List<ObjetoAchado> buscar(
            String termo,
            LocalDate data,
            Integer categoria,
            StatusObjeto status) {

        termo = (termo == null || termo.isBlank())
                ? ""
                : termo;

        return objetoRepository.buscarDinamico(
                termo,
                data,
                categoria,
                status);
    }

    public List<ObjetoAchado> buscarPorPosto(Integer idPosto) {
        return objetoRepository.findByPostoRetirada_Id(idPosto);
    }

    public List<ObjetoAchado> buscarParaMapa(String nome) {
        return objetoRepository.buscarParaMapa(nome);
    }

    public long contarObjetosPosto(
            Integer postoId) {
        return objetoRepository
                .contarObjetosPorPosto(postoId);
    }

    public List<ObjetoAchado> buscarPorUsuario(Integer idUsuario) {
        return objetoRepository.findByUsuario_Id(idUsuario);
    }

    public ObjetoAchado atualizarStatus(
                Integer idObjeto,
                Integer idUsuario,
                StatusObjeto novoStatus) {

        ObjetoAchado objeto = objetoRepository
                .findById(idObjeto)

                .orElseThrow(() ->
                        new RuntimeException(
                                "Objeto não encontrado"));

        // verifica se o objeto pertence ao usuário
        if (!objeto.getUsuario().getId().equals(idUsuario)) {

                throw new RuntimeException(
                        "Você não pode alterar este objeto");
        }

        objeto.setStatus(novoStatus);

        return objetoRepository.save(objeto);
        }
}