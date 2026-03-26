package ifb.edu.br.controller;

import ifb.edu.br.dto.ObjetoRequest;
import ifb.edu.br.dto.ObjetoResponse;
import ifb.edu.br.model.Objeto;
import ifb.edu.br.service.MinioService;
import ifb.edu.br.service.ObjetoService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.locationtech.jts.geom.Point;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/objetos")
@RequiredArgsConstructor
public class ObjetoController {

    private final ObjetoService objetoService;
    private final MinioService minioService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ObjetoResponse> criar(
            @ModelAttribute ObjetoRequest objetoRequest,
            @RequestParam(value = "imagem", required = false) MultipartFile imagem) {

        Objeto objeto = new Objeto();
        objeto.setNome(objetoRequest.nome());
        objeto.setDescricao(objetoRequest.descricao());
        objeto.setEnderecoEncontro(objetoRequest.enderecoEncontro());
        objeto.setDataEncontro(LocalDate.parse(objetoRequest.dataEncontro()));

        objeto = objetoService.salvarComImagem(
                objeto,
                objetoRequest.latitude(),
                objetoRequest.longitude(),
                imagem);
        Point geom = objeto.getGeom();

        ObjetoResponse response = new ObjetoResponse(
                objeto.getId(),
                objeto.getNome(),
                objeto.getDescricao(),
                objeto.getEnderecoEncontro(),
                objeto.getDataEncontro(),
                objeto.getImagemObjeto() != null ? objeto.getImagemObjeto().getCaminhoImagem() : null,
                geom != null ? geom.getY() : null,
                geom != null ? geom.getX() : null);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ObjetoResponse>> listarTodos() {
        List<Objeto> objetos = objetoService.listarTodos();

        List<ObjetoResponse> responses = objetos.stream()
                .map(objeto -> {
                    Point geom = objeto.getGeom();
                    return new ObjetoResponse(
                            objeto.getId(),
                            objeto.getNome(),
                            objeto.getDescricao(),
                            objeto.getEnderecoEncontro(),
                            objeto.getDataEncontro(),
                            objeto.getImagemObjeto() != null ? objeto.getImagemObjeto().getCaminhoImagem() : null,
                            geom != null ? geom.getY() : null,
                            geom != null ? geom.getX() : null);
                })
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObjetoResponse> buscarPorId(@PathVariable Integer id) {
        Optional<Objeto> objetoOpt = objetoService.buscarPorId(id);

        return objetoOpt.map(objeto -> {
            Point geom = objeto.getGeom();
            ObjetoResponse response = new ObjetoResponse(
                    objeto.getId(),
                    objeto.getNome(),
                    objeto.getDescricao(),
                    objeto.getEnderecoEncontro(),
                    objeto.getDataEncontro(),
                    objeto.getImagemObjeto() != null ? objeto.getImagemObjeto().getCaminhoImagem() : null,
                    geom != null ? geom.getY() : null,
                    geom != null ? geom.getX() : null);
            return ResponseEntity.ok(response);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ObjetoResponse> atualizar(
            @PathVariable Integer id,
            @RequestBody ObjetoRequest objetoRequest) {
        try {
            Objeto objetoAtualizado = new Objeto();
            objetoAtualizado.setNome(objetoRequest.nome());
            objetoAtualizado.setDescricao(objetoRequest.descricao());
            objetoAtualizado.setEnderecoEncontro(objetoRequest.enderecoEncontro());
            objetoAtualizado.setDataEncontro(LocalDate.parse(objetoRequest.dataEncontro()));

            Objeto atualizado = objetoService.atualizar(
                    id,
                    objetoAtualizado,
                    objetoRequest.latitude(),
                    objetoRequest.longitude());

            Point geom = atualizado.getGeom();
            ObjetoResponse response = new ObjetoResponse(
                    atualizado.getId(),
                    atualizado.getNome(),
                    atualizado.getDescricao(),
                    atualizado.getEnderecoEncontro(),
                    atualizado.getDataEncontro(),
                    atualizado.getImagemObjeto() != null ? atualizado.getImagemObjeto().getCaminhoImagem() : null,
                    geom != null ? geom.getY() : null,
                    geom != null ? geom.getX() : null);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        try {
            objetoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

@GetMapping(value = "/buscar", params = "nome")
public ResponseEntity<List<ObjetoResponse>> buscarPorNome(@RequestParam String nome) {
    List<Objeto> objetos = objetoService.buscarPorNome(nome);
    List<ObjetoResponse> responses = objetos.stream()
            .map(this::mapToResponse)
            .toList();
    return ResponseEntity.ok(responses);
}

@GetMapping(value = "/buscar", params = "data")
public ResponseEntity<List<ObjetoResponse>> buscarPorData(@RequestParam String data) {
    LocalDate dataConvertida = LocalDate.parse(data);
    List<Objeto> objetos = objetoService.buscarPorData(dataConvertida);
    List<ObjetoResponse> responses = objetos.stream()
            .map(this::mapToResponse)
            .toList();
    return ResponseEntity.ok(responses);
}

@GetMapping("/buscar/posto/{idPosto}")
public ResponseEntity<List<ObjetoResponse>> buscarPorPosto(@PathVariable Integer idPosto) {
    List<Objeto> objetos = objetoService.buscarPorPosto(idPosto);
    List<ObjetoResponse> responses = objetos.stream()
            .map(this::mapToResponse)
            .toList();
    return ResponseEntity.ok(responses);
}

private ObjetoResponse mapToResponse(Objeto objeto) {
    Point geom = objeto.getGeom();
    return new ObjetoResponse(
            objeto.getId(),
            objeto.getNome(),
            objeto.getDescricao(),
            objeto.getEnderecoEncontro(),
            objeto.getDataEncontro(),
            objeto.getImagemObjeto() != null ? objeto.getImagemObjeto().getCaminhoImagem() : null,
            geom != null ? geom.getY() : null,
            geom != null ? geom.getX() : null
    );
}
}
