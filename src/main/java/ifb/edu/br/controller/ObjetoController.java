package ifb.edu.br.controller;

import ifb.edu.br.dto.ObjetoAchadoRequest;
import ifb.edu.br.dto.ObjetoPerdidoRequest;
import ifb.edu.br.dto.ObjetoResponse;

import ifb.edu.br.model.Categoria;
import ifb.edu.br.model.ObjetoAchado;
import ifb.edu.br.model.ObjetoPerdido;
import ifb.edu.br.model.StatusObjeto;
import ifb.edu.br.model.PostoRetirada;

import ifb.edu.br.service.ObjetoAchadoService;
import ifb.edu.br.service.ObjetoPerdidoService;
import ifb.edu.br.service.PostoRetiradaService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ifb.edu.br.model.Usuario;
import ifb.edu.br.security.UsuarioLogin;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/objetos")
@RequiredArgsConstructor
public class ObjetoController {

        private final ObjetoAchadoService objetoAchadoService;
        private final ObjetoPerdidoService objetoPerdidoService;
        private final PostoRetiradaService postoRetiradaService;

        /* ===================================================== */
        /* CRIAR OBJETO ACHADO */
        /* ===================================================== */

        @PostMapping(value = "/achados", consumes = "multipart/form-data")
        public ResponseEntity<ObjetoResponse> criarAchado(

                        @AuthenticationPrincipal UsuarioLogin usuarioLogado,

                        @ModelAttribute ObjetoAchadoRequest objetoRequest,

                        @RequestParam(value = "imagens", required = false) List<MultipartFile> imagens) {

                ObjetoAchado objeto = new ObjetoAchado();

                Usuario usuario = usuarioLogado.getUser();

                objeto.setUsuario(usuario);

                objeto.setNome(objetoRequest.nome());

                objeto.setDescricao(objetoRequest.descricao());

                objeto.setEnderecoEncontro(
                                objetoRequest.enderecoEncontro());

                objeto.setDataEncontro(
                                LocalDate.parse(objetoRequest.dataEncontro()));

                PostoRetirada posto = postoRetiradaService
                                .buscarPorId(objetoRequest.postoRetiradaId())

                                .orElseThrow(() -> new RuntimeException(
                                                "Posto não encontrado"));

                objeto.setPostoRetirada(posto);

                objeto.setStatus(StatusObjeto.DISPONIVEL);

                if (objetoRequest.categorias() != null) {

                        objeto.setCategorias(

                                        objetoRequest.categorias().stream()

                                                        .map(id -> {

                                                                Categoria c = new Categoria();

                                                                c.setId(id);

                                                                return c;
                                                        })

                                                        .collect(Collectors.toList()));
                }

                objeto = objetoAchadoService.salvarComImagem(

                                objeto,

                                objetoRequest.latitudeAchado(),

                                objetoRequest.longitudeAchado(),

                                imagens);

                return ResponseEntity.ok(
                                mapToResponseAchado(objeto));
        }

        /* ===================================================== */
        /* CRIAR OBJETO PERDIDO */
        /* ===================================================== */

        @PostMapping(value = "/perdidos", consumes = "multipart/form-data")
        public ResponseEntity<ObjetoResponse> criarPerdido(

                        @AuthenticationPrincipal UsuarioLogin usuarioLogado,

                        @ModelAttribute ObjetoPerdidoRequest objetoRequest,

                        @RequestParam(value = "imagens", required = false) List<MultipartFile> imagens) {

                ObjetoPerdido objeto = new ObjetoPerdido();

                Usuario usuario = usuarioLogado.getUser();

                objeto.setUsuario(usuario);

                objeto.setNome(objetoRequest.nome());

                objeto.setDescricao(objetoRequest.descricao());

                objeto.setEnderecoPerda(
                                objetoRequest.enderecoPerdido());

                objeto.setDataPerda(
                                LocalDate.parse(objetoRequest.dataPerdido()));

                objeto.setStatus(StatusObjeto.PERDIDO);

                if (objetoRequest.categorias() != null) {

                        objeto.setCategorias(

                                        objetoRequest.categorias().stream()

                                                        .map(id -> {

                                                                Categoria c = new Categoria();

                                                                c.setId(id);

                                                                return c;
                                                        })

                                                        .collect(Collectors.toList()));
                }

                objeto = objetoPerdidoService.salvarComImagem(

                                objeto,

                                objetoRequest.latitude(),

                                objetoRequest.longitude(),

                                imagens);

                return ResponseEntity.ok(
                                mapToResponsePerdido(objeto));
        }

