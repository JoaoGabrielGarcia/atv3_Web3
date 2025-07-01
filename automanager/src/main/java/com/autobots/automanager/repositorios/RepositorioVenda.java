package com.autobots.automanager.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.autobots.automanager.entidades.Venda;
import java.util.Optional;
import java.util.List;

public interface RepositorioVenda extends JpaRepository<Venda, Long> {
    Optional<Venda> findByIdentificacao(String identificacao);
    
    @EntityGraph(attributePaths = { "cliente", "funcionario", "empresa", "mercadorias", "servicos", "veiculo" })
    List<Venda> findAll();
    
    Optional<Venda> findById(Long id);
    
    // === MÉTODOS PARA BACKUP HISTÓRICO ===
    
    @Query("SELECT v FROM Venda v WHERE v.cliente.id = :clienteId")
    List<Venda> findByClienteId(@Param("clienteId") Long clienteId);
    
    @Query("SELECT v FROM Venda v WHERE v.funcionario.id = :funcionarioId")
    List<Venda> findByFuncionarioId(@Param("funcionarioId") Long funcionarioId);
    
    @Query("SELECT v FROM Venda v WHERE v.veiculo.id = :veiculoId")
    List<Venda> findByVeiculoId(@Param("veiculoId") Long veiculoId);
    
    @Query("SELECT v FROM Venda v WHERE v.empresa.id = :empresaId")
    List<Venda> findByEmpresaId(@Param("empresaId") Long empresaId);
    
    @Query("SELECT DISTINCT v FROM Venda v JOIN v.mercadorias m WHERE m.id = :mercadoriaId")
    List<Venda> findByMercadoriaId(@Param("mercadoriaId") Long mercadoriaId);
    
    @Query("SELECT DISTINCT v FROM Venda v JOIN v.servicos s WHERE s.id = :servicoId")
    List<Venda> findByServicoId(@Param("servicoId") Long servicoId);
} 