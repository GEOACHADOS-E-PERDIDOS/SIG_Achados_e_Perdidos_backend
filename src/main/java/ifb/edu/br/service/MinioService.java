package ifb.edu.br.service;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import io.minio.messages.Item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient client;

    @Value("${minio.bucket.name}")
    private String bucketName;

    @PostConstruct
    @SneakyThrows
    public void init() {
        boolean exists = client.bucketExists(
                BucketExistsArgs.builder().bucket(bucketName).build());
        if (!exists) {
            client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    @SneakyThrows
    public List<String> listarObjetos() {
        List<String> objects = new ArrayList<>();
        Iterable<Result<Item>> result = client
                .listObjects(ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .recursive(true)
                        .build());

        for (Result<Item> item : result) {
            objects.add(item.get().objectName());
        }
        return objects;

    }

    @SneakyThrows
    public InputStream buscarObjetoPeloNome(String nomeArquivo) {
        return client.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(nomeArquivo)
                        .build());
    }

    @SneakyThrows
    public void remover(String nomeObjeto) {
        client.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(nomeObjeto)
                        .build());
    }

    @SneakyThrows

    public String uploadArquivo(MultipartFile arquivo) {
        String nomeArquivo = renomearArquivo(arquivo.getOriginalFilename());

        long tamanho = arquivo.getSize();
        InputStream input = arquivo.getInputStream();

        if (tamanho < 5 * 1024 * 1024) { 
            client.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(nomeArquivo)
                            .stream(input, tamanho, -1L) 
                            .contentType(arquivo.getContentType())
                            .build());
        } else {
            client.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(nomeArquivo)
                            .stream(input, tamanho,(long) 5 * 1024 * 1024) 
                            .contentType(arquivo.getContentType())
                            .build());
        }

        return nomeArquivo;
    }

    private String renomearArquivo(String nomeOriginal) {
        if (nomeOriginal != null) {
            return UUID.randomUUID().toString() + "_" + nomeOriginal.trim();
        }
        return null;
    }
}