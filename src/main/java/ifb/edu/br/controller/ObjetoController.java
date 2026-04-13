package ifb.edu.br.controller;

import ifb.edu.br.dto.ObjetoRequest;
import ifb.edu.br.dto.ObjetoResponse;
import ifb.edu.br.model.Categoria;
import ifb.edu.br.model.Objeto;
import ifb.edu.br.model.StatusObjeto;
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
        objeto.setStatus(StatusObjeto.DISPONIVEL);

        if (objetoRequest.categorias() != null) {
            objeto.setCategorias(
                    objetoRequest.categorias().stream()
                            .map(id -> {
                                Categoria c = new Categoria();
                                c.setId(id);
                                return c;
                            })
                            .toList());
        }

        objeto = objetoService.salvarComImagem(
                objeto,
                objetoRequest.latitude(),
                objetoRequest.longitude(),
                imagem);

        return ResponseEntity.ok(mapToResponse(objeto));
    }

    @GetMapping
    public ResponseEntity<List<ObjetoResponse>> listarTodos() {
        List<Objeto> objetos = objetoService.listarTodos();

        return ResponseEntity.ok(
                objetos.stream()
                        .map(this::mapToResponse)
                        .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObjetoResponse> buscarPorId(@PathVariable Integer id) {
        Optional<Objeto> objetoOpt = objetoService.buscarPorId(id);

        return objetoOpt
                .map(objeto -> ResponseEntity.ok(mapToResponse(objeto)))
                .orElse(ResponseEntity.notFound().build());
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

            if (objetoRequest.categorias() != null) {
                objetoAtualizado.setCategorias(
                        objetoRequest.categorias().stream()
                                .map(idCat -> {
                                    Categoria c = new Categoria();
                                    c.setId(idCat);
                                    return c;
                                })
                                .toList());
            }

            Objeto atualizado = objetoService.atualizar(
                    id,
                    objetoAtualizado,
                    objetoRequest.latitude(),
                    objetoRequest.longitude());

            return ResponseEntity.ok(mapToResponse(atualizado));

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

    @GetMapping("/buscar")
    public ResponseEntity<List<ObjetoResponse>> buscar(
            @RequestParam(required = false) String termo,
            @RequestParam(required = false) String data,
            @RequestParam(required = false) Integer categoria,
            @RequestParam(required = false) StatusObjeto status) {

        LocalDate dataConvertida = (data != null && !data.isEmpty())
                ? LocalDate.parse(data)
                : null;

        List<Objeto> objetos = objetoService.buscar(
                termo,
                dataConvertida,
                categoria,
                status);

        return ResponseEntity.ok(
                objetos.stream().map(this::mapToResponse).toList());
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
                geom != null ? geom.getX() : null,
                objeto.getCategorias() != null ? objeto.getCategorias() : List.of(),
                objeto.getStatus() 
        );
    }
}
