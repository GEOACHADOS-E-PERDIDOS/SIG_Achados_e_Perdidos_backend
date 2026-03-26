package ifb.edu.br.dto;

import java.time.LocalDate;

public record ObjetoResponse(
        Integer id,
        String nome,
        String descricao,
        String enderecoEncontro,
        LocalDate dataEncontro,
        String caminhoImagem,
        Double latitude,
        Double longitude
) {}