        /* ===================================================== */
        /* LISTAR TODOS */
        /* ===================================================== */

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

                return todos;
        }

        /* ===================================================== */
        /* LISTAR ACHADOS */
        /* ===================================================== */

        @GetMapping("/achados")
        public List<ObjetoAchado> listarAchados() {
                return objetoAchadoService.listarTodos();
        }

        /* ===================================================== */
        /* LISTAR PERDIDOS */
        /* ===================================================== */

        @GetMapping("/perdidos")
        public List<ObjetoPerdido> listarPerdidos() {
                return objetoPerdidoService.listarTodos();
        }

        /* ===================================================== */
        /* BUSCAR POR ID */
        /* ===================================================== */

        @GetMapping("/{id}")
        public ResponseEntity<ObjetoResponse> buscarPorId(
                        @PathVariable Integer id) {

                Optional<ObjetoAchado> achado = objetoAchadoService.buscarPorId(id);

                if (achado.isPresent()) {

                        return ResponseEntity.ok(
                                        mapToResponseAchado(achado.get()));
                }

                Optional<ObjetoPerdido> perdido = objetoPerdidoService.buscarPorId(id);

                if (perdido.isPresent()) {

                        return ResponseEntity.ok(
                                        mapToResponsePerdido(perdido.get()));
                }

                return ResponseEntity.notFound().build();
        }

        /* ===================================================== */
        /* ATUALIZAR PERDIDO */
        /* ===================================================== */

        @PutMapping("/perdidos/{id}")
        public ResponseEntity<ObjetoResponse> atualizarPerdido(

                        @PathVariable Integer id,

                        @RequestBody ObjetoPerdidoRequest objetoRequest) {

                try {

                        ObjetoPerdido objetoAtualizado = new ObjetoPerdido();

                        objetoAtualizado.setNome(
                                        objetoRequest.nome());

                        objetoAtualizado.setDescricao(
                                        objetoRequest.descricao());

                        objetoAtualizado.setEnderecoPerda(
                                        objetoRequest.enderecoPerdido());

                        objetoAtualizado.setDataPerda(
                                        LocalDate.parse(
                                                        objetoRequest.dataPerdido()));

                        if (objetoRequest.categorias() != null) {

                                objetoAtualizado.setCategorias(

                                                objetoRequest.categorias().stream()

                                                                .map(idCat -> {

                                                                        Categoria c = new Categoria();

                                                                        c.setId(idCat);

                                                                        return c;
                                                                })

                                                                .collect(Collectors.toList()));
                        }

                        ObjetoPerdido atualizado = objetoPerdidoService.atualizar(

                                        id,

                                        objetoAtualizado,

                                        objetoRequest.latitude(),

                                        objetoRequest.longitude());

                        return ResponseEntity.ok(
                                        mapToResponsePerdido(atualizado));

                } catch (RuntimeException e) {

                        return ResponseEntity.notFound().build();
                }
        }

        @PutMapping("/achados/{id}")
        public ResponseEntity<ObjetoResponse> atualizarAchado(
                        @PathVariable Integer id,
                        @RequestBody ObjetoAchadoRequest objetoRequest) {
                try {
                        ObjetoAchado objetoAtualizado = new ObjetoAchado();
                        objetoAtualizado.setNome(
                                        objetoRequest.nome());
                        objetoAtualizado.setDescricao(
                                        objetoRequest.descricao());
                        objetoAtualizado.setEnderecoEncontro(
                                        objetoRequest.enderecoEncontro());
                        if (objetoRequest.dataEncontro() != null &&
                                        !objetoRequest.dataEncontro().isBlank()) {

                                objetoAtualizado.setDataEncontro(
                                                LocalDate.parse(
                                                                objetoRequest.dataEncontro()));
                        }

                        PostoRetirada posto = postoRetiradaService
                                        .buscarPorId(
                                                        objetoRequest.postoRetiradaId())
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Posto não encontrado"));
                        objetoAtualizado.setPostoRetirada(
                                        posto);
                        if (objetoRequest.categorias() != null) {
                                objetoAtualizado.setCategorias(
                                                objetoRequest.categorias().stream()
                                                                .map(idCat -> {
                                                                        Categoria c = new Categoria();
                                                                        c.setId(idCat);
                                                                        return c;
                                                                })
                                                                .collect(Collectors.toList()));
                        }
                        ObjetoAchado atualizado = objetoAchadoService.atualizar(
                                        id,
                                        objetoAtualizado,
                                        objetoRequest.latitudeAchado(),
                                        objetoRequest.longitudeAchado());

                        return ResponseEntity.ok(
                                        mapToResponseAchado(
                                                        atualizado));

                } catch (Exception e) {

                        e.printStackTrace();

                        return ResponseEntity
                                        .badRequest()
                                        .build();
                }
        }

        /* ===================================================== */
        /* DELETAR */
        /* ===================================================== */

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deletar(
                        @PathVariable Integer id) {

                if (objetoAchadoService.buscarPorId(id).isPresent()) {

                        objetoAchadoService.deletar(id);

                        return ResponseEntity.noContent().build();
                }

                if (objetoPerdidoService.buscarPorId(id).isPresent()) {

                        objetoPerdidoService.deletar(id);

                        return ResponseEntity.noContent().build();
                }

                return ResponseEntity.notFound().build();
        }

        /* ===================================================== */
        /* BUSCA DINÂMICA */
        /* ===================================================== */

        @GetMapping("/buscar")
        public ResponseEntity<List<ObjetoResponse>> buscar(

                        @RequestParam(required = false) String termo,

                        @RequestParam(required = false) String data,

                        @RequestParam(required = false) Integer categoria,

                        @RequestParam(required = false) StatusObjeto status) {

                LocalDate dataConvertida = (data != null && !data.isEmpty())

                                ? LocalDate.parse(data)

                                : null;

                List<ObjetoAchado> achados = objetoAchadoService.buscar(
                                termo,
                                dataConvertida,
                                categoria,
                                status);

                List<ObjetoPerdido> perdidos = objetoPerdidoService.buscar(
                                termo,
                                dataConvertida,
                                categoria,
                                status);

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

        @GetMapping("/mapa/buscar")
        public ResponseEntity<List<ObjetoResponse>> buscarParaMapa(
                        @RequestParam String nome) {

                List<ObjetoAchado> achados = objetoAchadoService.buscarParaMapa(nome);

                List<ObjetoPerdido> perdidos = objetoPerdidoService.buscarParaMapa(nome);

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
        public ResponseEntity<List<ObjetoResponse>> buscarPorPosto(
                        @PathVariable Integer idPosto) {

                List<ObjetoAchado> achados = objetoAchadoService.buscarPorPosto(idPosto);

                List<ObjetoResponse> responses = achados.stream()

                                .map(this::mapToResponseAchado)

                                .toList();

                return ResponseEntity.ok(responses);
        }

        /* ===================================================== */
        /* MAP RESPONSE ACHADO */
        /* ===================================================== */

        private ObjetoResponse mapToResponseAchado(ObjetoAchado obj) {
                return new ObjetoResponse(

                                obj.getId(),
                                obj.getNome(),
                                obj.getDescricao(),
                                obj.getEnderecoEncontro(),
                                obj.getDataEncontro(),
                                obj.getImagens() != null
                                                ? obj.getImagens().stream().map(img -> img.getCaminhoImagem()).toList()
                                                : List.of(),
                                obj.getGeomAchado() != null ? obj.getGeomAchado().getY() : null,
                                obj.getGeomAchado() != null ? obj.getGeomAchado().getX() : null,
                                obj.getCategorias(),
                                obj.getStatus(),
                                obj.getPostoRetirada() != null ? obj.getPostoRetirada().getId() : null);
        }

        /* ===================================================== */
        /* MAP RESPONSE PERDIDO */
        /* ===================================================== */

        private ObjetoResponse mapToResponsePerdido(ObjetoPerdido obj) {
                return new ObjetoResponse(
                                obj.getId(),
                                obj.getNome(),
                                obj.getDescricao(),
                                obj.getEnderecoPerda(),
                                obj.getDataPerda(),
                                obj.getImagens() != null
                                                ? obj.getImagens().stream().map(img -> img.getCaminhoImagem()).toList()
                                                : List.of(),
                                obj.getGeomPerdido() != null ? obj.getGeomPerdido().getY() : null,
                                obj.getGeomPerdido() != null ? obj.getGeomPerdido().getX() : null,
                                obj.getCategorias(),
                                obj.getStatus(),
                                null);
        }

        @GetMapping("/achados/posto/{postoId}/quantidade")
        public ResponseEntity<Long> contarObjetos(
                        @PathVariable Integer postoId) {

                long quantidade = objetoAchadoService.contarObjetosPosto(
                                postoId);

                return ResponseEntity.ok(
                                quantidade);
        }

        @GetMapping("/me")
        public ResponseEntity<List<ObjetoResponse>> meusObjetos(
                        @AuthenticationPrincipal UsuarioLogin usuarioLogado) {

                if (usuarioLogado == null) {
                        return ResponseEntity.status(401).build();
                }

                Usuario usuario = usuarioLogado.getUser();

                List<ObjetoResponse> resposta = new ArrayList<>();

                // Usuário é um posto
                if (Boolean.TRUE.equals(usuario.getIsPosto())) {

                        if (usuario.getPostoRetirada() == null) {
                                return ResponseEntity.badRequest().build();
                        }

                        Integer postoId = usuario.getPostoRetirada().getId();

                        List<ObjetoAchado> achados = objetoAchadoService.buscarPorPosto(
                                        postoId);

                        resposta.addAll(
                                        achados.stream()
                                                        .map(this::mapToResponseAchado)
                                                        .toList());

                        return ResponseEntity.ok(resposta);
                }

                // Usuário comum
                Integer idUsuario = usuario.getId();

                List<ObjetoAchado> achados = objetoAchadoService.buscarPorUsuario(
                                idUsuario);

                List<ObjetoPerdido> perdidos = objetoPerdidoService.buscarPorUsuario(
                                idUsuario);

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

        @PutMapping("/{id}/status")
        public ResponseEntity<?> atualizarStatus(

                        @PathVariable Integer id,

                        @RequestBody Map<String, String> body,

                        @AuthenticationPrincipal UsuarioLogin usuarioLogado) {

                String novoStatus = body.get("status");

                Integer idUsuario = usuarioLogado.getUser().getId();

                try {

                        objetoAchadoService.atualizarStatus(
                                        id,
                                        idUsuario,
                                        StatusObjeto.valueOf(novoStatus));

                        return ResponseEntity.ok().build();

                } catch (RuntimeException e) {

                        try {

                                objetoPerdidoService.atualizarStatus(
                                                id,
                                                idUsuario,
                                                StatusObjeto.valueOf(novoStatus));

                                return ResponseEntity.ok().build();

                        } catch (RuntimeException ex) {

                                return ResponseEntity
                                                .badRequest()
                                                .body(ex.getMessage());
                        }
                }
        }
}