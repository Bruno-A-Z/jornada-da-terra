package br.com.fiap.jornadaterra.controller;

import br.com.fiap.jornadaterra.assembler.MissaoAssembler;
import br.com.fiap.jornadaterra.exception.ResourceNotFoundException;
import br.com.fiap.jornadaterra.model.missao.Missao;
import br.com.fiap.jornadaterra.service.MissaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Tag(name = "Missao", description = "Gerenciamento de missoes, criacao, atualizar progresso, etc.")
@RestController
@RequestMapping("/missoes")
public class MissaoController {

    @Autowired
    private MissaoService missaoService;

    @Autowired
    private MissaoAssembler missaoAssembler;

    @GetMapping("/{id}")
    @Operation(summary = "busca missao por id")
    public ResponseEntity<EntityModel<Missao>> buscarPorId(@PathVariable Long id) {
        Missao missao = missaoService.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Missão não encontrada: " + id));
        return ResponseEntity.ok(missaoAssembler.toModel(missao));
    }

    @GetMapping("/fazenda/{fazendaId}/produtor/{produtorId}")
    @Operation(summary = "Lista Historico de Missoes do produtor")
    public ResponseEntity<CollectionModel<EntityModel<Missao>>> listarTodas(@PathVariable Long produtorId) {
        List<EntityModel<Missao>> missoes = missaoService.listarTodasMissoes(produtorId)
                .stream()
                .map(missaoAssembler::toModel)
                .toList();
        CollectionModel<EntityModel<Missao>> collection = CollectionModel.of(missoes,
                linkTo(methodOn(MissaoController.class).listarTodas(produtorId)).withSelfRel()
        );
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/produtor/{produtorId}/ativas")
    @Operation(summary = "Lista missoes ativas/incompletas do produtor")
    public ResponseEntity<CollectionModel<EntityModel<Missao>>> listarAtivas(@PathVariable Long produtorId) {
        List<EntityModel<Missao>> missoes = missaoService.listarMissoesPendentes(produtorId)
                .stream()
                .map(missaoAssembler::toModel)
                .toList();
        CollectionModel<EntityModel<Missao>> collection = CollectionModel.of(missoes,
                linkTo(methodOn(MissaoController.class).listarAtivas(produtorId)).withSelfRel(),
                linkTo(methodOn(MissaoController.class).listarTodas(produtorId)).withRel("historico-completo")
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

    @PostMapping("/produtividade/fazenda/{fazendaId}")
    @Operation(summary = "Cria missao de produtividade")
    public ResponseEntity<EntityModel<Missao>> criarProdutividade(
            @PathVariable Long fazendaId,
            @RequestParam String tipoAcao,
            @RequestParam String cultura,
            @RequestParam double meta) {
        Missao missao = missaoService.criarMissaoProdutividade(fazendaId, tipoAcao, cultura, meta);
        return ResponseEntity.status(HttpStatus.CREATED).body(missaoAssembler.toModel(missao));
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
