package ifb.edu.br.service;

import ifb.edu.br.model.Categoria;
import ifb.edu.br.model.Objeto;
import ifb.edu.br.model.ObjetoAchado;
import ifb.edu.br.model.StatusObjeto;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ifb.edu.br.repository.CategoriaRepository;
import ifb.edu.br.repository.ObjetoAchadoRepository;
import ifb.edu.br.repository.ObjetoAchadoRepository;
import lombok.RequiredArgsConstructor;
import java.util.Optional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Coordinate;

@Service
@RequiredArgsConstructor

public class ObjetoAchadoService {

    private final ObjetoAchadoRepository objetoRepository;
    private final ImagemObjetoService imagemObjetoService;
    private final CategoriaRepository categoriaRepository;

    public ObjetoAchado salvarComImagem(ObjetoAchado objeto,
            double latitudeAchado,
            double longitudeAchado,
            double latitudeAtual,
            double longitudeAtual,
            MultipartFile imagem) {

        GeometryFactory geometryFactory = new GeometryFactory();
        Point pontoAchado = geometryFactory.createPoint(new Coordinate(longitudeAchado, latitudeAchado));
        Point pontoAtual = geometryFactory.createPoint(new Coordinate(longitudeAtual, latitudeAtual));

        objeto.setGeomAchado(pontoAchado);
        objeto.setGeomAtual(pontoAtual);

        if (imagem != null && !imagem.isEmpty()) {
            objeto.setImagemObjeto(
                    imagemObjetoService.salvarImagem(imagem));
        }
        if (objeto.getCategorias() != null && !objeto.getCategorias().isEmpty()) {
            objeto.setCategorias(
                    objeto.getCategorias().stream()
                            .map(cat -> categoriaRepository.findById(cat.getId())
                                    .orElseThrow(
                                            () -> new RuntimeException("Categoria não encontrada: " + cat.getId())))
                            .toList());
        }
        return objetoRepository.save(objeto);
    }

    public List<ObjetoAchado> listarTodos() {
        return objetoRepository.findAll();
    }

    public Optional<ObjetoAchado> buscarPorId(Integer id) {
        return objetoRepository.findById(id);
    }

    public ObjetoAchado atualizar(Integer id,
                            ObjetoAchado objetoAtualizado,
                            double latitudeAchado,
                            double longitudeAchado,
                            double latitudeAtual,
                            double longitudeAtual) {

        GeometryFactory geometryFactory = new GeometryFactory();
        Point pontoAchado = geometryFactory.createPoint(new Coordinate(longitudeAchado, latitudeAchado));
        Point pontoAtual = geometryFactory.createPoint(new Coordinate(longitudeAtual, latitudeAtual));

        return objetoRepository.findById(id)
                .map(objeto -> {
                    objeto.setNome(objetoAtualizado.getNome());
                    objeto.setDescricao(objetoAtualizado.getDescricao());
                    objeto.setEnderecoEncontro(objetoAtualizado.getEnderecoEncontro());
                    objeto.setDataEncontro(objetoAtualizado.getDataEncontro());
                    objeto.setImagemObjeto(objetoAtualizado.getImagemObjeto());
                    objeto.setGeomAchado(pontoAchado);
                    objeto.setGeomAtual(pontoAtual);

                    if (objetoAtualizado.getCategorias() != null && !objetoAtualizado.getCategorias().isEmpty()) {
                        objeto.setCategorias(
                                objetoAtualizado.getCategorias().stream()
                                        .map(cat -> categoriaRepository.findById(cat.getId())
                                                .orElseThrow(() -> new RuntimeException(
                                                        "Categoria não encontrada: " + cat.getId())))
                                        .toList());
                    } else {
                        objeto.setCategorias(new ArrayList<>());
                    }

                    return objetoRepository.save(objeto);
                })
                .orElseThrow(() -> new RuntimeException("Objeto não encontrado com ID: " + id));
    }

    public void deletar(Integer id) {
        if (!objetoRepository.existsById(id)) {
            throw new RuntimeException("Objeto não encontrado com ID: " + id);
        }
        objetoRepository.deleteById(id);
    }

    public List<ObjetoAchado> buscar(String termo, LocalDate data, Integer categoria,StatusObjeto status) {
        termo = (termo == null || termo.isBlank()) ? "" : termo;
        categoria = (categoria == null) ? -1 : categoria;
        return objetoRepository.buscarDinamico(termo, data, categoria,status);

    }

    public List<ObjetoAchado> buscarPorPosto(Integer idPosto) {
        return objetoRepository.findByPostoRetirada_Id(idPosto);
    }

}