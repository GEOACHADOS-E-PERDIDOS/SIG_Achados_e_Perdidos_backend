package ifb.edu.br.service;

import ifb.edu.br.model.Categoria;
import ifb.edu.br.model.Objeto;
import ifb.edu.br.model.ObjetoPerdido;
import ifb.edu.br.model.StatusObjeto;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ifb.edu.br.repository.CategoriaRepository;
import ifb.edu.br.repository.ObjetoPerdidoRepository;
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

public class ObjetoPerdidoService {

    private final ObjetoPerdidoRepository objetoRepository;
    private final ImagemObjetoService imagemObjetoService;
    private final CategoriaRepository categoriaRepository;

    public ObjetoPerdido salvarComImagem(ObjetoPerdido objeto,
            double latitude,
            double longitude,
            MultipartFile imagem) {

        GeometryFactory geometryFactory = new GeometryFactory();
        Point ponto = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        objeto.setGeomPerdido(ponto);

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

    public List<ObjetoPerdido> listarTodos() {
        return objetoRepository.findAll();
    }

    public Optional<ObjetoPerdido> buscarPorId(Integer id) {
        return objetoRepository.findById(id);
    }

    public ObjetoPerdido atualizar(Integer id, ObjetoPerdido objetoAtualizado, double latitude, double longitude) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Point ponto = geometryFactory.createPoint(new Coordinate(longitude, latitude));

        return objetoRepository.findById(id)
                .map(objeto -> {
                    objeto.setNome(objetoAtualizado.getNome());
                    objeto.setDescricao(objetoAtualizado.getDescricao());
                    objeto.setEnderecoPerda(objetoAtualizado.getEnderecoPerda());
                    objeto.setDataPerda(objetoAtualizado.getDataPerda());
                    objeto.setImagemObjeto(objetoAtualizado.getImagemObjeto());
                    objeto.setGeomPerdido(ponto);

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

    public List<ObjetoPerdido> buscar(String termo, LocalDate data, Integer categoria,StatusObjeto status) {
        termo = (termo == null || termo.isBlank()) ? "" : termo;
        categoria = (categoria == null) ? -1 : categoria;
        return objetoRepository.buscarDinamico(termo, data, categoria,status);

    }


}