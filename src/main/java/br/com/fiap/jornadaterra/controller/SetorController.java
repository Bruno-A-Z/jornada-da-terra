package br.com.fiap.jornadaterra.controller;

import br.com.fiap.jornadaterra.model.Setor;
import br.com.fiap.jornadaterra.service.SetorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/setores")
public class SetorController {

    @Autowired
    private SetorService setorService;

    // POST /setores/ — cria setor vinculado à fazenda
    @PostMapping("/fazenda/{fazendaId}")
    public ResponseEntity<Setor> cadastrar(@PathVariable Long fazendaId,
                                           @Valid @RequestBody Setor setor) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(setorService.cadastrar(setor, fazendaId));
    }

    // GET /setores/ — lista setores da fazenda
    @GetMapping("/fazenda/{fazendaId}")
    public ResponseEntity<List<Setor>> listarPorFazenda(@PathVariable Long fazendaId) {
        return ResponseEntity.ok(setorService.listarPorFazenda(fazendaId));
    }

    // GET /setores/
    @GetMapping("/{id}")
    public ResponseEntity<Setor> buscarPorId(@PathVariable Long id) {
        return setorService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PATCH /setores/ — simula leitura de dados do satélite
    @PatchMapping("/{id}/satelital")
    public ResponseEntity<Setor> atualizarSatelital(@PathVariable Long id,
                                                    @RequestBody Map<String, Double> dados) {
        double temperatura = dados.getOrDefault("temperatura", 20.0);
        double umidade = dados.getOrDefault("umidade", 60.0);
        double ndvi = dados.getOrDefault("ndvi", 0.7);
        return ResponseEntity.ok(setorService.atualizarDadosSatelitais(id, temperatura, umidade, ndvi));
    }

    // PUT /setores/
    @PutMapping("/{id}")
    public ResponseEntity<Setor> atualizar(@PathVariable Long id,
                                           @Valid @RequestBody Setor dados) {
        try {
            return ResponseEntity.ok(setorService.atualizar(id, dados));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /setores/
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            setorService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}