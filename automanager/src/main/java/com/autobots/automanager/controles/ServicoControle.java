package com.autobots.automanager.controles;

import com.autobots.automanager.entidades.Servico;
import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.repositorios.RepositorioServico;
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
@RequestMapping("/servicos")
public class ServicoControle {
    @Autowired
    private RepositorioServico repositorioServico;
    
    @Autowired
    private RepositorioEmpresa repositorioEmpresa;

    @Autowired
    private HistoricoVendaService historicoVendaService;

    @GetMapping
    public CollectionModel<EntityModel<Servico>> listarServicos() {
        List<EntityModel<Servico>> servicos = repositorioServico.findAll().stream()
            .map(servico -> {
                EntityModel<Servico> resource = EntityModel.of(servico);
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ServicoControle.class).obterServico(servico.getId())).withSelfRel());
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ServicoControle.class).listarServicos()).withRel("servicos"));
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ServicoControle.class).criarServico(servico)).withRel("create"));
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ServicoControle.class).atualizarServico(servico.getId(), servico)).withRel("update"));
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ServicoControle.class).deletarServico(servico.getId())).withRel("delete"));
                return resource;
            })
            .collect(Collectors.toList());
        
        CollectionModel<EntityModel<Servico>> collection = CollectionModel.of(servicos);
        collection.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ServicoControle.class).listarServicos()).withSelfRel());
        collection.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ServicoControle.class).criarServico(new Servico())).withRel("create"));
        return collection;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Servico>> obterServico(@PathVariable Long id) {
        Optional<Servico> servico = repositorioServico.findById(id);
        if (servico.isPresent()) {
            EntityModel<Servico> resource = EntityModel.of(servico.get());
            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ServicoControle.class).obterServico(id)).withSelfRel();
            resource.add(selfLink);
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ServicoControle.class).listarServicos()).withRel("servicos"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ServicoControle.class).criarServico(servico.get())).withRel("create"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ServicoControle.class).atualizarServico(id, servico.get())).withRel("update"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ServicoControle.class).deletarServico(id)).withRel("delete"));
            return ResponseEntity.ok(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<EntityModel<Servico>> criarServico(@RequestBody Servico servico) {
        // Validar se empresa foi fornecida
        if (servico.getEmpresa() == null || servico.getEmpresa().getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        
        // Buscar e validar empresa
        Optional<Empresa> empresaOpt = repositorioEmpresa.findById(servico.getEmpresa().getId());
        if (!empresaOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        servico.setEmpresa(empresaOpt.get());
        
        Servico salvo = repositorioServico.save(servico);
        
        EntityModel<Servico> resource = EntityModel.of(salvo);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ServicoControle.class).obterServico(salvo.getId())).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ServicoControle.class).listarServicos()).withRel("servicos"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ServicoControle.class).atualizarServico(salvo.getId(), salvo)).withRel("update"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ServicoControle.class).deletarServico(salvo.getId())).withRel("delete"));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Servico>> atualizarServico(@PathVariable Long id, @RequestBody Servico atualizacao) {
        Optional<Servico> servicoOpt = repositorioServico.findById(id);
        if (!servicoOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Servico servico = servicoOpt.get();

        if (atualizacao.getNome() != null) servico.setNome(atualizacao.getNome());
        if (atualizacao.getValor() > 0) servico.setValor(atualizacao.getValor());
        if (atualizacao.getDescricao() != null) servico.setDescricao(atualizacao.getDescricao());
        
        // Validar e processar empresa (obrigatório)
        if (atualizacao.getEmpresa() == null || atualizacao.getEmpresa().getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        
        Optional<Empresa> empresaOpt = repositorioEmpresa.findById(atualizacao.getEmpresa().getId());
        if (!empresaOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        servico.setEmpresa(empresaOpt.get());

        Servico salvo = repositorioServico.save(servico);
        
        EntityModel<Servico> resource = EntityModel.of(salvo);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ServicoControle.class).obterServico(salvo.getId())).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ServicoControle.class).listarServicos()).withRel("servicos"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ServicoControle.class).criarServico(salvo)).withRel("create"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ServicoControle.class).atualizarServico(salvo.getId(), salvo)).withRel("update"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ServicoControle.class).deletarServico(salvo.getId())).withRel("delete"));
        
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarServico(@PathVariable Long id) {
        if (!repositorioServico.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        // Buscar o serviço para fazer backup histórico
        Optional<Servico> servicoOpt = repositorioServico.findById(id);
        if (servicoOpt.isPresent()) {
            Servico servico = servicoOpt.get();
            System.out.println("[DEBUG] Preparando backup histórico para serviço: " + servico.getNome());
            
            // Fazer backup de todas as vendas relacionadas
            historicoVendaService.prepararBackupServico(servico);
        }
        
        repositorioServico.deleteById(id);
        return ResponseEntity.noContent().build();
    }
} 