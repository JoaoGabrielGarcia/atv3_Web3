package com.autobots.automanager.controles;

import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Veiculo;
import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.entidades.Servico;
import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.repositorios.RepositorioVenda;
import com.autobots.automanager.repositorios.RepositorioUsuario;
import com.autobots.automanager.repositorios.RepositorioVeiculo;
import com.autobots.automanager.repositorios.RepositorioMercadoria;
import com.autobots.automanager.repositorios.RepositorioServico;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.enumeracoes.PerfilUsuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vendas")
public class VendaControle {
    @Autowired
    private RepositorioVenda repositorioVenda;
    
    @Autowired
    private RepositorioUsuario repositorioUsuario;
    
    @Autowired
    private RepositorioVeiculo repositorioVeiculo;
    
    @Autowired
    private RepositorioMercadoria repositorioMercadoria;
    
    @Autowired
    private RepositorioServico repositorioServico;
    
    @Autowired
    private RepositorioEmpresa repositorioEmpresa;

    @GetMapping
    public CollectionModel<EntityModel<Venda>> listarVendas() {
        List<EntityModel<Venda>> vendas = repositorioVenda.findAll().stream()
            .map(venda -> {
                EntityModel<Venda> resource = EntityModel.of(venda);
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VendaControle.class).obterVenda(venda.getId())).withSelfRel());
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VendaControle.class).listarVendas()).withRel("vendas"));
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VendaControle.class).criarVenda(venda)).withRel("create"));
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VendaControle.class).atualizarVenda(venda.getId(), venda)).withRel("update"));
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VendaControle.class).deletarVenda(venda.getId(), null)).withRel("delete"));
                return resource;
            })
            .collect(Collectors.toList());
        
        CollectionModel<EntityModel<Venda>> collection = CollectionModel.of(vendas);
        collection.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VendaControle.class).listarVendas()).withSelfRel());
        collection.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VendaControle.class).criarVenda(new Venda())).withRel("create"));
        return collection;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Venda>> obterVenda(@PathVariable Long id) {
        Optional<Venda> venda = repositorioVenda.findById(id);
        if (venda.isPresent()) {
            EntityModel<Venda> resource = EntityModel.of(venda.get());
            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VendaControle.class).obterVenda(id)).withSelfRel();
            resource.add(selfLink);
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VendaControle.class).listarVendas()).withRel("vendas"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VendaControle.class).criarVenda(venda.get())).withRel("create"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VendaControle.class).atualizarVenda(id, venda.get())).withRel("update"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VendaControle.class).deletarVenda(id, null)).withRel("delete"));
            return ResponseEntity.ok(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<EntityModel<Venda>> criarVenda(@RequestBody Venda venda) {
        System.out.println("[DEBUG] Criando venda com identificação: " + venda.getIdentificacao());
        
        // Verificar se já existe uma venda com a mesma identificação
        if (venda.getIdentificacao() != null) {
            Optional<Venda> vendaExistente = repositorioVenda.findByIdentificacao(venda.getIdentificacao());
            if (vendaExistente.isPresent()) {
                System.out.println("[DEBUG] Erro: já existe uma venda com a identificação: " + venda.getIdentificacao());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            }
        }
        
        // Validar se empresa foi fornecida
        if (venda.getEmpresa() != null && venda.getEmpresa().getId() != null) {
            Optional<Empresa> empresaOpt = repositorioEmpresa.findById(venda.getEmpresa().getId());
            if (!empresaOpt.isPresent()) {
                System.out.println("[DEBUG] Erro: empresa com ID " + venda.getEmpresa().getId() + " não encontrada");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            venda.setEmpresa(empresaOpt.get());
        }
        
        // Validar se cliente foi fornecido 
        if (venda.getCliente() != null && venda.getCliente().getId() != null) {
            Optional<Usuario> clienteOpt = repositorioUsuario.findById(venda.getCliente().getId());
            if (!clienteOpt.isPresent()) {
                System.out.println("[DEBUG] Erro: cliente com ID " + venda.getCliente().getId() + " não encontrado");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            venda.setCliente(clienteOpt.get());
        }
        
        // Validar se funcionário foi fornecido 
        if (venda.getFuncionario() != null && venda.getFuncionario().getId() != null) {
            Optional<Usuario> funcionarioOpt = repositorioUsuario.findById(venda.getFuncionario().getId());
            if (!funcionarioOpt.isPresent()) {
                System.out.println("[DEBUG] Erro: funcionário com ID " + venda.getFuncionario().getId() + " não encontrado");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            venda.setFuncionario(funcionarioOpt.get());
        }
        
        // Validar se pelo menos uma mercadoria OU um serviço foi fornecido
        boolean temMercadorias = venda.getMercadorias() != null && !venda.getMercadorias().isEmpty();
        boolean temServicos = venda.getServicos() != null && !venda.getServicos().isEmpty();
        
        if (!temMercadorias && !temServicos) {
            System.out.println("[DEBUG] Erro: nenhuma mercadoria ou serviço fornecido");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        
        try {
            // Processar veículo referenciado por ID (opcional)
            if (venda.getVeiculo() != null && venda.getVeiculo().getId() != null) {
                Optional<Veiculo> veiculoOpt = repositorioVeiculo.findById(venda.getVeiculo().getId());
                if (veiculoOpt.isPresent()) {
                    venda.setVeiculo(veiculoOpt.get());
                } else {
                    System.out.println("[DEBUG] Erro: veículo com ID " + venda.getVeiculo().getId() + " não encontrado");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                }
            }
            
            // Processar mercadorias referenciadas por ID
            if (temMercadorias) {
                Set<Mercadoria> mercadoriasProcessadas = new java.util.HashSet<>();
                for (Mercadoria mercadoria : venda.getMercadorias()) {
                    if (mercadoria.getId() != null) {
                        Optional<Mercadoria> mercadoriaOpt = repositorioMercadoria.findById(mercadoria.getId());
                        if (mercadoriaOpt.isPresent()) {
                            mercadoriasProcessadas.add(mercadoriaOpt.get());
                        } else {
                            System.out.println("[DEBUG] Erro: mercadoria com ID " + mercadoria.getId() + " não encontrada");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                        }
                    }
                }
                venda.setMercadorias(mercadoriasProcessadas);
            }
            
            // Processar serviços referenciados por ID
            if (temServicos) {
                Set<Servico> servicosProcessados = new java.util.HashSet<>();
                for (Servico servico : venda.getServicos()) {
                    if (servico.getId() != null) {
                        Optional<Servico> servicoOpt = repositorioServico.findById(servico.getId());
                        if (servicoOpt.isPresent()) {
                            servicosProcessados.add(servicoOpt.get());
                        } else {
                            System.out.println("[DEBUG] Erro: serviço com ID " + servico.getId() + " não encontrado");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                        }
                    }
                }
                venda.setServicos(servicosProcessados);
            }
            
            // === FAZER BACKUP AUTOMÁTICO DOS DADOS ===
            venda.atualizarBackupCliente();
            venda.atualizarBackupFuncionario();
            venda.atualizarBackupVeiculo();
            venda.atualizarBackupEmpresa();
            venda.atualizarBackupMercadorias();
            venda.atualizarBackupServicos();
            System.out.println("[DEBUG] Backup preenchido: " + venda.getBackup());
            
            System.out.println("[DEBUG] Salvando venda...");
            Venda salvo = repositorioVenda.save(venda);
            System.out.println("[DEBUG] Venda salva com sucesso, ID: " + salvo.getId());
            
            EntityModel<Venda> resource = EntityModel.of(salvo);
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VendaControle.class).obterVenda(salvo.getId())).withSelfRel());
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VendaControle.class).listarVendas()).withRel("vendas"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VendaControle.class).atualizarVenda(salvo.getId(), salvo)).withRel("update"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VendaControle.class).deletarVenda(salvo.getId(), null)).withRel("delete"));
            
            return ResponseEntity.status(HttpStatus.CREATED).body(resource);
            
        } catch (Exception e) {
            System.out.println("[DEBUG] Erro ao salvar venda: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Venda>> atualizarVenda(@PathVariable Long id, @RequestBody Venda atualizacao) {
        Optional<Venda> vendaOpt = repositorioVenda.findById(id);
        if (!vendaOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Venda venda = vendaOpt.get();

        if (atualizacao.getCadastro() != null) venda.setCadastro(atualizacao.getCadastro());
        if (atualizacao.getIdentificacao() != null) venda.setIdentificacao(atualizacao.getIdentificacao());
        
        // Validar se empresa foi fornecida 
        if (atualizacao.getEmpresa() != null && atualizacao.getEmpresa().getId() != null) {
            Optional<Empresa> empresaOpt = repositorioEmpresa.findById(atualizacao.getEmpresa().getId());
            if (!empresaOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            venda.setEmpresa(empresaOpt.get());
        }
        
        // Validar se cliente foi fornecido 
        if (atualizacao.getCliente() != null && atualizacao.getCliente().getId() != null) {
            Optional<Usuario> clienteOpt = repositorioUsuario.findById(atualizacao.getCliente().getId());
            if (!clienteOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            venda.setCliente(clienteOpt.get());
        }
        
        // Validar se funcionário foi fornecido 
        if (atualizacao.getFuncionario() != null && atualizacao.getFuncionario().getId() != null) {
            Optional<Usuario> funcionarioOpt = repositorioUsuario.findById(atualizacao.getFuncionario().getId());
            if (!funcionarioOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            venda.setFuncionario(funcionarioOpt.get());
        }
        
        // Validar se pelo menos uma mercadoria OU um serviço foi fornecido
        boolean temMercadorias = atualizacao.getMercadorias() != null && !atualizacao.getMercadorias().isEmpty();
        boolean temServicos = atualizacao.getServicos() != null && !atualizacao.getServicos().isEmpty();
        
        if (!temMercadorias && !temServicos) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        
        // Processar veículo referenciado por ID (opcional)
        if (atualizacao.getVeiculo() != null && atualizacao.getVeiculo().getId() != null) {
            Optional<Veiculo> veiculoOpt = repositorioVeiculo.findById(atualizacao.getVeiculo().getId());
            if (veiculoOpt.isPresent()) {
                venda.setVeiculo(veiculoOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        } else {
            venda.setVeiculo(null); // Remove veículo se não foi fornecido
        }
        
        // Processar mercadorias referenciadas por ID
        if (temMercadorias) {
            Set<Mercadoria> mercadoriasProcessadas = new java.util.HashSet<>();
            for (Mercadoria mercadoria : atualizacao.getMercadorias()) {
                if (mercadoria.getId() != null) {
                    Optional<Mercadoria> mercadoriaOpt = repositorioMercadoria.findById(mercadoria.getId());
                    if (mercadoriaOpt.isPresent()) {
                        mercadoriasProcessadas.add(mercadoriaOpt.get());
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                    }
                }
            }
            venda.setMercadorias(mercadoriasProcessadas);
        }
        
        // Processar serviços referenciados por ID
        if (temServicos) {
            Set<Servico> servicosProcessados = new java.util.HashSet<>();
            for (Servico servico : atualizacao.getServicos()) {
                if (servico.getId() != null) {
                    Optional<Servico> servicoOpt = repositorioServico.findById(servico.getId());
                    if (servicoOpt.isPresent()) {
                        servicosProcessados.add(servicoOpt.get());
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                    }
                }
            }
            venda.setServicos(servicosProcessados);
        }

        venda.atualizarBackupCliente();
        venda.atualizarBackupFuncionario();
        venda.atualizarBackupVeiculo();
        venda.atualizarBackupEmpresa();
        venda.atualizarBackupMercadorias();
        venda.atualizarBackupServicos();
        System.out.println("[DEBUG] Backup preenchido: " + venda.getBackup());

        Venda salvo = repositorioVenda.save(venda);
        
        EntityModel<Venda> resource = EntityModel.of(salvo);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VendaControle.class).obterVenda(salvo.getId())).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VendaControle.class).listarVendas()).withRel("vendas"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VendaControle.class).criarVenda(salvo)).withRel("create"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VendaControle.class).atualizarVenda(salvo.getId(), salvo)).withRel("update"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(VendaControle.class).deletarVenda(salvo.getId(), null)).withRel("delete"));
        
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarVenda(@PathVariable Long id, Authentication authentication) {
        if (!repositorioVenda.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        // Verificar se o usuário tem permissão pra deletar
        if (authentication != null) {
            String email = authentication.getName();
            Optional<Usuario> usuarioOpt = repositorioUsuario.findByEmail(email);
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                if (!usuario.getPerfis().contains(PerfilUsuario.ADMINISTRADOR) && 
                    !usuario.getPerfis().contains(PerfilUsuario.GERENTE)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
        }
        
        repositorioVenda.deleteById(id);
        return ResponseEntity.noContent().build();
    }
} 