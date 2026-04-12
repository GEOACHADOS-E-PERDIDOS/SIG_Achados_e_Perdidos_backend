package ifb.edu.br.dto;

public record PostoRetiradaRequest(
        String nome,
        String endereco,
        String telefone,
        String email,
        Double latitude,
        Double longitude
) {}