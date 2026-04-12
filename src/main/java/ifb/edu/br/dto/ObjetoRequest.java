package ifb.edu.br.dto;

import java.util.List;

public record ObjetoRequest(
        String nome,
        String descricao,
        String enderecoEncontro,
        String dataEncontro,
        Integer postoRetiradaId,
        Integer imagemObjetoId,
        Double latitude,
        Double longitude,
        List<Integer> categorias 
) {}