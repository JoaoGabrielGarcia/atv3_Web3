package com.autobots.automanager.entidades;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(exclude = { "empresa" })
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Mercadoria {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private Date validade;
	@Column(nullable = false)
	private Date fabricacao;
	@Column(nullable = false)
	private Date cadastro;
	@Column(nullable = false)
	private String nome;
	@Column(nullable = false)
	private int quantidade;
	@Column(nullable = false)
	private double valor;
	@Column()
	private String descricao;
	@ManyToOne(fetch = FetchType.LAZY)
	private Empresa empresa;

	// === MÉTODOS DE SERIALIZAÇÃO ===

	@JsonProperty("empresaInfo")
	public EmpresaInfo getEmpresaInfo() {
		if (empresa != null) {
			return new EmpresaInfo(empresa.getId(), empresa.getRazaoSocial());
		}
		return null;
	}

	// === CLASSE INTERNA PARA INFORMAÇÕES RESUMIDAS ===

	public static class EmpresaInfo {
		private Long id;
		private String nome;

		public EmpresaInfo(Long id, String nome) {
			this.id = id;
			this.nome = nome;
		}

		public Long getId() { return id; }
		public void setId(Long id) { this.id = id; }
		public String getNome() { return nome; }
		public void setNome(String nome) { this.nome = nome; }
	}
}