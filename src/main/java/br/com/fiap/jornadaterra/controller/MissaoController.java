package br.com.fiap.jornadaterra.controller;

import br.com.fiap.jornadaterra.model.missao.Missao;
import br.com.fiap.jornadaterra.service.MissaoService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/missoes")
public class MissaoController {

    @Autowired
    private MissaoService missaoService;

    // GET /missoes/ - Missões ativas do produtor
    @GetMapping("/produtor/{produtorId}")
    @Operation(description = "Lista missoes ativas/incompletas do produtor")
    public ResponseEntity<List<Missao>> listarAtivas(@PathVariable Long produtorId) {
        return ResponseEntity.ok(missaoService.listarMissoesPendentes(produtorId));
    }

    // GET /missoes/ - Histórico completo
    @GetMapping("/produtor/{produtorId}/todas")
    @Operation(description = "Lista Historico de Missoes do produtor")
    public ResponseEntity<List<Missao>> listarTodas(@PathVariable Long produtorId) {
        return ResponseEntity.ok(missaoService.listarTodasMissoes(produtorId));
    }

    // POST /missoes/ - Iniciar missão
    @PostMapping("/{id}/iniciar")
    @Operation(description = "Altera status da missao para Iniciado")
    public ResponseEntity<Map<String, String>> iniciar(@PathVariable Long id,
                                                        @RequestParam Long produtorId) {
        String resultado = missaoService.iniciarMissao(id, produtorId);
        return ResponseEntity.ok(Map.of("mensagem", resultado));
    }

    // POST /missoes/ - Concluir missão e receber pontos
    @PostMapping("/{id}/concluir")
    @Operation(description = "Altera Status da Missao Para Concluido")
    public ResponseEntity<Map<String, String>> concluir(@PathVariable Long id,
                                                         @RequestParam Long produtorId) {
        String resultado = missaoService.concluirMissao(id, produtorId);
        return ResponseEntity.ok(Map.of("mensagem", resultado));
    }

    // POST /missoes/ - Confirmar ação climática
    @PostMapping("/{id}/acao-climatica")
    @Operation(description = "Confirma acao")
    public ResponseEntity<Map<String, String>> confirmarAcao(@PathVariable Long id) {
        String resultado = missaoService.confirmarAcaoClimatica(id);
        return ResponseEntity.ok(Map.of("mensagem", resultado));
    }

    // POST /missoes/ - Registrar verificação de setor
    @PostMapping("/{id}/verificar-setor")
    public ResponseEntity<Map<String, String>> verificarSetor(@PathVariable Long id,
                                                               @RequestParam String nomeSetor) {
        String resultado = missaoService.verificarSetorMonitoramento(id, nomeSetor);
        return ResponseEntity.ok(Map.of("mensagem", resultado));
    }



    // GET /missoes/ - Detalhes de uma missão
    @GetMapping("/{id}")
    @Operation(description = "Busca Missao por ID")
    public ResponseEntity<Missao> buscarPorId(@PathVariable Long id) {
        return missaoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
