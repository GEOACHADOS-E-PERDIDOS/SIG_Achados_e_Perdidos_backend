package ifb.edu.br.dto;

import java.time.LocalDate;
import java.util.List;

import ifb.edu.br.model.Categoria;
import ifb.edu.br.model.StatusObjeto;

public record ObjetoResponse(
        Integer id,
        String nome,
        String descricao,
        String enderecoEncontro,
        LocalDate dataEncontro,
        String caminhoImagem,
        Double latitude,
        Double longitude,
        List<Categoria> categorias,
        StatusObjeto status
) {}