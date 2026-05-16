package ifb.edu.br.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImagemObjetoService {

    private final MinioService minioService;

    public String salvarImagemArquivo(MultipartFile arquivo) {

        return minioService.uploadArquivo(arquivo);
    }
}