package ifb.edu.br.security;

import org.jspecify.annotations.Nullable;

public record LoginRequest(String email,String senha) {

}