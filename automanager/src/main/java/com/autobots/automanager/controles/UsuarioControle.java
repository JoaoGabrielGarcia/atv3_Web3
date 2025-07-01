package com.autobots.automanager.controles;

import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.CredencialUsuarioSenha;
import com.autobots.automanager.entidades.Veiculo;
import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.repositorios.RepositorioUsuario;
import com.autobots.automanager.repositorios.RepositorioVeiculo;
import com.autobots.automanager.repositorios.RepositorioMercadoria;
import com.autobots.automanager.repositorios.RepositorioVenda;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.enumeracoes.PerfilUsuario;
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
@RequestMapping("/usuarios")
public class UsuarioControle {
    @Autowired
    private RepositorioUsuario repositorioUsuario;
    
    @Autowired
    private RepositorioVeiculo repositorioVeiculo;
    
    @Autowired
    private RepositorioMercadoria repositorioMercadoria;
    
    @Autowired
    private RepositorioVenda repositorioVenda;
    
    @Autowired
    private RepositorioEmpresa repositorioEmpresa;

    @Autowired
    private HistoricoVendaService historicoVendaService;

    @GetMapping
    public CollectionModel<EntityModel<Usuario>> listarUsuarios() {
        List<EntityModel<Usuario>> usuarios = repositorioUsuario.findAll().stream()
            .map(usuario -> {
                EntityModel<Usuario> resource = EntityModel.of(usuario);
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).obterUsuario(usuario.getId())).withSelfRel());
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).listarUsuarios()).withRel("usuarios"));
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).criarUsuario(usuario)).withRel("create"));
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).atualizarUsuario(usuario.getId(), usuario)).withRel("update"));
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).deletarUsuario(usuario.getId())).withRel("delete"));
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).vincularVeiculo(usuario.getId(), new Veiculo())).withRel("usuarios.veiculos"));
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).vincularMercadoria(usuario.getId(), new Mercadoria())).withRel("usuarios.mercadorias"));
                resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).vincularVenda(usuario.getId(), new Venda())).withRel("usuarios.vendas"));
                return resource;
            })
            .collect(Collectors.toList());
        
        CollectionModel<EntityModel<Usuario>> collection = CollectionModel.of(usuarios);
        collection.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).listarUsuarios()).withSelfRel());
        collection.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).criarUsuario(new Usuario())).withRel("create"));
        return collection;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> obterUsuario(@PathVariable Long id) {
        Optional<Usuario> usuario = repositorioUsuario.findById(id);
        if (usuario.isPresent()) {
            EntityModel<Usuario> resource = EntityModel.of(usuario.get());
            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).obterUsuario(id)).withSelfRel();
            resource.add(selfLink);
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).listarUsuarios()).withRel("usuarios"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).criarUsuario(usuario.get())).withRel("create"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).atualizarUsuario(id, usuario.get())).withRel("update"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).deletarUsuario(id)).withRel("delete"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).vincularVeiculo(id, new Veiculo())).withRel("usuarios.veiculos"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).vincularMercadoria(id, new Mercadoria())).withRel("usuarios.mercadorias"));
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).vincularVenda(id, new Venda())).withRel("usuarios.vendas"));
            return ResponseEntity.ok(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<EntityModel<Usuario>> criarUsuario(@RequestBody Usuario usuario) {
        
        String email = usuario.getEmail();
        Set<CredencialUsuarioSenha> credenciais = usuario.getCredenciais().stream()
                .filter(c -> c instanceof CredencialUsuarioSenha)
                .map(c -> (CredencialUsuarioSenha) c)
                .collect(java.util.stream.Collectors.toSet());
        if (email == null || email.isEmpty() || credenciais.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String senha = credenciais.iterator().next().getSenha();
        String credEmail = credenciais.iterator().next().getEmail();
        if (senha == null || senha.isEmpty() || credEmail == null || credEmail.isEmpty() || !credEmail.equals(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        
        // Validar se empresa foi fornecida
        if (usuario.getEmpresa() == null || usuario.getEmpresa().getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        
        // Buscar e validar empresa
        Optional<Empresa> empresaOpt = repositorioEmpresa.findById(usuario.getEmpresa().getId());
        if (!empresaOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        usuario.setEmpresa(empresaOpt.get());
        
        if (usuario.getVeiculos() != null && !usuario.getVeiculos().isEmpty()) {
            Set<Veiculo> veiculosProcessados = new java.util.HashSet<>();
            for (Veiculo veiculo : usuario.getVeiculos()) {
                if (veiculo.getId() != null) {
                    Optional<Veiculo> veiculoOpt = repositorioVeiculo.findById(veiculo.getId());
                    if (veiculoOpt.isPresent()) {
                        veiculosProcessados.add(veiculoOpt.get());
                    }
                }
            }
            usuario.setVeiculos(veiculosProcessados);
        }
        
        if (usuario.getMercadorias() != null && !usuario.getMercadorias().isEmpty()) {
            Set<Mercadoria> mercadoriasProcessadas = new java.util.HashSet<>();
            for (Mercadoria mercadoria : usuario.getMercadorias()) {
                if (mercadoria.getId() != null) {
                    Optional<Mercadoria> mercadoriaOpt = repositorioMercadoria.findById(mercadoria.getId());
                    if (mercadoriaOpt.isPresent()) {
                        mercadoriasProcessadas.add(mercadoriaOpt.get());
                    }
                }
            }
            usuario.setMercadorias(mercadoriasProcessadas);
        }
        
        Usuario salvo = repositorioUsuario.save(usuario);
        
        EntityModel<Usuario> resource = EntityModel.of(salvo);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).obterUsuario(salvo.getId())).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).listarUsuarios()).withRel("usuarios"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).atualizarUsuario(salvo.getId(), salvo)).withRel("update"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).deletarUsuario(salvo.getId())).withRel("delete"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).vincularVeiculo(salvo.getId(), new Veiculo())).withRel("usuarios.veiculos"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).vincularMercadoria(salvo.getId(), new Mercadoria())).withRel("usuarios.mercadorias"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).vincularVenda(salvo.getId(), new Venda())).withRel("usuarios.vendas"));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario atualizacao) {
        Optional<Usuario> usuarioOpt = repositorioUsuario.findById(id);
        if (!usuarioOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Usuario usuario = usuarioOpt.get();

        if (atualizacao.getNome() != null) usuario.setNome(atualizacao.getNome());
        if (atualizacao.getNomeSocial() != null) usuario.setNomeSocial(atualizacao.getNomeSocial());
        if (atualizacao.getPerfis() != null && !atualizacao.getPerfis().isEmpty()) usuario.setPerfis(atualizacao.getPerfis());
        if (atualizacao.getTelefones() != null && !atualizacao.getTelefones().isEmpty()) {
            usuario.getTelefones().clear();
            usuario.getTelefones().addAll(atualizacao.getTelefones());
        }
        if (atualizacao.getEmail() != null) usuario.setEmail(atualizacao.getEmail());
        if (atualizacao.getEndereco() != null) usuario.setEndereco(atualizacao.getEndereco());
        if (atualizacao.getDocumentos() != null && !atualizacao.getDocumentos().isEmpty()) {
            usuario.getDocumentos().clear();
            usuario.getDocumentos().addAll(atualizacao.getDocumentos());
        }
        if (atualizacao.getCredenciais() != null && !atualizacao.getCredenciais().isEmpty()) {
            usuario.getCredenciais().clear();
            usuario.getCredenciais().addAll(atualizacao.getCredenciais());
        }
        
        // Validar e processar empresa (obrigatório)
        if (atualizacao.getEmpresa() == null || atualizacao.getEmpresa().getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        
        Optional<Empresa> empresaOpt = repositorioEmpresa.findById(atualizacao.getEmpresa().getId());
        if (!empresaOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        usuario.setEmpresa(empresaOpt.get());
        
        if (atualizacao.getVeiculos() != null && !atualizacao.getVeiculos().isEmpty()) {
            Set<Veiculo> veiculosProcessados = new java.util.HashSet<>();
            for (Veiculo veiculo : atualizacao.getVeiculos()) {
                if (veiculo.getId() != null) {
                    Optional<Veiculo> veiculoOpt = repositorioVeiculo.findById(veiculo.getId());
                    if (veiculoOpt.isPresent()) {
                        veiculosProcessados.add(veiculoOpt.get());
                    }
                }
            }
            usuario.setVeiculos(veiculosProcessados);
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
            usuario.setMercadorias(mercadoriasProcessadas);
        }
        
        if (atualizacao.getVendas() != null && !atualizacao.getVendas().isEmpty()) {
            usuario.getVendas().clear();
            usuario.getVendas().addAll(atualizacao.getVendas());
        }

        Usuario salvo = repositorioUsuario.save(usuario);
        
        EntityModel<Usuario> resource = EntityModel.of(salvo);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).obterUsuario(salvo.getId())).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).listarUsuarios()).withRel("usuarios"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).criarUsuario(salvo)).withRel("create"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).atualizarUsuario(salvo.getId(), salvo)).withRel("update"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).deletarUsuario(salvo.getId())).withRel("delete"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).vincularVeiculo(salvo.getId(), new Veiculo())).withRel("usuarios.veiculos"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).vincularMercadoria(salvo.getId(), new Mercadoria())).withRel("usuarios.mercadorias"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).vincularVenda(salvo.getId(), new Venda())).withRel("usuarios.vendas"));
        
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        if (!repositorioUsuario.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        // Buscar o usuário para fazer backup histórico
        Optional<Usuario> usuarioOpt = repositorioUsuario.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            System.out.println("[DEBUG] Preparando backup histórico para usuário: " + usuario.getNome());
            
            // Fazer backup de todas as vendas relacionadas
            historicoVendaService.prepararBackupUsuario(usuario);
        }
        
        repositorioUsuario.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // === ROTAS DE VINCULAÇÃO ===

    @PostMapping("/{id}/veiculos")
    public ResponseEntity<EntityModel<Usuario>> vincularVeiculo(@PathVariable Long id, @RequestBody Veiculo veiculo) {
        Optional<Usuario> usuarioOpt = repositorioUsuario.findById(id);
        if (!usuarioOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        if (veiculo.getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        
        Optional<Veiculo> veiculoOpt = repositorioVeiculo.findById(veiculo.getId());
        if (!veiculoOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        
        Usuario usuario = usuarioOpt.get();
        usuario.getVeiculos().add(veiculoOpt.get());
        Usuario salvo = repositorioUsuario.save(usuario);
        
        EntityModel<Usuario> resource = EntityModel.of(salvo);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).obterUsuario(salvo.getId())).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).listarUsuarios()).withRel("usuarios"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).vincularVeiculo(salvo.getId(), new Veiculo())).withRel("usuarios.veiculos"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).vincularMercadoria(salvo.getId(), new Mercadoria())).withRel("usuarios.mercadorias"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).vincularVenda(salvo.getId(), new Venda())).withRel("usuarios.vendas"));
        
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}/veiculos/{veiculoId}")
    public ResponseEntity<EntityModel<Usuario>> desvincularVeiculo(@PathVariable Long id, @PathVariable Long veiculoId) {
        Optional<Usuario> usuarioOpt = repositorioUsuario.findById(id);
        if (!usuarioOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Optional<Veiculo> veiculoOpt = repositorioVeiculo.findById(veiculoId);
        if (!veiculoOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Usuario usuario = usuarioOpt.get();
        usuario.getVeiculos().removeIf(v -> v.getId().equals(veiculoId));
        Usuario salvo = repositorioUsuario.save(usuario);
        
        EntityModel<Usuario> resource = EntityModel.of(salvo);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).obterUsuario(salvo.getId())).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).listarUsuarios()).withRel("usuarios"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).vincularVeiculo(salvo.getId(), new Veiculo())).withRel("usuarios.veiculos"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).vincularMercadoria(salvo.getId(), new Mercadoria())).withRel("usuarios.mercadorias"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).vincularVenda(salvo.getId(), new Venda())).withRel("usuarios.vendas"));
        
        return ResponseEntity.ok(resource);
    }

    @PostMapping("/{id}/mercadorias")
    public ResponseEntity<EntityModel<Usuario>> vincularMercadoria(@PathVariable Long id, @RequestBody Mercadoria mercadoria) {
        Optional<Usuario> usuarioOpt = repositorioUsuario.findById(id);
        if (!usuarioOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        // Verificar se o usuário é fornecedor
        Usuario usuario = usuarioOpt.get();
        if (!usuario.getPerfis().contains(PerfilUsuario.FORNECEDOR)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        
        if (mercadoria.getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        
        Optional<Mercadoria> mercadoriaOpt = repositorioMercadoria.findById(mercadoria.getId());
        if (!mercadoriaOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        
        usuario.getMercadorias().add(mercadoriaOpt.get());
        Usuario salvo = repositorioUsuario.save(usuario);
        
        EntityModel<Usuario> resource = EntityModel.of(salvo);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).obterUsuario(salvo.getId())).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).listarUsuarios()).withRel("usuarios"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).vincularVeiculo(salvo.getId(), new Veiculo())).withRel("usuarios.veiculos"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).vincularMercadoria(salvo.getId(), new Mercadoria())).withRel("usuarios.mercadorias"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).vincularVenda(salvo.getId(), new Venda())).withRel("usuarios.vendas"));
        
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}/mercadorias/{mercadoriaId}")
    public ResponseEntity<EntityModel<Usuario>> desvincularMercadoria(@PathVariable Long id, @PathVariable Long mercadoriaId) {
        Optional<Usuario> usuarioOpt = repositorioUsuario.findById(id);
        if (!usuarioOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Usuario usuario = usuarioOpt.get();
        if (!usuario.getPerfis().contains(PerfilUsuario.FORNECEDOR)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        
        usuario.getMercadorias().removeIf(m -> m.getId().equals(mercadoriaId));
        Usuario salvo = repositorioUsuario.save(usuario);
        
        EntityModel<Usuario> resource = EntityModel.of(salvo);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).obterUsuario(salvo.getId())).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).listarUsuarios()).withRel("usuarios"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).vincularVeiculo(salvo.getId(), new Veiculo())).withRel("usuarios.veiculos"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).vincularMercadoria(salvo.getId(), new Mercadoria())).withRel("usuarios.mercadorias"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).vincularVenda(salvo.getId(), new Venda())).withRel("usuarios.vendas"));
        
        return ResponseEntity.ok(resource);
    }

    @PostMapping("/{id}/vendas")
    public ResponseEntity<EntityModel<Usuario>> vincularVenda(@PathVariable Long id, @RequestBody Venda venda) {
        Optional<Usuario> usuarioOpt = repositorioUsuario.findById(id);
        if (!usuarioOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        // Verificar se o usuário é funcionário ou gerente
        Usuario usuario = usuarioOpt.get();
        if (!usuario.getPerfis().contains(PerfilUsuario.FUNCIONARIO) && 
            !usuario.getPerfis().contains(PerfilUsuario.GERENTE)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        
        if (venda.getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        
        Optional<Venda> vendaOpt = repositorioVenda.findById(venda.getId());
        if (!vendaOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        
        usuario.getVendas().add(vendaOpt.get());
        Usuario salvo = repositorioUsuario.save(usuario);
        
        EntityModel<Usuario> resource = EntityModel.of(salvo);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).obterUsuario(salvo.getId())).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).listarUsuarios()).withRel("usuarios"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).vincularVeiculo(salvo.getId(), new Veiculo())).withRel("usuarios.veiculos"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).vincularMercadoria(salvo.getId(), new Mercadoria())).withRel("usuarios.mercadorias"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).vincularVenda(salvo.getId(), new Venda())).withRel("usuarios.vendas"));
        
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}/vendas/{vendaId}")
    public ResponseEntity<EntityModel<Usuario>> desvincularVenda(@PathVariable Long id, @PathVariable Long vendaId) {
        Optional<Usuario> usuarioOpt = repositorioUsuario.findById(id);
        if (!usuarioOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Usuario usuario = usuarioOpt.get();
        if (!usuario.getPerfis().contains(PerfilUsuario.FUNCIONARIO) && 
            !usuario.getPerfis().contains(PerfilUsuario.GERENTE)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        
        usuario.getVendas().removeIf(v -> v.getId().equals(vendaId));
        Usuario salvo = repositorioUsuario.save(usuario);
        
        EntityModel<Usuario> resource = EntityModel.of(salvo);
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).obterUsuario(salvo.getId())).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).listarUsuarios()).withRel("usuarios"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).vincularVeiculo(salvo.getId(), new Veiculo())).withRel("usuarios.veiculos"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).vincularMercadoria(salvo.getId(), new Mercadoria())).withRel("usuarios.mercadorias"));
        resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UsuarioControle.class).vincularVenda(salvo.getId(), new Venda())).withRel("usuarios.vendas"));
        
        return ResponseEntity.ok(resource);
    }
} 