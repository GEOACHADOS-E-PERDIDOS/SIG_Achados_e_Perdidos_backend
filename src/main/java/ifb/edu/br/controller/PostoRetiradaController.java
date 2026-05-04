package ifb.edu.br.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ifb.edu.br.dto.PostoRetiradaRequest;
import ifb.edu.br.dto.PostoRetiradaResponse;
import ifb.edu.br.model.PostoRetirada;
import ifb.edu.br.service.PostoRetiradaService;

import lombok.RequiredArgsConstructor;

import org.locationtech.jts.geom.Point;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/postos")
@RequiredArgsConstructor
public class PostoRetiradaController {

    private final PostoRetiradaService postoService;

    // ➕ Criar posto
    @PostMapping
    public ResponseEntity<PostoRetiradaResponse> criar(
            @RequestBody PostoRetiradaRequest request) {

        PostoRetirada posto = new PostoRetirada();
        posto.setNome(request.nome());
        posto.setEndereco(request.endereco());
        posto.setTelefone(request.telefone());
        posto.setEmail(request.email());

        posto = postoService.salvar(
                posto,
                request.latitude(),
                request.longitude());

        Point geom = posto.getGeom();

        PostoRetiradaResponse response = new PostoRetiradaResponse(
                posto.getId(),
                posto.getNome(),
                posto.getEndereco(),
                posto.getTelefone(),
                posto.getEmail(),
                geom != null ? geom.getY() : null,
                geom != null ? geom.getX() : null);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PostoRetiradaResponse>> listar(@RequestParam(required = false) String termo) {

    List<PostoRetirada> postos = postoService.buscarPorTermo(termo);

    return ResponseEntity.ok(
            postos.stream()
                .map(this::mapToResponse)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostoRetiradaResponse> buscarPorId(@PathVariable Integer id) {
        Optional<PostoRetirada> postoOpt = postoService.buscarPorId(id);

        return postoOpt.map(posto -> {
            Point geom = posto.getGeom();

            PostoRetiradaResponse response = new PostoRetiradaResponse(
                    posto.getId(),
                    posto.getNome(),
                    posto.getEndereco(),
                    posto.getTelefone(),
                    posto.getEmail(),
                    geom != null ? geom.getY() : null,
                    geom != null ? geom.getX() : null);

            return ResponseEntity.ok(response);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostoRetiradaResponse> atualizar(
            @PathVariable Integer id,
            @RequestBody PostoRetiradaRequest request) {

        try {
            PostoRetirada postoAtualizado = new PostoRetirada();
            postoAtualizado.setNome(request.nome());
            postoAtualizado.setEndereco(request.endereco());
            postoAtualizado.setTelefone(request.telefone());
            postoAtualizado.setEmail(request.email());

            PostoRetirada atualizado = postoService.atualizar(
                    id,
                    postoAtualizado,
                    request.latitude(),
                    request.longitude());

            Point geom = atualizado.getGeom();

            PostoRetiradaResponse response = new PostoRetiradaResponse(
                    atualizado.getId(),
                    atualizado.getNome(),
                    atualizado.getEndereco(),
                    atualizado.getTelefone(),
                    atualizado.getEmail(),
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
            postoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private PostoRetiradaResponse mapToResponse(PostoRetirada posto) {
        Point geom = posto.getGeom();

        return new PostoRetiradaResponse(
                posto.getId(),
                posto.getNome(),
                posto.getEndereco(),
                posto.getTelefone(),
                posto.getEmail(),
                geom != null ? geom.getY() : null,
                geom != null ? geom.getX() : null);
    }
}