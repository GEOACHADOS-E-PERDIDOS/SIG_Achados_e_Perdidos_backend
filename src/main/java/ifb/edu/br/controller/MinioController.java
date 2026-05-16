package ifb.edu.br.controller;

import ifb.edu.br.service.MinioService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/uploads")
@RequiredArgsConstructor
public class MinioController {

    private final MinioService minioService;

    @GetMapping
    public ResponseEntity<List<String>> listarObjetos() {

        List<String> objetos =
                minioService.listarObjetos();

        return ResponseEntity.ok(objetos);
    }

    @GetMapping("/{nomeArquivo}")
    public ResponseEntity<byte[]> buscarArquivo(
            @PathVariable String nomeArquivo)
            throws IOException {

        try {

            byte[] arquivo = minioService
                    .buscarObjetoPeloNome(nomeArquivo)
                    .readAllBytes();

            String contentType =
                    URLConnection.guessContentTypeFromName(
                            nomeArquivo);

            if (contentType == null) {
                contentType =
                        MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ResponseEntity.ok()

                    .header(
                            HttpHeaders.CONTENT_TYPE,
                            contentType)

                    .body(arquivo);

        } catch (IOException e) {

            throw new IOException(
                    "Erro ao ler arquivo: "
                            + e.getMessage());
        }
    }

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String, String>> uploadArquivo(
            @RequestPart("arquivo") MultipartFile arquivo) {

        String nomeSalvo =
                minioService.uploadArquivo(arquivo);

        return ResponseEntity.ok(
                Map.of("arquivo", nomeSalvo));
    }

    @DeleteMapping("/{nomeArquivo}")
    public ResponseEntity<Void> removerArquivo(
            @PathVariable String nomeArquivo) {

        minioService.remover(nomeArquivo);

        return ResponseEntity.noContent().build();
    }
}