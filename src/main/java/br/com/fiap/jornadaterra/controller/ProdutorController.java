package br.com.fiap.jornadaterra.controller;

import br.com.fiap.jornadaterra.model.Produtor;
import br.com.fiap.jornadaterra.service.ProdutorService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jdk.jfr.Description;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/produtores")
public class ProdutorController {

    @Autowired
    private ProdutorService produtorService;

    // POST /produtores - Cadastrar produtor
    @PostMapping
    @Operation(description = "Cadastra produtor")
    public ResponseEntity<Produtor> cadastrar(@Valid @RequestBody Produtor produtor) {
        Produtor salvo = produtorService.cadastrar(produtor);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // GET /produtores - Listar todos
    @GetMapping
    @Operation(description = "Lista todos os Produtores")
    public ResponseEntity<List<Produtor>> listarTodos() {
        return ResponseEntity.ok(produtorService.listarTodos());
    }

    // GET /produtores/ - Buscar por ID
    @GetMapping("/{id}")
    @Operation(description = "Busca produtor pelo id")
    public ResponseEntity<Produtor> buscarPorId(@PathVariable Long id) {
        return produtorService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /produtores/ - Perfil gamificado
    @GetMapping("/{id}/perfil")
    @Operation(description = "Mostra perfil 'gamificado' do Produtor pelo id")
    public Object perfil(@PathVariable Long id) {
        return produtorService.buscarPorId(id)
                .map(p -> ResponseEntity.ok(Map.of(
                        "nome",    p.getNome(),
                        "nivel",   p.getNivel(),
                        "titulo",  p.getNomeNivel(),
                        "pontos",  p.getPontos()
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /produtores/ - Atualizar
    @PutMapping("/{id}")
    @Operation(description = "Atualiza Produtor pelo ID")
    public ResponseEntity<Produtor> atualizar(@PathVariable Long id,
                                               @Valid @RequestBody Produtor produtor) {
        try {
            return ResponseEntity.ok(produtorService.atualizar(id, produtor));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /produtores/ - Deletar
    @DeleteMapping("/{id}")
    @Operation(description = "Deleta por Id")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            produtorService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
