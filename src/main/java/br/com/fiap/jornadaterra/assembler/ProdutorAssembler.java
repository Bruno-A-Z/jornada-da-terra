package br.com.fiap.jornadaterra.assembler;

import br.com.fiap.jornadaterra.controller.FazendaController;
import br.com.fiap.jornadaterra.controller.ProdutorController;
import br.com.fiap.jornadaterra.model.Produtor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ProdutorAssembler implements RepresentationModelAssembler<Produtor, EntityModel<Produtor>> {

    @Override
    public EntityModel<Produtor> toModel(Produtor produtor) {
        return EntityModel.of(produtor,
                linkTo(methodOn(ProdutorController.class).buscarPorId(produtor.getId())).withSelfRel(),
                linkTo(methodOn(ProdutorController.class).listarTodos()).withRel("todos-produtores"),
                linkTo(methodOn(FazendaController.class).listarPorProdutor(produtor.getId())).withRel("fazendas"),
                linkTo(methodOn(ProdutorController.class).perfil(produtor.getId())).withRel("perfil")
        );
    }
}