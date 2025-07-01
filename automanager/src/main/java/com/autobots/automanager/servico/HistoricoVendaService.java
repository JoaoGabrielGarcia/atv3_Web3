package com.autobots.automanager.servico;

import java.util.List;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Veiculo;
import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.entidades.Servico;
import com.autobots.automanager.repositorios.RepositorioVenda;

@Service
public class HistoricoVendaService {
    
    @Autowired
    private RepositorioVenda repositorioVenda;
    
    /**
     * Atualiza o backup de todas as vendas relacionadas a um usuário antes da exclusão
     */
    @Transactional
    public void prepararBackupUsuario(Usuario usuario) {
        System.out.println("[HISTORICO] Preparando backup para usuário: " + usuario.getNome() + " (ID: " + usuario.getId() + ")");
        
        // Buscar vendas onde o usuário é cliente
        List<Venda> vendasCliente = repositorioVenda.findByClienteId(usuario.getId());
        for (Venda venda : vendasCliente) {
            System.out.println("[HISTORICO] Atualizando backup de cliente na venda ID: " + venda.getId());
            venda.atualizarBackupCliente();
            venda.setCliente(null); // Limpar relacionamento
            repositorioVenda.save(venda);
        }
        
        // Buscar vendas onde o usuário é funcionário
        List<Venda> vendasFuncionario = repositorioVenda.findByFuncionarioId(usuario.getId());
        for (Venda venda : vendasFuncionario) {
            System.out.println("[HISTORICO] Atualizando backup de funcionário na venda ID: " + venda.getId());
            venda.atualizarBackupFuncionario();
            venda.setFuncionario(null); // Limpar relacionamento
            repositorioVenda.save(venda);
        }
    }
    
    /**
     * Atualiza o backup de todas as vendas relacionadas a um veículo antes da exclusão
     */
    @Transactional
    public void prepararBackupVeiculo(Veiculo veiculo) {
        System.out.println("[HISTORICO] Preparando backup para veículo: " + veiculo.getModelo() + " (ID: " + veiculo.getId() + ")");
        
        List<Venda> vendas = repositorioVenda.findByVeiculoId(veiculo.getId());
        for (Venda venda : vendas) {
            System.out.println("[HISTORICO] Atualizando backup de veículo na venda ID: " + venda.getId());
            venda.atualizarBackupVeiculo();
            venda.setVeiculo(null); // Limpar relacionamento
            repositorioVenda.save(venda);
        }
    }
    
    /**
     * Atualiza o backup de todas as vendas relacionadas a uma empresa antes da exclusão
     */
    @Transactional
    public void prepararBackupEmpresa(Empresa empresa) {
        System.out.println("[HISTORICO] Preparando backup para empresa: " + empresa.getRazaoSocial() + " (ID: " + empresa.getId() + ")");
        
        List<Venda> vendas = repositorioVenda.findByEmpresaId(empresa.getId());
        for (Venda venda : vendas) {
            System.out.println("[HISTORICO] Atualizando backup de empresa na venda ID: " + venda.getId());
            venda.atualizarBackupEmpresa();
            venda.setEmpresa(null); // Limpar relacionamento
            repositorioVenda.save(venda);
        }
    }
    
    /**
     * Atualiza o backup de todas as vendas relacionadas a uma mercadoria antes da exclusão
     */
    @Transactional
    public void prepararBackupMercadoria(Mercadoria mercadoria) {
        System.out.println("[HISTORICO] Preparando backup para mercadoria: " + mercadoria.getNome() + " (ID: " + mercadoria.getId() + ")");
        
        List<Venda> vendas = repositorioVenda.findByMercadoriaId(mercadoria.getId());
        for (Venda venda : vendas) {
            System.out.println("[HISTORICO] Atualizando backup de mercadorias na venda ID: " + venda.getId());
            // Preservar backup existente e adicionar a mercadoria que está sendo excluída
            if (venda.getBackup() == null) {
                venda.setBackup(new Venda.Backup());
            }
            if (venda.getBackup().getMercadorias() == null) {
                venda.getBackup().setMercadorias(new HashSet<>());
            }
            
            // Adicionar a mercadoria que está sendo excluída ao backup
            venda.getBackup().getMercadorias().add(new Venda.Backup.MercadoriaBackup(
                mercadoria.getId(), 
                mercadoria.getNome(), 
                mercadoria.getValor(), 
                mercadoria.getDescricao()
            ));
            
            venda.getMercadorias().remove(mercadoria); // Remover da lista de relacionamentos
            repositorioVenda.save(venda);
        }
    }
    
    /**
     * Atualiza o backup de todas as vendas relacionadas a um serviço antes da exclusão
     */
    @Transactional
    public void prepararBackupServico(Servico servico) {
        System.out.println("[HISTORICO] Preparando backup para serviço: " + servico.getNome() + " (ID: " + servico.getId() + ")");
        
        List<Venda> vendas = repositorioVenda.findByServicoId(servico.getId());
        for (Venda venda : vendas) {
            System.out.println("[HISTORICO] Atualizando backup de serviços na venda ID: " + venda.getId());
            // Preservar backup existente e adicionar o serviço que está sendo excluído
            if (venda.getBackup() == null) {
                venda.setBackup(new Venda.Backup());
            }
            if (venda.getBackup().getServicos() == null) {
                venda.getBackup().setServicos(new HashSet<>());
            }
            
            // Adicionar o serviço que está sendo excluído ao backup
            venda.getBackup().getServicos().add(new Venda.Backup.ServicoBackup(
                servico.getId(), 
                servico.getNome(), 
                servico.getValor(), 
                servico.getDescricao()
            ));
            
            venda.getServicos().remove(servico); // Remover da lista de relacionamentos
            repositorioVenda.save(venda);
        }
    }
} 