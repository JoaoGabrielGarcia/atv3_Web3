package com.autobots.automanager.entidades;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(exclude = { "vendas" })
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Empresa {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String razaoSocial;
	@Column
	private String nomeFantasia;
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<Telefone> telefones = new HashSet<>();
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<Email> emails = new HashSet<>();
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private Endereco endereco;
	@Column(nullable = false)
	private Date cadastro;
	@JsonIgnore
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<Usuario> usuarios = new HashSet<>();
	@JsonIgnore
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<Mercadoria> mercadorias = new HashSet<>();
	@JsonIgnore
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<Servico> servicos = new HashSet<>();
	@JsonIgnore
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<Venda> vendas = new HashSet<>();

	// === MÉTODOS DE SERIALIZAÇÃO ===

	@Transient
	@JsonProperty("usuariosInfo")
	public Set<UsuarioInfo> getUsuariosInfo() {
		Set<UsuarioInfo> usuariosInfo = new HashSet<>();
		if (usuarios != null) {
			for (Usuario usuario : usuarios) {
				usuariosInfo.add(new UsuarioInfo(usuario.getId(), usuario.getNome()));
			}
		}
		return usuariosInfo;
	}

	@Transient
	@JsonProperty("mercadoriasInfo")
	public Set<MercadoriaInfo> getMercadoriasInfo() {
		Set<MercadoriaInfo> mercadoriasInfo = new HashSet<>();
		if (mercadorias != null) {
			for (Mercadoria mercadoria : mercadorias) {
				mercadoriasInfo.add(new MercadoriaInfo(mercadoria.getId(), mercadoria.getNome(), mercadoria.getValor(), mercadoria.getDescricao()));
			}
		}
		return mercadoriasInfo;
	}

	@Transient
	@JsonProperty("servicosInfo")
	public Set<ServicoInfo> getServicosInfo() {
		Set<ServicoInfo> servicosInfo = new HashSet<>();
		if (servicos != null) {
			for (Servico servico : servicos) {
				servicosInfo.add(new ServicoInfo(servico.getId(), servico.getNome(), servico.getValor(), servico.getDescricao()));
			}
		}
		return servicosInfo;
	}

	@Transient
	@JsonProperty("vendasInfo")
	public Set<VendaInfo> getVendasInfo() {
		Set<VendaInfo> vendasInfo = new HashSet<>();
		if (vendas != null) {
			for (Venda venda : vendas) {
				vendasInfo.add(new VendaInfo(venda.getId(), venda.getIdentificacao(), venda.getCadastro()));
			}
		}
		return vendasInfo;
	}

	// === CLASSES INTERNAS PARA INFORMAÇÕES RESUMIDAS ===

	public static class UsuarioInfo {
		private Long id;
		private String nome;

		public UsuarioInfo(Long id, String nome) {
			this.id = id;
			this.nome = nome;
		}

		public Long getId() { return id; }
		public void setId(Long id) { this.id = id; }
		public String getNome() { return nome; }
		public void setNome(String nome) { this.nome = nome; }
	}

	public static class MercadoriaInfo {
		private Long id;
		private String nome;
		private double valor;
		private String descricao;

		public MercadoriaInfo(Long id, String nome, double valor, String descricao) {
			this.id = id;
			this.nome = nome;
			this.valor = valor;
			this.descricao = descricao;
		}

		public Long getId() { return id; }
		public void setId(Long id) { this.id = id; }
		public String getNome() { return nome; }
		public void setNome(String nome) { this.nome = nome; }
		public double getValor() { return valor; }
		public void setValor(double valor) { this.valor = valor; }
		public String getDescricao() { return descricao; }
		public void setDescricao(String descricao) { this.descricao = descricao; }
	}

	public static class ServicoInfo {
		private Long id;
		private String nome;
		private double valor;
		private String descricao;

		public ServicoInfo(Long id, String nome, double valor, String descricao) {
			this.id = id;
			this.nome = nome;
			this.valor = valor;
			this.descricao = descricao;
		}

		public Long getId() { return id; }
		public void setId(Long id) { this.id = id; }
		public String getNome() { return nome; }
		public void setNome(String nome) { this.nome = nome; }
		public double getValor() { return valor; }
		public void setValor(double valor) { this.valor = valor; }
		public String getDescricao() { return descricao; }
		public void setDescricao(String descricao) { this.descricao = descricao; }
	}

	public static class VendaInfo {
		private Long id;
		private String identificacao;
		private Date cadastro;

		public VendaInfo(Long id, String identificacao, Date cadastro) {
			this.id = id;
			this.identificacao = identificacao;
			this.cadastro = cadastro;
		}

		public Long getId() { return id; }
		public void setId(Long id) { this.id = id; }
		public String getIdentificacao() { return identificacao; }
		public void setIdentificacao(String identificacao) { this.identificacao = identificacao; }
		public Date getCadastro() { return cadastro; }
		public void setCadastro(Date cadastro) { this.cadastro = cadastro; }
	}
}