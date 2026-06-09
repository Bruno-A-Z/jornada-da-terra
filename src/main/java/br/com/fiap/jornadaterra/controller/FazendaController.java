package br.com.fiap.jornadaterra.controller;

import br.com.fiap.jornadaterra.assembler.FazendaAssembler;
import br.com.fiap.jornadaterra.exception.ResourceNotFoundException;
import br.com.fiap.jornadaterra.model.Fazenda;
import br.com.fiap.jornadaterra.model.Localizacao;
import br.com.fiap.jornadaterra.service.FazendaService;
import br.com.fiap.jornadaterra.service.MissaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Tag(name = "Fazenda", description = "Gerenciar Fazendas")
@RestController
@RequestMapping("/fazendas")
public class FazendaController {

    @Autowired
    private FazendaService fazendaService;

    @Autowired
    private MissaoService missaoService;

    @Autowired
    private FazendaAssembler fazendaAssembler;

    @PostMapping("/produtor/{produtorId}")
    @Operation(summary = "Cadastra fazenda com um Produtor como proprietario")
    public ResponseEntity<EntityModel<Fazenda>> cadastrar(@PathVariable Long produtorId,
                                                          @Valid @RequestBody Fazenda fazenda) {
        Fazenda salva = fazendaService.cadastrar(fazenda, produtorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(fazendaAssembler.toModel(salva));
    }

    @PutMapping("/{idFazenda}/produtor/{idProdutor}")
    @Operation(summary = "Vincula um produtor à Fazenda")
    public ResponseEntity<EntityModel<Fazenda>> vincularProdutor(@PathVariable Long idFazenda,
                                                                 @PathVariable Long idProdutor) {
        Fazenda fazenda = fazendaService.vincularProdutor(idFazenda, idProdutor);
        return ResponseEntity.ok(fazendaAssembler.toModel(fazenda));
    }

    @GetMapping("/produtor/{produtorId}")
    @Operation(summary = "Busca Fazenda pelo Id do Produtor")
    public ResponseEntity<CollectionModel<EntityModel<Fazenda>>> listarPorProdutor(@PathVariable Long produtorId) {
        List<EntityModel<Fazenda>> fazendas = fazendaService.listarPorProdutorId(produtorId)
                .stream()
                .map(fazendaAssembler::toModel)
                .toList();
        CollectionModel<EntityModel<Fazenda>> collection = CollectionModel.of(fazendas,
                linkTo(methodOn(FazendaController.class).listarPorProdutor(produtorId)).withSelfRel(),
                linkTo(methodOn(ProdutorController.class).buscarPorId(produtorId)).withRel("produtor")
        );
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca fazenda pelo Id")
    public ResponseEntity<EntityModel<Fazenda>> buscarPorId(@PathVariable Long id) {
        Fazenda fazenda = fazendaService.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fazenda não encontrada: " + id));
        return ResponseEntity.ok(fazendaAssembler.toModel(fazenda));
    }

    @PostMapping("/{id}/gerar-missoes")
    @Operation(summary = "Gera Missão")
    public ResponseEntity<Map<String, Object>> gerarMissoes(@PathVariable Long id) {
        Fazenda fazenda = fazendaService.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fazenda não encontrada: " + id));
        var missoes = missaoService.gerarMissoesAutomaticas(fazenda);
        return ResponseEntity.ok(Map.of(
                "mensagem", missoes.size() + " missão(ões) gerada(s)",
                "total", missoes.size()
        ));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza Informacoes de uma fazenda")
    public ResponseEntity<EntityModel<Fazenda>> atualizar(@PathVariable Long id,
                                                          @Valid @RequestBody Fazenda dados) {
        Fazenda atualizada = fazendaService.atualizar(id, dados);
        return ResponseEntity.ok(fazendaAssembler.toModel(atualizada));
    }

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