package br.com.fiap.jornadaterra.assembler;

import br.com.fiap.jornadaterra.controller.FazendaController;
import br.com.fiap.jornadaterra.controller.MissaoController;
import br.com.fiap.jornadaterra.controller.ProdutorController;
import br.com.fiap.jornadaterra.model.missao.Missao;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class MissaoAssembler implements RepresentationModelAssembler<Missao, EntityModel<Missao>> {

    @Override
    public EntityModel<Missao> toModel(Missao missao) {
        EntityModel<Missao> model = EntityModel.of(missao,
                linkTo(methodOn(MissaoController.class).buscarPorId(missao.getId())).withSelfRel()
        );

        if (missao.getFazenda() != null) {
            model.add(linkTo(methodOn(FazendaController.class)
                    .buscarPorId(missao.getFazenda().getId())).withRel("fazenda"));

            if (missao.getFazenda().getProdutor() != null) {
                model.add(linkTo(methodOn(ProdutorController.class)
                        .buscarPorId(missao.getFazenda().getProdutor().getId())).withRel("produtor"));
                model.add(linkTo(methodOn(MissaoController.class)
                        .listarAtivas(missao.getFazenda().getProdutor().getId())).withRel("missoes-ativas"));
            }
        }

        return model;
    }
}