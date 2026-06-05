package br.com.fiap.jornadaterra.controller;

import br.com.fiap.jornadaterra.exception.ResourceNotFoundException;
import br.com.fiap.jornadaterra.model.Fazenda;
import br.com.fiap.jornadaterra.model.Localizacao;
import br.com.fiap.jornadaterra.service.FazendaService;
import br.com.fiap.jornadaterra.service.MissaoService;
import io.swagger.v3.oas.annotations.Operation;
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

@RestController
@RequestMapping("/fazendas")
public class FazendaController {

    @Autowired
    private FazendaService fazendaService;

    @Autowired
    private MissaoService missaoService;

    @PostMapping("/produtor/{produtorId}")
    @Operation(summary = "Cadastra fazenda com um Produtor como proprietario")
    public ResponseEntity<EntityModel<Fazenda>> cadastrar(@PathVariable Long produtorId,
                                                          @Valid @RequestBody Fazenda fazenda) {
        Fazenda salva = fazendaService.cadastrar(fazenda, produtorId);
        EntityModel<Fazenda> model = EntityModel.of(salva,
                linkTo(methodOn(FazendaController.class).buscarPorId(salva.getId())).withSelfRel(),
                linkTo(methodOn(FazendaController.class).listarPorProdutor(produtorId)).withRel("fazendas-do-produtor"),
                linkTo(methodOn(SetorController.class).listarPorFazenda(salva.getId())).withRel("setores"),
                linkTo(methodOn(FazendaController.class).gerarMissoes(salva.getId())).withRel("gerar-missoes")
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @PutMapping("/{idFazenda}/produtor/{idProdutor}")
    @Operation(summary = "Vincula um produtor à Fazenda")
    public ResponseEntity<Fazenda> vincularProdutor(@PathVariable Long idFazenda, @PathVariable Long idProdutor) {
        return ResponseEntity.ok(fazendaService.vincularProdutor(idFazenda, idProdutor));
    }

    @GetMapping("/produtor/{produtorId}")
    @Operation(summary = "Busca Fazenda pelo Id do Produtor")
    public ResponseEntity<CollectionModel<EntityModel<Fazenda>>> listarPorProdutor(@PathVariable Long produtorId) {
        List<EntityModel<Fazenda>> fazendas = fazendaService.listarPorProdutorId(produtorId)
                .stream()
                .map(f -> EntityModel.of(f,
                        linkTo(methodOn(FazendaController.class).buscarPorId(f.getId())).withSelfRel(),
                        linkTo(methodOn(SetorController.class).listarPorFazenda(f.getId())).withRel("setores"),
                        linkTo(methodOn(FazendaController.class).gerarMissoes(f.getId())).withRel("gerar-missoes")
                ))
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
        EntityModel<Fazenda> model = EntityModel.of(fazenda,
                linkTo(methodOn(FazendaController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(SetorController.class).listarPorFazenda(id)).withRel("setores"),
                linkTo(methodOn(FazendaController.class).gerarMissoes(id)).withRel("gerar-missoes"),
                linkTo(methodOn(ProdutorController.class).buscarPorId(id)).withRel("produtor")
        );
        return ResponseEntity.ok(model);
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
        EntityModel<Fazenda> model = EntityModel.of(atualizada,
                linkTo(methodOn(FazendaController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(SetorController.class).listarPorFazenda(id)).withRel("setores")
        );
        return ResponseEntity.ok(model);
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