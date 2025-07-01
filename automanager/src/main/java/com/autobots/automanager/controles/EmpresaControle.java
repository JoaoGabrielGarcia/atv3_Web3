package com.autobots.automanager.controles;

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.entidades.Servico;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.RepositorioUsuario;
import com.autobots.automanager.repositorios.RepositorioMercadoria;
import com.autobots.automanager.repositorios.RepositorioServico;
import com.autobots.automanager.repositorios.RepositorioVenda;
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
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/empresas")
public class EmpresaControle {
    @Autowired
    private RepositorioEmpresa repositorioEmpresa;
    
    @Autowired
    private RepositorioUsuario repositorioUsuario;
    
    @Autowired
    private RepositorioMercadoria repositorioMercadoria;
    
    @Autowired
    private RepositorioServico repositorioServico;
    
    @Autowired
    private RepositorioVenda repositorioVenda;

    @Autowired
    private HistoricoVendaService historicoVendaService;

    @GetMapping
    public CollectionModel<EntityModel<Empresa>> listarEmpresas() {
        List<EntityModel<Empresa>> empresas = repositorioEmpresa.findAll().stream()
            .map(empresa -> {
                EntityModel<Empresa> resource = EntityModel.of(empresa);
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).obterEmpresa(empresa.getId())).withSelfRel());
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).listarEmpresas()).withRel("empresas"));
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).criarEmpresa(empresa)).withRel("create"));
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).atualizarEmpresa(empresa.getId(), empresa)).withRel("update"));
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).deletarEmpresa(empresa.getId())).withRel("delete"));
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularUsuario(empresa.getId(), new Usuario())).withRel("empresas.usuarios"));
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularMercadoria(empresa.getId(), new Mercadoria())).withRel("empresas.mercadorias"));
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularServico(empresa.getId(), new Servico())).withRel("empresas.servicos"));
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularVenda(empresa.getId(), new Venda())).withRel("empresas.vendas"));
                return resource;
            })
            .collect(Collectors.toList());
        
        CollectionModel<EntityModel<Empresa>> collection = CollectionModel.of(empresas);
        collection.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).listarEmpresas()).withSelfRel());
        collection.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).criarEmpresa(new Empresa())).withRel("create"));
        return collection;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Empresa>> obterEmpresa(@PathVariable Long id) {
        Optional<Empresa> empresa = repositorioEmpresa.findById(id);
        if (empresa.isPresent()) {
            EntityModel<Empresa> resource = EntityModel.of(empresa.get());
            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).obterEmpresa(id)).withSelfRel();
            resource.add(selfLink);
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).listarEmpresas()).withRel("empresas"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).criarEmpresa(empresa.get())).withRel("create"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).atualizarEmpresa(id, empresa.get())).withRel("update"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).deletarEmpresa(id)).withRel("delete"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularUsuario(id, new Usuario())).withRel("empresas.usuarios"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularMercadoria(id, new Mercadoria())).withRel("empresas.mercadorias"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularServico(id, new Servico())).withRel("empresas.servicos"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularVenda(id, new Venda())).withRel("empresas.vendas"));
            return ResponseEntity.ok(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<EntityModel<Empresa>> criarEmpresa(@RequestBody Empresa empresa) {
        
        if (empresa.getUsuarios() != null && !empresa.getUsuarios().isEmpty()) {
            Set<Usuario> usuariosProcessados = new java.util.HashSet<>();
            for (Usuario usuario : empresa.getUsuarios()) {
                if (usuario.getId() != null) {
                    Optional<Usuario> usuarioOpt = repositorioUsuario.findById(usuario.getId());
                    if (usuarioOpt.isPresent()) {
                        usuariosProcessados.add(usuarioOpt.get());
                    }
                }
            }
            empresa.setUsuarios(usuariosProcessados);
        }
        
        if (empresa.getMercadorias() != null && !empresa.getMercadorias().isEmpty()) {
            Set<Mercadoria> mercadoriasProcessadas = new java.util.HashSet<>();
            for (Mercadoria mercadoria : empresa.getMercadorias()) {
                if (mercadoria.getId() != null) {
                    Optional<Mercadoria> mercadoriaOpt = repositorioMercadoria.findById(mercadoria.getId());
                    if (mercadoriaOpt.isPresent()) {
                        mercadoriasProcessadas.add(mercadoriaOpt.get());
                    }
                }
            }
            empresa.setMercadorias(mercadoriasProcessadas);
        }
        
        if (empresa.getServicos() != null && !empresa.getServicos().isEmpty()) {
            Set<Servico> servicosProcessados = new java.util.HashSet<>();
            for (Servico servico : empresa.getServicos()) {
                if (servico.getId() != null) {
                    Optional<Servico> servicoOpt = repositorioServico.findById(servico.getId());
                    if (servicoOpt.isPresent()) {
                        servicosProcessados.add(servicoOpt.get());
                    }
                }
            }
            empresa.setServicos(servicosProcessados);
        }
        
        if (empresa.getVendas() != null && !empresa.getVendas().isEmpty()) {
            Set<Venda> vendasProcessadas = new java.util.HashSet<>();
            for (Venda venda : empresa.getVendas()) {
                if (venda.getId() != null) {
                    Optional<Venda> vendaOpt = repositorioVenda.findById(venda.getId());
                    if (vendaOpt.isPresent()) {
                        vendasProcessadas.add(vendaOpt.get());
                    }
                }
            }
            empresa.setVendas(vendasProcessadas);
        }
        
        Empresa salvo = repositorioEmpresa.save(empresa);
        
        EntityModel<Empresa> resource = EntityModel.of(salvo);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).obterEmpresa(salvo.getId())).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).listarEmpresas()).withRel("empresas"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).atualizarEmpresa(salvo.getId(), salvo)).withRel("update"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).deletarEmpresa(salvo.getId())).withRel("delete"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularUsuario(salvo.getId(), new Usuario())).withRel("empresas.usuarios"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularMercadoria(salvo.getId(), new Mercadoria())).withRel("empresas.mercadorias"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularServico(salvo.getId(), new Servico())).withRel("empresas.servicos"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularVenda(salvo.getId(), new Venda())).withRel("empresas.vendas"));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Empresa>> atualizarEmpresa(@PathVariable Long id, @RequestBody Empresa atualizacao) {
        Optional<Empresa> empresaOpt = repositorioEmpresa.findById(id);
        if (!empresaOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Empresa empresa = empresaOpt.get();

        if (atualizacao.getRazaoSocial() != null) empresa.setRazaoSocial(atualizacao.getRazaoSocial());
        if (atualizacao.getNomeFantasia() != null) empresa.setNomeFantasia(atualizacao.getNomeFantasia());
        if (atualizacao.getTelefones() != null && !atualizacao.getTelefones().isEmpty()) {
            empresa.getTelefones().clear();
            empresa.getTelefones().addAll(atualizacao.getTelefones());
        }
        if (atualizacao.getEmails() != null && !atualizacao.getEmails().isEmpty()) {
            empresa.getEmails().clear();
            empresa.getEmails().addAll(atualizacao.getEmails());
        }
        if (atualizacao.getEndereco() != null) empresa.setEndereco(atualizacao.getEndereco());
        if (atualizacao.getCadastro() != null) empresa.setCadastro(atualizacao.getCadastro());
        
        if (atualizacao.getUsuarios() != null && !atualizacao.getUsuarios().isEmpty()) {
            Set<Usuario> usuariosProcessados = new java.util.HashSet<>();
            for (Usuario usuario : atualizacao.getUsuarios()) {
                if (usuario.getId() != null) {
                    Optional<Usuario> usuarioOpt = repositorioUsuario.findById(usuario.getId());
                    if (usuarioOpt.isPresent()) {
                        usuariosProcessados.add(usuarioOpt.get());
                    }
                }
            }
            empresa.setUsuarios(usuariosProcessados);
        }
        
        if (atualizacao.getMercadorias() != null && !atualizacao.getMercadorias().isEmpty()) {
            Set<Mercadoria> mercadoriasProcessadas = new java.util.HashSet<>();
            for (Mercadoria mercadoria : atualizacao.getMercadorias()) {
                if (mercadoria.getId() != null) {
                    Optional<Mercadoria> mercadoriaOpt = repositorioMercadoria.findById(mercadoria.getId());
                    if (mercadoriaOpt.isPresent()) {
                        mercadoriasProcessadas.add(mercadoriaOpt.get());
                    }
                }
            }
            empresa.setMercadorias(mercadoriasProcessadas);
        }
        
        if (atualizacao.getServicos() != null && !atualizacao.getServicos().isEmpty()) {
            Set<Servico> servicosProcessados = new java.util.HashSet<>();
            for (Servico servico : atualizacao.getServicos()) {
                if (servico.getId() != null) {
                    Optional<Servico> servicoOpt = repositorioServico.findById(servico.getId());
                    if (servicoOpt.isPresent()) {
                        servicosProcessados.add(servicoOpt.get());
                    }
                }
            }
            empresa.setServicos(servicosProcessados);
        }
        
        if (atualizacao.getVendas() != null && !atualizacao.getVendas().isEmpty()) {
            Set<Venda> vendasProcessadas = new java.util.HashSet<>();
            for (Venda venda : atualizacao.getVendas()) {
                if (venda.getId() != null) {
                    Optional<Venda> vendaOpt = repositorioVenda.findById(venda.getId());
                    if (vendaOpt.isPresent()) {
                        vendasProcessadas.add(vendaOpt.get());
                    }
                }
            }
            empresa.setVendas(vendasProcessadas);
        }

        Empresa salvo = repositorioEmpresa.save(empresa);
        
        EntityModel<Empresa> resource = EntityModel.of(salvo);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).obterEmpresa(salvo.getId())).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).listarEmpresas()).withRel("empresas"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).criarEmpresa(salvo)).withRel("create"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).atualizarEmpresa(salvo.getId(), salvo)).withRel("update"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).deletarEmpresa(salvo.getId())).withRel("delete"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularUsuario(salvo.getId(), new Usuario())).withRel("empresas.usuarios"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularMercadoria(salvo.getId(), new Mercadoria())).withRel("empresas.mercadorias"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularServico(salvo.getId(), new Servico())).withRel("empresas.servicos"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularVenda(salvo.getId(), new Venda())).withRel("empresas.vendas"));
        
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarEmpresa(@PathVariable Long id) {
        if (!repositorioEmpresa.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        // Buscar a empresa para fazer backup histórico
        Optional<Empresa> empresaOpt = repositorioEmpresa.findById(id);
        if (empresaOpt.isPresent()) {
            Empresa empresa = empresaOpt.get();
            System.out.println("[DEBUG] Preparando backup histórico para empresa: " + empresa.getRazaoSocial());
            
            // Fazer backup de todas as vendas relacionadas
            historicoVendaService.prepararBackupEmpresa(empresa);
        }
        
        repositorioEmpresa.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // === ROTAS DE VINCULAÇÃO ===

    @PostMapping("/{id}/usuarios")
    public ResponseEntity<EntityModel<Empresa>> vincularUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        Optional<Empresa> empresaOpt = repositorioEmpresa.findById(id);
        if (!empresaOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        if (usuario.getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        
        Optional<Usuario> usuarioOpt = repositorioUsuario.findById(usuario.getId());
        if (!usuarioOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        
        Empresa empresa = empresaOpt.get();
        empresa.getUsuarios().add(usuarioOpt.get());
        Empresa salvo = repositorioEmpresa.save(empresa);
        
        EntityModel<Empresa> resource = EntityModel.of(salvo);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).obterEmpresa(salvo.getId())).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).listarEmpresas()).withRel("empresas"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularUsuario(salvo.getId(), new Usuario())).withRel("empresas.usuarios"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularMercadoria(salvo.getId(), new Mercadoria())).withRel("empresas.mercadorias"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularServico(salvo.getId(), new Servico())).withRel("empresas.servicos"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularVenda(salvo.getId(), new Venda())).withRel("empresas.vendas"));
        
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}/usuarios/{usuarioId}")
    public ResponseEntity<EntityModel<Empresa>> desvincularUsuario(@PathVariable Long id, @PathVariable Long usuarioId) {
        Optional<Empresa> empresaOpt = repositorioEmpresa.findById(id);
        if (!empresaOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Optional<Usuario> usuarioOpt = repositorioUsuario.findById(usuarioId);
        if (!usuarioOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Empresa empresa = empresaOpt.get();
        empresa.getUsuarios().removeIf(u -> u.getId().equals(usuarioId));
        Empresa salvo = repositorioEmpresa.save(empresa);
        
        EntityModel<Empresa> resource = EntityModel.of(salvo);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).obterEmpresa(salvo.getId())).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).listarEmpresas()).withRel("empresas"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularUsuario(salvo.getId(), new Usuario())).withRel("empresas.usuarios"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularMercadoria(salvo.getId(), new Mercadoria())).withRel("empresas.mercadorias"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularServico(salvo.getId(), new Servico())).withRel("empresas.servicos"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularVenda(salvo.getId(), new Venda())).withRel("empresas.vendas"));
        
        return ResponseEntity.ok(resource);
    }

    @PostMapping("/{id}/mercadorias")
    public ResponseEntity<EntityModel<Empresa>> vincularMercadoria(@PathVariable Long id, @RequestBody Mercadoria mercadoria) {
        Optional<Empresa> empresaOpt = repositorioEmpresa.findById(id);
        if (!empresaOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        if (mercadoria.getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        
        Optional<Mercadoria> mercadoriaOpt = repositorioMercadoria.findById(mercadoria.getId());
        if (!mercadoriaOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        
        Empresa empresa = empresaOpt.get();
        empresa.getMercadorias().add(mercadoriaOpt.get());
        Empresa salvo = repositorioEmpresa.save(empresa);
        
        EntityModel<Empresa> resource = EntityModel.of(salvo);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).obterEmpresa(salvo.getId())).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).listarEmpresas()).withRel("empresas"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularUsuario(salvo.getId(), new Usuario())).withRel("empresas.usuarios"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularMercadoria(salvo.getId(), new Mercadoria())).withRel("empresas.mercadorias"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularServico(salvo.getId(), new Servico())).withRel("empresas.servicos"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularVenda(salvo.getId(), new Venda())).withRel("empresas.vendas"));
        
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}/mercadorias/{mercadoriaId}")
    public ResponseEntity<EntityModel<Empresa>> desvincularMercadoria(@PathVariable Long id, @PathVariable Long mercadoriaId) {
        Optional<Empresa> empresaOpt = repositorioEmpresa.findById(id);
        if (!empresaOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Empresa empresa = empresaOpt.get();
        empresa.getMercadorias().removeIf(m -> m.getId().equals(mercadoriaId));
        Empresa salvo = repositorioEmpresa.save(empresa);
        
        EntityModel<Empresa> resource = EntityModel.of(salvo);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).obterEmpresa(salvo.getId())).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).listarEmpresas()).withRel("empresas"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularUsuario(salvo.getId(), new Usuario())).withRel("empresas.usuarios"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularMercadoria(salvo.getId(), new Mercadoria())).withRel("empresas.mercadorias"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularServico(salvo.getId(), new Servico())).withRel("empresas.servicos"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularVenda(salvo.getId(), new Venda())).withRel("empresas.vendas"));
        
        return ResponseEntity.ok(resource);
    }

    @PostMapping("/{id}/servicos")
    public ResponseEntity<EntityModel<Empresa>> vincularServico(@PathVariable Long id, @RequestBody Servico servico) {
        Optional<Empresa> empresaOpt = repositorioEmpresa.findById(id);
        if (!empresaOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        if (servico.getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        
        Optional<Servico> servicoOpt = repositorioServico.findById(servico.getId());
        if (!servicoOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        
        Empresa empresa = empresaOpt.get();
        empresa.getServicos().add(servicoOpt.get());
        Empresa salvo = repositorioEmpresa.save(empresa);
        
        EntityModel<Empresa> resource = EntityModel.of(salvo);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).obterEmpresa(salvo.getId())).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).listarEmpresas()).withRel("empresas"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularUsuario(salvo.getId(), new Usuario())).withRel("empresas.usuarios"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularMercadoria(salvo.getId(), new Mercadoria())).withRel("empresas.mercadorias"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularServico(salvo.getId(), new Servico())).withRel("empresas.servicos"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularVenda(salvo.getId(), new Venda())).withRel("empresas.vendas"));
        
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}/servicos/{servicoId}")
    public ResponseEntity<EntityModel<Empresa>> desvincularServico(@PathVariable Long id, @PathVariable Long servicoId) {
        Optional<Empresa> empresaOpt = repositorioEmpresa.findById(id);
        if (!empresaOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Empresa empresa = empresaOpt.get();
        empresa.getServicos().removeIf(s -> s.getId().equals(servicoId));
        Empresa salvo = repositorioEmpresa.save(empresa);
        
        EntityModel<Empresa> resource = EntityModel.of(salvo);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).obterEmpresa(salvo.getId())).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).listarEmpresas()).withRel("empresas"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularUsuario(salvo.getId(), new Usuario())).withRel("empresas.usuarios"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularMercadoria(salvo.getId(), new Mercadoria())).withRel("empresas.mercadorias"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularServico(salvo.getId(), new Servico())).withRel("empresas.servicos"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularVenda(salvo.getId(), new Venda())).withRel("empresas.vendas"));
        
        return ResponseEntity.ok(resource);
    }

    @PostMapping("/{id}/vendas")
    public ResponseEntity<EntityModel<Empresa>> vincularVenda(@PathVariable Long id, @RequestBody Venda venda) {
        Optional<Empresa> empresaOpt = repositorioEmpresa.findById(id);
        if (!empresaOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        if (venda.getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        
        Optional<Venda> vendaOpt = repositorioVenda.findById(venda.getId());
        if (!vendaOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        
        Empresa empresa = empresaOpt.get();
        empresa.getVendas().add(vendaOpt.get());
        Empresa salvo = repositorioEmpresa.save(empresa);
        
        EntityModel<Empresa> resource = EntityModel.of(salvo);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).obterEmpresa(salvo.getId())).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).listarEmpresas()).withRel("empresas"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularUsuario(salvo.getId(), new Usuario())).withRel("empresas.usuarios"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularMercadoria(salvo.getId(), new Mercadoria())).withRel("empresas.mercadorias"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularServico(salvo.getId(), new Servico())).withRel("empresas.servicos"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularVenda(salvo.getId(), new Venda())).withRel("empresas.vendas"));
        
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}/vendas/{vendaId}")
    public ResponseEntity<EntityModel<Empresa>> desvincularVenda(@PathVariable Long id, @PathVariable Long vendaId) {
        Optional<Empresa> empresaOpt = repositorioEmpresa.findById(id);
        if (!empresaOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Empresa empresa = empresaOpt.get();
        empresa.getVendas().removeIf(v -> v.getId().equals(vendaId));
        Empresa salvo = repositorioEmpresa.save(empresa);
        
        EntityModel<Empresa> resource = EntityModel.of(salvo);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).obterEmpresa(salvo.getId())).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).listarEmpresas()).withRel("empresas"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularUsuario(salvo.getId(), new Usuario())).withRel("empresas.usuarios"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularMercadoria(salvo.getId(), new Mercadoria())).withRel("empresas.mercadorias"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularServico(salvo.getId(), new Servico())).withRel("empresas.servicos"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EmpresaControle.class).vincularVenda(salvo.getId(), new Venda())).withRel("empresas.vendas"));
        
        return ResponseEntity.ok(resource);
    }
} 