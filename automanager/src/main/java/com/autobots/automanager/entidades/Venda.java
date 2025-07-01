package com.autobots.automanager.entidades;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(exclude = { "cliente", "funcionario", "veiculo", "empresa" })
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Venda {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private Date cadastro;
	@Column(nullable = false, unique = true)
	private String identificacao;
	
	// === RELACIONAMENTOS ATIVOS (OPCIONAIS) ===
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "cliente_id", nullable = true)
	private Usuario cliente;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "funcionario_id", nullable = true)
	private Usuario funcionario;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	private Set<Mercadoria> mercadorias = new HashSet<>();
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	private Set<Servico> servicos = new HashSet<>();
	
	@OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "veiculo_id", nullable = true)
	private Veiculo veiculo;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "empresa_id", nullable = true)
	private Empresa empresa;

	// === BACKUP AGRUPADO ===
	@Column(columnDefinition = "TEXT")
	@JsonIgnore // Ocultar da serialização JSON
	private String backupJson; // Persistência do backup como JSON
	
	@Transient
	@JsonProperty("backup")
	private Backup backup;

	// === MÉTODOS DE PERSISTÊNCIA DO BACKUP ===
	
	@PrePersist
	@PreUpdate
	private void persistirBackup() {
		if (backup != null) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				backupJson = mapper.writeValueAsString(backup);
			} catch (JsonProcessingException e) {
				System.err.println("Erro ao serializar backup: " + e.getMessage());
			}
		}
	}
	
	@PostLoad
	private void restaurarBackup() {
		System.out.println("[DEBUG] @PostLoad chamado para venda ID " + id);
		System.out.println("[DEBUG] backupJson = " + backupJson);
		
		if (backupJson != null && !backupJson.isEmpty()) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				backup = mapper.readValue(backupJson, Backup.class);
				System.out.println("[DEBUG] Backup restaurado para venda ID " + id + ": " + backup);
			} catch (JsonProcessingException e) {
				System.err.println("Erro ao deserializar backup: " + e.getMessage());
				e.printStackTrace();
			}
		} else {
			System.out.println("[DEBUG] Nenhum backup encontrado para venda ID " + id);
			// Inicializar backup vazio para evitar null
			backup = new Backup();
		}
	}

	// === MÉTODOS PARA GESTÃO DO BACKUP ===
	public void atualizarBackupCliente() {
		if (backup == null) backup = new Backup();
		if (cliente != null) {
			backup.cliente = new Backup.ClienteBackup(cliente.getId(), cliente.getNome(), cliente.getEmail());
		}
	}
	public void atualizarBackupFuncionario() {
		if (backup == null) backup = new Backup();
		if (funcionario != null) {
			backup.funcionario = new Backup.FuncionarioBackup(funcionario.getId(), funcionario.getNome(), funcionario.getEmail());
		}
	}
	public void atualizarBackupVeiculo() {
		if (backup == null) backup = new Backup();
		if (veiculo != null) {
			backup.veiculo = new Backup.VeiculoBackup(veiculo.getId(), veiculo.getTipo() != null ? veiculo.getTipo().name() : null, veiculo.getModelo(), veiculo.getPlaca());
		}
	}
	public void atualizarBackupEmpresa() {
		if (backup == null) backup = new Backup();
		if (empresa != null) {
			backup.empresa = new Backup.EmpresaBackup(empresa.getId(), empresa.getRazaoSocial(), empresa.getNomeFantasia());
		}
	}
	public void atualizarBackupMercadorias() {
		if (backup == null) backup = new Backup();
		if (mercadorias != null && !mercadorias.isEmpty()) {
			backup.mercadorias = new HashSet<>();
			for (Mercadoria mercadoria : mercadorias) {
				backup.mercadorias.add(new Backup.MercadoriaBackup(
					mercadoria.getId(), 
					mercadoria.getNome(), 
					mercadoria.getValor(), 
					mercadoria.getDescricao()
				));
			}
		}
	}
	public void atualizarBackupServicos() {
		if (backup == null) backup = new Backup();
		if (servicos != null && !servicos.isEmpty()) {
			backup.servicos = new HashSet<>();
			for (Servico servico : servicos) {
				backup.servicos.add(new Backup.ServicoBackup(
					servico.getId(), 
					servico.getNome(), 
					servico.getValor(), 
					servico.getDescricao()
				));
			}
		}
	}

	// === CLASSE INTERNA DE BACKUP ===
	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Backup {
		public ClienteBackup cliente;
		public FuncionarioBackup funcionario;
		public VeiculoBackup veiculo;
		public EmpresaBackup empresa;
		public Set<MercadoriaBackup> mercadorias;
		public Set<ServicoBackup> servicos;

		@Data
		public static class ClienteBackup {
			public Long id;
			public String nome;
			public String email;
			
			
			public ClienteBackup() {}
			
			public ClienteBackup(Long id, String nome, String email) {
				this.id = id; this.nome = nome; this.email = email;
			}
		}
		@Data
		public static class FuncionarioBackup {
			public Long id;
			public String nome;
			public String email;
			
			
			public FuncionarioBackup() {}
			
			public FuncionarioBackup(Long id, String nome, String email) {
				this.id = id; this.nome = nome; this.email = email;
			}
		}
		@Data
		public static class VeiculoBackup {
			public Long id;
			public String tipo;
			public String modelo;
			public String placa;
			
			
			public VeiculoBackup() {}
			
			public VeiculoBackup(Long id, String tipo, String modelo, String placa) {
				this.id = id; this.tipo = tipo; this.modelo = modelo; this.placa = placa;
			}
		}
		@Data
		public static class EmpresaBackup {
			public Long id;
			public String razaoSocial;
			public String nomeFantasia;
			
			
			public EmpresaBackup() {}
			
			public EmpresaBackup(Long id, String razaoSocial, String nomeFantasia) {
				this.id = id; this.razaoSocial = razaoSocial; this.nomeFantasia = nomeFantasia;
			}
		}
		@Data
		public static class MercadoriaBackup {
			public Long id;
			public String nome;
			public double valor;
			public String descricao;
			
			
			public MercadoriaBackup() {}
			
			public MercadoriaBackup(Long id, String nome, double valor, String descricao) {
				this.id = id; this.nome = nome; this.valor = valor; this.descricao = descricao;
			}
		}
		@Data
		public static class ServicoBackup {
			public Long id;
			public String nome;
			public double valor;
			public String descricao;
			
			
			public ServicoBackup() {}
			
			public ServicoBackup(Long id, String nome, double valor, String descricao) {
				this.id = id; this.nome = nome; this.valor = valor; this.descricao = descricao;
			}
		}
	}
}