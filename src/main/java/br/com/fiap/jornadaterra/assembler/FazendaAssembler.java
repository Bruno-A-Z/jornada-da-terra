package br.com.fiap.jornadaterra.assembler;

import br.com.fiap.jornadaterra.controller.FazendaController;
import br.com.fiap.jornadaterra.controller.ProdutorController;
import br.com.fiap.jornadaterra.controller.SetorController;
import br.com.fiap.jornadaterra.model.Fazenda;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class FazendaAssembler implements RepresentationModelAssembler<Fazenda, EntityModel<Fazenda>> {

    @Override
    public EntityModel<Fazenda> toModel(Fazenda fazenda) {
        EntityModel<Fazenda> model = EntityModel.of(fazenda,
                linkTo(methodOn(FazendaController.class).buscarPorId(fazenda.getId())).withSelfRel(),
                linkTo(methodOn(SetorController.class).listarPorFazenda(fazenda.getId())).withRel("setores"),
                linkTo(methodOn(FazendaController.class).gerarMissoes(fazenda.getId())).withRel("gerar-missoes")
        );

        if (fazenda.getProdutor() != null) {
            model.add(linkTo(methodOn(ProdutorController.class)
                    .buscarPorId(fazenda.getProdutor().getId())).withRel("produtor"));
        }

        return model;
    }
}