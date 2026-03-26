package ifb.edu.br.service;

import ifb.edu.br.model.Objeto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ifb.edu.br.repository.ObjetoRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Coordinate;

@Service
@RequiredArgsConstructor

public class ObjetoService {

    private final ObjetoRepository objetoRepository;
    private final ImagemObjetoService imagemObjetoService;

    public Objeto salvarComImagem(Objeto objeto,
            double latitude,
            double longitude,
            MultipartFile imagem) {

        GeometryFactory geometryFactory = new GeometryFactory();
        Point ponto = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        objeto.setGeom(ponto);

        if (imagem != null && !imagem.isEmpty()) {
            objeto.setImagemObjeto(
                    imagemObjetoService.salvarImagem(imagem));
        }

        return objetoRepository.save(objeto);
    }

    public List<Objeto> listarTodos() {
        return objetoRepository.findAll();
    }

    public Optional<Objeto> buscarPorId(Integer id) {
        return objetoRepository.findById(id);
    }

    public Objeto atualizar(Integer id, Objeto objetoAtualizado, double latitude, double longitude) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Point ponto = geometryFactory.createPoint(new Coordinate(longitude, latitude));

        return objetoRepository.findById(id)
                .map(objeto -> {
                    objeto.setNome(objetoAtualizado.getNome());
                    objeto.setDescricao(objetoAtualizado.getDescricao());
                    objeto.setEnderecoEncontro(objetoAtualizado.getEnderecoEncontro());
                    objeto.setDataEncontro(objetoAtualizado.getDataEncontro());
                    objeto.setPostoRetirada(objetoAtualizado.getPostoRetirada());
                    objeto.setImagemObjeto(objetoAtualizado.getImagemObjeto());
                    objeto.setGeom(ponto);
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

    public List<Objeto> buscarPorNome(String nome) {
        return objetoRepository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Objeto> buscarPorData(LocalDate data) {
        return objetoRepository.findByDataEncontro(data);
    }

    public List<Objeto> buscarPorPosto(Integer idPosto) {
        return objetoRepository.findByPostoRetirada_Id(idPosto);
    }
}