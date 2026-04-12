package ifb.edu.br.dto;

public record PostoRetiradaResponse(
        Integer id,
        String nome,
        String endereco,
        String telefone,
        String email,
        Double latitude,
        Double longitude
) {}