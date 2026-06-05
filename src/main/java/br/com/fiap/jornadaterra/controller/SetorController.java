package br.com.fiap.jornadaterra.controller;

import br.com.fiap.jornadaterra.exception.ResourceNotFoundException;
import br.com.fiap.jornadaterra.model.Setor;
import br.com.fiap.jornadaterra.service.SetorService;
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
@RequestMapping("/setores")
public class SetorController {

    @Autowired
    private SetorService setorService;

    @PostMapping("/fazenda/{fazendaId}")
    @Operation(summary = "Adiciona Setor da Fazenda")
    public ResponseEntity<EntityModel<Setor>> cadastrar(@PathVariable Long fazendaId,
                                                        @Valid @RequestBody Setor setor) {
        Setor salvo = setorService.cadastrar(setor, fazendaId);
        EntityModel<Setor> model = EntityModel.of(salvo,
                linkTo(methodOn(SetorController.class).buscarPorId(salvo.getId())).withSelfRel(),
                linkTo(methodOn(SetorController.class).listarPorFazenda(fazendaId)).withRel("setores-da-fazenda"),
                linkTo(methodOn(FazendaController.class).buscarPorId(fazendaId)).withRel("fazenda")
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @GetMapping("/fazenda/{fazendaId}")
    @Operation(summary = "Lista setores da fazenda")
    public ResponseEntity<CollectionModel<EntityModel<Setor>>> listarPorFazenda(@PathVariable Long fazendaId) {
        List<EntityModel<Setor>> setores = setorService.listarPorFazenda(fazendaId)
                .stream()
                .map(s -> EntityModel.of(s,
                        linkTo(methodOn(SetorController.class).buscarPorId(s.getId())).withSelfRel(),
                        linkTo(methodOn(FazendaController.class).buscarPorId(fazendaId)).withRel("fazenda")
                ))
                .toList();

        CollectionModel<EntityModel<Setor>> collection = CollectionModel.of(setores,
                linkTo(methodOn(SetorController.class).listarPorFazenda(fazendaId)).withSelfRel(),
                linkTo(methodOn(FazendaController.class).buscarPorId(fazendaId)).withRel("fazenda")
        );
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca Setor por Id")
    public ResponseEntity<EntityModel<Setor>> buscarPorId(@PathVariable Long id) {
        Setor setor = setorService.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Setor não encontrado: " + id));
        EntityModel<Setor> model = EntityModel.of(setor,
                linkTo(methodOn(SetorController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(FazendaController.class).buscarPorId(setor.getFazenda().getId())).withRel("fazenda")
        );
        return ResponseEntity.ok(model);
    }

    @PatchMapping("/{id}/satelital")
    @Operation(summary = "Simula leitura de dados satelitais")
    public ResponseEntity<EntityModel<Setor>> atualizarSatelital(@PathVariable Long id,
                                                                 @RequestBody Map<String, Double> dados) {
        double temperatura = dados.getOrDefault("temperatura", 20.0);
        double umidade = dados.getOrDefault("umidade", 60.0);
        double ndvi = dados.getOrDefault("ndvi", 0.7);
        Setor setor = setorService.atualizarDadosSatelitais(id, temperatura, umidade, ndvi);
        EntityModel<Setor> model = EntityModel.of(setor,
                linkTo(methodOn(SetorController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(FazendaController.class).gerarMissoes(setor.getFazenda().getId())).withRel("gerar-missoes"),
                linkTo(methodOn(FazendaController.class).buscarPorId(setor.getFazenda().getId())).withRel("fazenda")
        );
        return ResponseEntity.ok(model);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza Setor por Id")
    public ResponseEntity<EntityModel<Setor>> atualizar(@PathVariable Long id,
                                                        @Valid @RequestBody Setor dados) {
        Setor atualizado = setorService.atualizar(id, dados);
        EntityModel<Setor> model = EntityModel.of(atualizado,
                linkTo(methodOn(SetorController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(FazendaController.class).buscarPorId(atualizado.getFazenda().getId())).withRel("fazenda")
        );
        return ResponseEntity.ok(model);
    }

    // DELETE /setores/
    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta Setor")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            setorService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}