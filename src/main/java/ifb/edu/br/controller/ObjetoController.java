package ifb.edu.br.controller;

import ifb.edu.br.dto.ObjetoAchadoRequest;
import ifb.edu.br.dto.ObjetoPerdidoRequest;
import ifb.edu.br.dto.ObjetoResponse;
import ifb.edu.br.model.Categoria;
import ifb.edu.br.model.Objeto;
import ifb.edu.br.model.ObjetoAchado;
import ifb.edu.br.model.ObjetoPerdido;
import ifb.edu.br.model.StatusObjeto;
import ifb.edu.br.service.MinioService;
import ifb.edu.br.service.ObjetoAchadoService;
import ifb.edu.br.service.ObjetoPerdidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.locationtech.jts.geom.Point;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/objetos")
@RequiredArgsConstructor
public class ObjetoController {

    private final ObjetoAchadoService objetoAchadoService;
    private final ObjetoPerdidoService objetoPerdidoService;
    private final MinioService minioService;

    @PostMapping(value = "/achados", consumes = "multipart/form-data")
    public ResponseEntity<ObjetoResponse> criarAchado(
            @ModelAttribute ObjetoAchadoRequest objetoRequest,
            @RequestParam(value = "imagem", required = false) MultipartFile imagem) {

        ObjetoAchado objeto = new ObjetoAchado();
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

        objeto = objetoAchadoService.salvarComImagem(
                objeto,
                objetoRequest.latitudeAchado(),
                objetoRequest.longitudeAchado(),
                objetoRequest.latitudeAtual(),
                objetoRequest.longitudeAtual(),
                imagem);

        return ResponseEntity.ok(mapToResponseAchado(objeto));
    }

    @PostMapping(value = "/perdidos", consumes = "multipart/form-data")
    public ResponseEntity<ObjetoResponse> criarPerdido(
            @ModelAttribute ObjetoPerdidoRequest objetoRequest,
            @RequestParam(value = "imagem", required = false) MultipartFile imagem) {

        ObjetoPerdido objeto = new ObjetoPerdido();
        objeto.setNome(objetoRequest.nome());
        objeto.setDescricao(objetoRequest.descricao());
        objeto.setEnderecoPerda(objetoRequest.enderecoPerdido());
        objeto.setDataPerda(LocalDate.parse(objetoRequest.dataPerdido()));
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

        objeto = objetoPerdidoService.salvarComImagem(
                objeto,
                objetoRequest.latitude(),
                objetoRequest.longitude(),
                imagem);

        
        return ResponseEntity.ok(mapToResponsePerdido(objeto));
    }

    @GetMapping
    public List<ObjetoResponse> listarTodos() {

        List<ObjetoResponse> achados = objetoAchadoService.listarTodos()
                .stream()
                .map(this::mapToResponseAchado)
                .toList();

        List<ObjetoResponse> perdidos = objetoPerdidoService.listarTodos()
                .stream()
                .map(this::mapToResponsePerdido)
                .toList();

        List<ObjetoResponse> todos = new ArrayList<>();
        todos.addAll(achados);
        todos.addAll(perdidos);
        System.out.println("ACHADOS: " + objetoAchadoService.listarTodos().size());
        System.out.println("PERDIDOS: " + objetoPerdidoService.listarTodos().size());
        return todos;
    }

    @GetMapping("/achados")
    public List<ObjetoAchado> listarAchados() {
        return objetoAchadoService.listarTodos();
    }

