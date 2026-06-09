package br.com.fiap.jornadaterra.assembler;

import br.com.fiap.jornadaterra.controller.FazendaController;
import br.com.fiap.jornadaterra.controller.SetorController;
import br.com.fiap.jornadaterra.model.Setor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class SetorAssembler implements RepresentationModelAssembler<Setor, EntityModel<Setor>> {

    @Override
    public EntityModel<Setor> toModel(Setor setor) {
        EntityModel<Setor> model = EntityModel.of(setor,
                linkTo(methodOn(SetorController.class).buscarPorId(setor.getId())).withSelfRel()
        );

        if (setor.getFazenda() != null) {
            model.add(linkTo(methodOn(FazendaController.class)
                    .buscarPorId(setor.getFazenda().getId())).withRel("fazenda"));
            model.add(linkTo(methodOn(FazendaController.class)
                    .gerarMissoes(setor.getFazenda().getId())).withRel("gerar-missoes"));
        }

        return model;
    }
}