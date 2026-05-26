package ifb.edu.br.controller;

import ifb.edu.br.dto.LoginRequest;
import ifb.edu.br.dto.NovaSenhaRequest;
import ifb.edu.br.model.Usuario;
import ifb.edu.br.security.UsuarioLogin;
import ifb.edu.br.security.TokenService;
import ifb.edu.br.security.UsuarioDetailsServiceImpl;
import ifb.edu.br.service.EmailService;
import ifb.edu.br.service.UsuarioService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody LoginRequest loginRequest) {

        try {
            var authToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.email(),
                    loginRequest.senha());

            var auth = authenticationManager.authenticate(authToken);

            UsuarioLogin loginUser = (UsuarioLogin) auth.getPrincipal();
            Usuario user = loginUser.getUser();
            String token = tokenService.gerarToken(
                    user.getEmail(),
                    Map.of(
                            "name", user.getName(),
                            "isAdmin", user.getIsAdmin()));

            Map<String, Object> resposta = new HashMap<>();
            resposta.put("mensagem", "Login realizado com sucesso!");

            resposta.put(
                    "usuario",
                    user.getName());

            resposta.put(
                    "token",
                    token);

            resposta.put(
                    "isTemp",
                    user.getSenhaTemporaria());

            return ResponseEntity.ok(resposta);

        } catch (AuthenticationException ex) {

            return ResponseEntity
                    .status(401)
                    .body(Map.of("erro", "Usuário ou senha inválidos"));
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<String> registro(@RequestBody Usuario user) {
        boolean emailExiste = userService.buscarPorEmail(user.getEmail()).isPresent();
        if (emailExiste) {
            return ResponseEntity.badRequest().body("Email já cadastrado");
        }

        user.setDataCadastro(LocalDate.now());
        user.setIsAdmin(false);
        user.setSenhaTemporaria(false);
        user.setSenhaHash(passwordEncoder.encode(user.getSenhaHash()));
        userService.criarUsuario(user);

        return ResponseEntity.ok("Usuário registrado com sucesso!");
    }

@PostMapping("/recuperar-senha")
    public ResponseEntity<String> recuperarSenha(@RequestParam String email) {

        var usuarioOpt = userService.buscarPorEmail(email);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            String senhaTemp = gerarSenhaTemporaria();

            usuario.setSenhaHash(passwordEncoder.encode(senhaTemp));
            usuario.setSenhaTemporaria(true);
            userService.atualizarUsuario(usuario.getId(), usuario);

            String textoEmail = """
                        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #dddddd; border-radius: 5px;">
                            <h2 style="color: #2F80ED; text-align: center;">GeoAchados - Recuperação de Senha</h2>
                            <p>Olá, <strong>%s</strong>!</p>
                            <p>Uma nova senha temporária foi gerada para o seu acesso conforme solicitado.</p>

                            <div style="background-color: #f2f2f2; padding: 15px; border-radius: 5px; text-align: center; margin: 20px 0;">
                                <span style="font-size: 18px; font-family: monospace; letter-spacing: 2px; font-weight: bold; color: #333333;">
                                    %s
                                </span>
                            </div>

                            <p style="color: #ff3b30; font-size: 13px;">
                                * Por segurança, altere esta senha assim que realizar o seu próximo login na plataforma.
                            </p>
                            <hr style="border: 0; border-top: 1px solid #eeeeee; margin: 20px 0;">
                            <p style="font-size: 12px; color: #888888; text-align: center;">Este é um e-mail automático enviado por GeoAchados, por favor não responda.</p>
                        </div>
                    """
                    .formatted(usuario.getName(), senhaTemp);

            emailService.enviarEmail(
                    usuario.getEmail(),
                    "Sua senha temporária - GeoAchados",
                    textoEmail);
        }

        return ResponseEntity.ok("Se o e-mail informado estiver cadastrado em nosso sistema, uma nova senha temporária será enviada em instantes.");
    }

    @PostMapping("/trocar-senha")
    public ResponseEntity<String> trocarSenha(
            @RequestBody NovaSenhaRequest request,
            @AuthenticationPrincipal UsuarioLogin usuarioLogado) {

        Usuario usuario = usuarioLogado.getUser();

        usuario.setSenhaHash(

                passwordEncoder.encode(
                        request.novaSenha())

        );

        usuario.setSenhaTemporaria(
                false);

        userService.atualizarUsuario(
                usuario.getId(),
                usuario);

        return ResponseEntity.ok(
                "Senha atualizada com sucesso!");
    }

    @GetMapping("/admin/check")
    public ResponseEntity<Boolean> checkAdmin(@AuthenticationPrincipal UsuarioLogin usuarioLogado) {
        if (usuarioLogado == null)
            return ResponseEntity.status(401).body(false);

        Usuario user = usuarioLogado.getUser();
        return ResponseEntity.ok(user.getIsAdmin());
    }

    private String gerarSenhaTemporaria() {
        int tamanho = 8;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tamanho; i++) {
            int idx = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(idx));
        }
        return sb.toString();
    }
}