package com.autobots.automanager.controles;

import com.autobots.automanager.entidades.Veiculo;
import com.autobots.automanager.repositorios.RepositorioVeiculo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/veiculos")
public class VeiculoControle {
    @Autowired
    private RepositorioVeiculo repositorioVeiculo;

    @GetMapping
    public CollectionModel<EntityModel<Veiculo>> listarVeiculos() {
        List<EntityModel<Veiculo>> veiculos = repositorioVeiculo.findAll().stream()
            .map(veiculo -> {
                EntityModel<Veiculo> resource = EntityModel.of(veiculo);
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VeiculoControle.class).obterVeiculo(veiculo.getId())).withSelfRel());
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VeiculoControle.class).listarVeiculos()).withRel("veiculos"));
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VeiculoControle.class).criarVeiculo(veiculo)).withRel("create"));
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VeiculoControle.class).atualizarVeiculo(veiculo.getId(), veiculo)).withRel("update"));
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VeiculoControle.class).deletarVeiculo(veiculo.getId())).withRel("delete"));
                return resource;
            })
            .collect(Collectors.toList());
        
        CollectionModel<EntityModel<Veiculo>> collection = CollectionModel.of(veiculos);
        collection.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VeiculoControle.class).listarVeiculos()).withSelfRel());
        collection.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VeiculoControle.class).criarVeiculo(new Veiculo())).withRel("create"));
        return collection;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Veiculo>> obterVeiculo(@PathVariable Long id) {
        Optional<Veiculo> veiculo = repositorioVeiculo.findById(id);
        if (veiculo.isPresent()) {
            EntityModel<Veiculo> resource = EntityModel.of(veiculo.get());
            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VeiculoControle.class).obterVeiculo(id)).withSelfRel();
            resource.add(selfLink);
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VeiculoControle.class).listarVeiculos()).withRel("veiculos"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VeiculoControle.class).criarVeiculo(veiculo.get())).withRel("create"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VeiculoControle.class).atualizarVeiculo(id, veiculo.get())).withRel("update"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VeiculoControle.class).deletarVeiculo(id)).withRel("delete"));
            return ResponseEntity.ok(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<EntityModel<Veiculo>> criarVeiculo(@RequestBody Veiculo veiculo) {
        Veiculo salvo = repositorioVeiculo.save(veiculo);
        
        EntityModel<Veiculo> resource = EntityModel.of(salvo);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VeiculoControle.class).obterVeiculo(salvo.getId())).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VeiculoControle.class).listarVeiculos()).withRel("veiculos"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VeiculoControle.class).atualizarVeiculo(salvo.getId(), salvo)).withRel("update"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VeiculoControle.class).deletarVeiculo(salvo.getId())).withRel("delete"));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Veiculo>> atualizarVeiculo(@PathVariable Long id, @RequestBody Veiculo atualizacao) {
        Optional<Veiculo> veiculoOpt = repositorioVeiculo.findById(id);
        if (!veiculoOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Veiculo veiculo = veiculoOpt.get();

        if (atualizacao.getTipo() != null) veiculo.setTipo(atualizacao.getTipo());
        if (atualizacao.getModelo() != null) veiculo.setModelo(atualizacao.getModelo());
        if (atualizacao.getPlaca() != null) veiculo.setPlaca(atualizacao.getPlaca());

        Veiculo salvo = repositorioVeiculo.save(veiculo);
        
        EntityModel<Veiculo> resource = EntityModel.of(salvo);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VeiculoControle.class).obterVeiculo(salvo.getId())).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VeiculoControle.class).listarVeiculos()).withRel("veiculos"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VeiculoControle.class).criarVeiculo(salvo)).withRel("create"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VeiculoControle.class).atualizarVeiculo(salvo.getId(), salvo)).withRel("update"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VeiculoControle.class).deletarVeiculo(salvo.getId())).withRel("delete"));
        
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarVeiculo(@PathVariable Long id) {
        if (!repositorioVeiculo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repositorioVeiculo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
} 