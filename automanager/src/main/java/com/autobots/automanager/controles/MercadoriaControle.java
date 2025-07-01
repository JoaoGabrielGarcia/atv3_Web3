package com.autobots.automanager.controles;

import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.repositorios.RepositorioMercadoria;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.servico.HistoricoVendaService;
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
@RequestMapping("/mercadorias")
public class MercadoriaControle {
    @Autowired
    private RepositorioMercadoria repositorioMercadoria;
    
    @Autowired
    private RepositorioEmpresa repositorioEmpresa;

    @Autowired
    private HistoricoVendaService historicoVendaService;

    @GetMapping
    public CollectionModel<EntityModel<Mercadoria>> listarMercadorias() {
        List<EntityModel<Mercadoria>> mercadorias = repositorioMercadoria.findAll().stream()
            .map(mercadoria -> {
                EntityModel<Mercadoria> resource = EntityModel.of(mercadoria);
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MercadoriaControle.class).obterMercadoria(mercadoria.getId())).withSelfRel());
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MercadoriaControle.class).listarMercadorias()).withRel("mercadorias"));
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MercadoriaControle.class).criarMercadoria(mercadoria)).withRel("create"));
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MercadoriaControle.class).atualizarMercadoria(mercadoria.getId(), mercadoria)).withRel("update"));
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MercadoriaControle.class).deletarMercadoria(mercadoria.getId())).withRel("delete"));
                return resource;
            })
            .collect(Collectors.toList());
        
        CollectionModel<EntityModel<Mercadoria>> collection = CollectionModel.of(mercadorias);
        collection.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MercadoriaControle.class).listarMercadorias()).withSelfRel());
        collection.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MercadoriaControle.class).criarMercadoria(new Mercadoria())).withRel("create"));
        return collection;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Mercadoria>> obterMercadoria(@PathVariable Long id) {
        Optional<Mercadoria> mercadoria = repositorioMercadoria.findById(id);
        if (mercadoria.isPresent()) {
            EntityModel<Mercadoria> resource = EntityModel.of(mercadoria.get());
            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MercadoriaControle.class).obterMercadoria(id)).withSelfRel();
            resource.add(selfLink);
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MercadoriaControle.class).listarMercadorias()).withRel("mercadorias"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MercadoriaControle.class).criarMercadoria(mercadoria.get())).withRel("create"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MercadoriaControle.class).atualizarMercadoria(id, mercadoria.get())).withRel("update"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MercadoriaControle.class).deletarMercadoria(id)).withRel("delete"));
            return ResponseEntity.ok(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<EntityModel<Mercadoria>> criarMercadoria(@RequestBody Mercadoria mercadoria) {
        
        // Validar se empresa foi fornecida
        if (mercadoria.getEmpresa() == null || mercadoria.getEmpresa().getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        
        // Buscar e validar empresa
        Optional<Empresa> empresaOpt = repositorioEmpresa.findById(mercadoria.getEmpresa().getId());
        if (!empresaOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        mercadoria.setEmpresa(empresaOpt.get());
        Mercadoria salvo = repositorioMercadoria.save(mercadoria);
        
        EntityModel<Mercadoria> resource = EntityModel.of(salvo);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MercadoriaControle.class).obterMercadoria(salvo.getId())).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MercadoriaControle.class).listarMercadorias()).withRel("mercadorias"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MercadoriaControle.class).atualizarMercadoria(salvo.getId(), salvo)).withRel("update"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MercadoriaControle.class).deletarMercadoria(salvo.getId())).withRel("delete"));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Mercadoria>> atualizarMercadoria(@PathVariable Long id, @RequestBody Mercadoria atualizacao) {
        Optional<Mercadoria> mercadoriaOpt = repositorioMercadoria.findById(id);
        if (!mercadoriaOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Mercadoria mercadoria = mercadoriaOpt.get();

        if (atualizacao.getValidade() != null) mercadoria.setValidade(atualizacao.getValidade());
        if (atualizacao.getFabricacao() != null) mercadoria.setFabricacao(atualizacao.getFabricacao());
        if (atualizacao.getCadastro() != null) mercadoria.setCadastro(atualizacao.getCadastro());
        if (atualizacao.getNome() != null) mercadoria.setNome(atualizacao.getNome());
        if (atualizacao.getQuantidade() > 0) mercadoria.setQuantidade(atualizacao.getQuantidade());
        if (atualizacao.getValor() > 0) mercadoria.setValor(atualizacao.getValor());
        if (atualizacao.getDescricao() != null) mercadoria.setDescricao(atualizacao.getDescricao());
        
        // Validar e processar empresa (obrigatório)
        if (atualizacao.getEmpresa() == null || atualizacao.getEmpresa().getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        
        Optional<Empresa> empresaOpt = repositorioEmpresa.findById(atualizacao.getEmpresa().getId());
        if (!empresaOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        mercadoria.setEmpresa(empresaOpt.get());

        Mercadoria salvo = repositorioMercadoria.save(mercadoria);
        
        EntityModel<Mercadoria> resource = EntityModel.of(salvo);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MercadoriaControle.class).obterMercadoria(salvo.getId())).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MercadoriaControle.class).listarMercadorias()).withRel("mercadorias"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MercadoriaControle.class).criarMercadoria(salvo)).withRel("create"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MercadoriaControle.class).atualizarMercadoria(salvo.getId(), salvo)).withRel("update"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(MercadoriaControle.class).deletarMercadoria(salvo.getId())).withRel("delete"));
        
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarMercadoria(@PathVariable Long id) {
        if (!repositorioMercadoria.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        // Buscar a mercadoria para fazer backup histórico
        Optional<Mercadoria> mercadoriaOpt = repositorioMercadoria.findById(id);
        if (mercadoriaOpt.isPresent()) {
            Mercadoria mercadoria = mercadoriaOpt.get();
            System.out.println("[DEBUG] Preparando backup histórico para mercadoria: " + mercadoria.getNome());
            
            // Fazer backup de todas as vendas relacionadas
            historicoVendaService.prepararBackupMercadoria(mercadoria);
        }
        
        repositorioMercadoria.deleteById(id);
        return ResponseEntity.noContent().build();
    }
} 