    @GetMapping("/perdidos")
    public List<ObjetoPerdido> listarPerdidos() {
        return objetoPerdidoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ObjetoResponse> buscarPorId(@PathVariable Integer id) {

        Optional<ObjetoAchado> achado = objetoAchadoService.buscarPorId(id);
        if (achado.isPresent()) {
            return ResponseEntity.ok(mapToResponseAchado(achado.get()));
        }

        Optional<ObjetoPerdido> perdido = objetoPerdidoService.buscarPorId(id);
        if (perdido.isPresent()) {
            return ResponseEntity.ok(mapToResponsePerdido(perdido.get()));
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/perdidos/{id}")
    public ResponseEntity<ObjetoResponse> atualizarPerdido(
            @PathVariable Integer id,
            @RequestBody ObjetoPerdidoRequest objetoRequest) {
        try {
            ObjetoPerdido objetoAtualizado = new ObjetoPerdido();
            objetoAtualizado.setNome(objetoRequest.nome());
            objetoAtualizado.setDescricao(objetoRequest.descricao());
            objetoAtualizado.setEnderecoPerda(objetoRequest.enderecoPerdido());
            objetoAtualizado.setDataPerda(LocalDate.parse(objetoRequest.dataPerdido()));

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

            ObjetoPerdido atualizado = objetoPerdidoService.atualizar(
                    id,
                    objetoAtualizado,
                    objetoRequest.latitude(),
                    objetoRequest.longitude());

            return ResponseEntity.ok(mapToResponsePerdido(atualizado));

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {

        // tenta deletar achado
        if (objetoAchadoService.buscarPorId(id).isPresent()) {
            objetoAchadoService.deletar(id);
            return ResponseEntity.noContent().build();
        }

        // tenta deletar perdido
        if (objetoPerdidoService.buscarPorId(id).isPresent()) {
            objetoPerdidoService.deletar(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
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

        // 🔹 busca separados
        List<ObjetoAchado> achados = objetoAchadoService.buscar(
                termo, dataConvertida, categoria, status);

        List<ObjetoPerdido> perdidos = objetoPerdidoService.buscar(
                termo, dataConvertida, categoria, status);

        // 🔹 mapeia
        List<ObjetoResponse> resposta = new ArrayList<>();

        resposta.addAll(
                achados.stream()
                        .map(this::mapToResponseAchado)
                        .toList());

        resposta.addAll(
                perdidos.stream()
                        .map(this::mapToResponsePerdido)
                        .toList());

        return ResponseEntity.ok(resposta);
    }

    @GetMapping("achados/buscar/posto/{idPosto}")
    public ResponseEntity<List<ObjetoResponse>> buscarPorPosto(@PathVariable Integer idPosto) {

        List<ObjetoAchado> achados = objetoAchadoService.buscarPorPosto(idPosto);

        List<ObjetoResponse> responses = achados.stream()
                .map(this::mapToResponseAchado)
                .toList();

        return ResponseEntity.ok(responses);
    }

    private ObjetoResponse mapToResponseAchado(ObjetoAchado obj) {
        return new ObjetoResponse(
                obj.getId(),
                obj.getNome(),
                obj.getDescricao(),
                obj.getEnderecoEncontro(),
                obj.getDataEncontro(),
                obj.getImagemObjeto() != null ? obj.getImagemObjeto().getCaminhoImagem() : null,

                obj.getGeomAchado() != null ? obj.getGeomAchado().getY() : null,
                obj.getGeomAchado() != null ? obj.getGeomAchado().getX() : null,

                obj.getGeomAtual() != null ? obj.getGeomAtual().getY() : null,
                obj.getGeomAtual() != null ? obj.getGeomAtual().getX() : null,

                obj.getCategorias(),
                obj.getStatus());
    }

    private ObjetoResponse mapToResponsePerdido(ObjetoPerdido obj) {
        return new ObjetoResponse(
                obj.getId(),
                obj.getNome(),
                obj.getDescricao(),

                obj.getEnderecoPerda(),
                obj.getDataPerda(),

                obj.getImagemObjeto() != null ? obj.getImagemObjeto().getCaminhoImagem() : null,

                // local perda
                obj.getGeomPerdido() != null ? obj.getGeomPerdido().getY() : null,
                obj.getGeomPerdido() != null ? obj.getGeomPerdido().getX() : null,

                // NÃO EXISTE para perdido
                null,
                null,

                obj.getCategorias(),
                obj.getStatus());
    }
}
