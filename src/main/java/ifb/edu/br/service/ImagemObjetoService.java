package ifb.edu.br.service;

import ifb.edu.br.model.ImagemObjeto;
import ifb.edu.br.repository.ImagemObjetoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImagemObjetoService {

    private final ImagemObjetoRepository imagemObjetoRepository;
    private final MinioService minioService;

    public ImagemObjeto salvarImagem(MultipartFile arquivo) {

        String nomeArquivo = minioService.uploadArquivo(arquivo);

        ImagemObjeto imagem = ImagemObjeto.builder()
                .caminhoImagem(nomeArquivo)
                .build();

        return imagemObjetoRepository.save(imagem);
    }
}