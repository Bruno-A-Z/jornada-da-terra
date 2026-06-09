package br.com.fiap.jornadaterra.controller;

import br.com.fiap.jornadaterra.assembler.ProdutorAssembler;
import br.com.fiap.jornadaterra.exception.ResourceNotFoundException;
import br.com.fiap.jornadaterra.model.Produtor;
import br.com.fiap.jornadaterra.service.ProdutorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Tag(name = "Produtor", description = "Gerencia produtores")
@RestController
@RequestMapping("/produtores")
public class ProdutorController {

    @Autowired
    private ProdutorService produtorService;

    @Autowired
    private ProdutorAssembler produtorAssembler;

    @PostMapping
    @Operation(summary = "Cadastra produtor")
    public ResponseEntity<EntityModel<Produtor>> cadastrar(@Valid @RequestBody Produtor produtor) {
        Produtor salvo = produtorService.cadastrar(produtor);
        return ResponseEntity.status(HttpStatus.CREATED).body(produtorAssembler.toModel(salvo));
    }

    @GetMapping
    @Operation(summary = "Lista todos os Produtores")
    public ResponseEntity<CollectionModel<EntityModel<Produtor>>> listarTodos() {
        List<EntityModel<Produtor>> produtores = produtorService.listarTodos()
                .stream()
                .map(produtorAssembler::toModel)
                .toList();
        CollectionModel<EntityModel<Produtor>> collection = CollectionModel.of(produtores,
                linkTo(methodOn(ProdutorController.class).listarTodos()).withSelfRel()
        );
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca produtor pelo id")
    public ResponseEntity<EntityModel<Produtor>> buscarPorId(@PathVariable Long id) {
        Produtor produtor = produtorService.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produtor não encontrado: " + id));
        return ResponseEntity.ok(produtorAssembler.toModel(produtor));
    }

    @GetMapping("/{id}/perfil")
    @Operation(summary = "Mostra perfil 'gamificado' do Produtor pelo id")
    public ResponseEntity<EntityModel<Map<String, Object>>> perfil(@PathVariable Long id) {
        Produtor p = produtorService.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produtor não encontrado: " + id));

        Map<String, Object> dados = Map.of(
                "nome", p.getNome(),
                "nivel", p.getNivel(),
                "titulo", p.getNomeNivel(),
                "pontos", p.getPontos(),
                "fazendas", p.getFazendas().size()
        );

        EntityModel<Map<String, Object>> model = EntityModel.of(dados,
                linkTo(methodOn(ProdutorController.class).perfil(id)).withSelfRel(),
                linkTo(methodOn(ProdutorController.class).buscarPorId(id)).withRel("produtor"),
                linkTo(methodOn(FazendaController.class).listarPorProdutor(id)).withRel("fazendas"),
                linkTo(methodOn(MissaoController.class).listarAtivas(id)).withRel("missoes-ativas")
        );
        return ResponseEntity.ok(model);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza Produtor pelo ID")
    public ResponseEntity<EntityModel<Produtor>> atualizar(@PathVariable Long id,
                                                           @Valid @RequestBody Produtor produtor) {
        Produtor atualizado = produtorService.atualizar(id, produtor);
        return ResponseEntity.ok(produtorAssembler.toModel(atualizado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta por Id")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            produtorService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
