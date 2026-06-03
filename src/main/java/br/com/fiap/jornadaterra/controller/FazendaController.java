package br.com.fiap.jornadaterra.controller;

import br.com.fiap.jornadaterra.model.Fazenda;
import br.com.fiap.jornadaterra.service.FazendaService;
import br.com.fiap.jornadaterra.service.MissaoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/fazendas")
public class FazendaController {

    @Autowired
    private FazendaService fazendaService;

    @Autowired
    private MissaoService missaoService;

    // POST /fazendas/ — cria fazenda sem produtor
    @PostMapping("/produtor/{produtorId}")
    @Operation(summary = "Cadastra fazenda com um Produtor como proprietario")
    public ResponseEntity<Fazenda> cadastrar(@PathVariable Long produtorId, @Valid @RequestBody Fazenda fazenda) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fazendaService.cadastrar(fazenda, produtorId));
    }

    // PUT /fazendas/ — vincula produtor
    @PutMapping("/{idFazenda}/produtor/{idProdutor}")
    @Operation(summary = "Vincula um produtor à Fazenda")
    public ResponseEntity<Fazenda> vincularProdutor(@PathVariable Long idFazenda, @PathVariable Long idProdutor) {
        return ResponseEntity.ok(fazendaService.vincularProdutor(idFazenda, idProdutor));
    }

    // GET /fazendas/ — lista fazendas do produtor
    @GetMapping("/produtor/{produtorId}")
    @Operation(summary = "Busca Fazenda pelo Id do Produtor")
    public ResponseEntity<List<Fazenda>> listarPorProdutor(@PathVariable Long produtorId) {
        return ResponseEntity.ok(fazendaService.listarPorProdutorId(produtorId));
    }

    // GET /fazendas/
    @GetMapping("/{id}")
    @Operation(summary = "Busca fazenda pelo Id")
    public ResponseEntity<Fazenda> buscarPorId(@PathVariable Long id) {
        return fazendaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /fazendas/
    @PostMapping("/{id}/gerar-missoes")
    @Operation(summary = "Gera Missão")
    public ResponseEntity<Map<String, Object>> gerarMissoes(@PathVariable Long id) {
        Fazenda fazenda = fazendaService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Fazenda não encontrada: " + id));
        var missoes = missaoService.gerarMissoesAutomaticas(fazenda);
        return ResponseEntity.ok(Map.of(
                "mensagem", missoes.size() + " missão(ões) gerada(s)",
                "total", missoes.size()
        ));
    }

    // PUT /fazendas/
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza Informacoes de uma fazenda")
    public ResponseEntity<Fazenda> atualizar(@PathVariable Long id, @Valid @RequestBody Fazenda dados) {
        try {
            return ResponseEntity.ok(fazendaService.atualizar(id, dados));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /fazendas/
    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta Fazenda pelo Id")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            fazendaService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}