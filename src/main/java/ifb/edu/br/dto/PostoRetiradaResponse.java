package ifb.edu.br.dto;

import java.util.List;

public record PostoRetiradaResponse(
        Integer id,
        String nome,
        String endereco,
        String telefone,
        String email,
        Double latitude,
        Double longitude,
        List<String> imagens
) {}