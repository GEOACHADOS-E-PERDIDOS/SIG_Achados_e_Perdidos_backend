package ifb.edu.br.dto;

import java.util.List;

public record ObjetoPerdidoRequest(
        String nome,
        String descricao,
        String enderecoPerdido,
        String dataPerdido,
        Integer imagemObjetoId,
        Double latitude,
        Double longitude,
        List<Integer> categorias 
) {}