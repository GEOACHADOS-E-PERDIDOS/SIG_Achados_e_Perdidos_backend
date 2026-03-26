package ifb.edu.br.dto;

public record ObjetoRequest(
        String nome, String descricao, String enderecoEncontro, String dataEncontro,
        Integer postoRetiradaId, Integer imagemObjetoId, Double latitude, Double longitude) {
}