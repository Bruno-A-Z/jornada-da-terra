package br.com.fiap.jornadaterra.controller;

import br.com.fiap.jornadaterra.exception.ResourceNotFoundException;
import br.com.fiap.jornadaterra.model.missao.Missao;
import br.com.fiap.jornadaterra.service.MissaoService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/missoes")
public class MissaoController {

    @Autowired
    private MissaoService missaoService;

    @GetMapping("/{id}")
    @Operation(summary = "busca missao por id")
    public ResponseEntity<EntityModel<Missao>> buscarPorId(@PathVariable Long id) {
        Missao missao = missaoService.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Missão não encontrada: " + id));
        EntityModel<Missao> model = EntityModel.of(missao,
                linkTo(methodOn(MissaoController.class).buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(FazendaController.class).buscarPorId(missao.getFazenda().getId())).withRel("fazenda")
        );
        return ResponseEntity.ok(model);
    }


    @GetMapping("/produtor/{produtorId}")
    @Operation(summary = "Lista missoes ativas/incompletas do produtor")
    public ResponseEntity<CollectionModel<EntityModel<Missao>>> listarAtivas(@PathVariable Long produtorId) {
        List<EntityModel<Missao>> missoes = missaoService.listarMissoesPendentes(produtorId)
                .stream()
                .map(m -> EntityModel.of(m,
                        linkTo(methodOn(MissaoController.class).buscarPorId(m.getId())).withSelfRel(),
                        linkTo(methodOn(ProdutorController.class).buscarPorId(produtorId)).withRel("produtor")
                ))
                .toList();

        CollectionModel<EntityModel<Missao>> collection = CollectionModel.of(missoes,
                linkTo(methodOn(MissaoController.class).listarAtivas(produtorId)).withSelfRel(),
                linkTo(methodOn(MissaoController.class).listarTodas(produtorId)).withRel("historico-completo")
        );
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/produtor/{produtorId}/todas")
    @Operation(summary = "Lista Historico de Missoes do produtor")
    public ResponseEntity<CollectionModel<EntityModel<Missao>>> listarTodas(@PathVariable Long produtorId) {
        List<EntityModel<Missao>> missoes = missaoService.listarTodasMissoes(produtorId)
                .stream()
                .map(m -> EntityModel.of(m,
                        linkTo(methodOn(MissaoController.class).buscarPorId(m.getId())).withSelfRel(),
                        linkTo(methodOn(ProdutorController.class).buscarPorId(produtorId)).withRel("produtor")
                ))
                .toList();

        CollectionModel<EntityModel<Missao>> collection = CollectionModel.of(missoes,
                linkTo(methodOn(MissaoController.class).listarTodas(produtorId)).withSelfRel()
        );
        return ResponseEntity.ok(collection);
    }

    @PostMapping("/{id}/iniciar")
    @Operation(summary = "Altera status da missao para Iniciado")
    public ResponseEntity<Map<String, String>> iniciar(@PathVariable Long id,
                                                       @RequestParam Long produtorId) {
        String resultado = missaoService.iniciarMissao(id, produtorId);
        return ResponseEntity.ok(Map.of(
                "mensagem", resultado,
                "_link_concluir", "/missoes/" + id + "/concluir?produtorId=" + produtorId,
                "_link_missao", "/missoes/" + id
        ));
    }

    @PostMapping("/{id}/concluir")
    @Operation(summary = "Altera Status da Missao Para Concluido")
    public ResponseEntity<Map<String, String>> concluir(@PathVariable Long id,
                                                        @RequestParam Long produtorId) {
        String resultado = missaoService.concluirMissao(id, produtorId);
        return ResponseEntity.ok(Map.of(
                "mensagem", resultado,
                "_link_perfil", "/produtores/" + produtorId + "/perfil",
                "_link_missoes", "/missoes/produtor/" + produtorId
        ));
    }

    @PostMapping("/{id}/acao-climatica")
    @Operation(summary = "Confirma acao")
    public ResponseEntity<Map<String, String>> confirmarAcao(@PathVariable Long id) {
        String resultado = missaoService.confirmarAcaoClimatica(id);
        return ResponseEntity.ok(Map.of(
                "mensagem", resultado,
                "_link_concluir", "/missoes/" + id + "/concluir"
        ));
    }

    @PostMapping("/{id}/verificar-setor")
    public ResponseEntity<Map<String, String>> verificarSetor(@PathVariable Long id,
                                                              @RequestParam String nomeSetor) {
        String resultado = missaoService.verificarSetorMonitoramento(id, nomeSetor);
        return ResponseEntity.ok(Map.of("mensagem", resultado));
    }



    @GetMapping("/{id}/produtividade")
    @Operation(summary = "Busca Missao por ID")
    public ResponseEntity<Map<String, String>> registrarProdutividade(@PathVariable Long id,
                                                                      @RequestParam double valor) {
        return ResponseEntity.ok(Map.of("mensagem",
                missaoService.registrarProdutividade(id, valor)));
    }
}
