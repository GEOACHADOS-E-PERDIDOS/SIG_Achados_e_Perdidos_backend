package ifb.edu.br.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import ifb.edu.br.model.Categoria;
import ifb.edu.br.model.StatusObjeto;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ObjetoResponse(
        Integer id,
        String nome,
        String descricao,
        String enderecoEncontro,
        LocalDate dataEncontro,
        String caminhoImagem,
        Double latitudeEncontro,
        Double longitudeEncontro,
        Double latitudeAtual,
        Double longitudeAtual,
        List<Categoria> categorias,
        StatusObjeto status
) {}