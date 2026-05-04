package ifb.edu.br.dto;

import java.util.List;

public record ObjetoAchadoRequest(
        String nome,
        String descricao,
        String enderecoEncontro,
        String dataEncontro,
        Integer postoRetiradaId,
        Integer imagemObjetoId,
        Double latitudeAchado,
        Double longitudeAchado,
        List<Integer> categorias 
) {}