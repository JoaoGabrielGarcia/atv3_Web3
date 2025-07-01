package com.autobots.automanager.entidades;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

import com.autobots.automanager.enumeracoes.PerfilUsuario;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@EqualsAndHashCode(exclude = { "mercadorias", "vendas", "veiculos" })
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Usuario implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, unique = true)
	private String email;
	@Column(nullable = false)
	private String nome;
	@Column
	private String nomeSocial;
	@ElementCollection(fetch = FetchType.EAGER)
	private Set<PerfilUsuario> perfis = new HashSet<>();
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<Telefone> telefones = new HashSet<>();
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private Endereco endereco;
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<Documento> documentos = new HashSet<>();
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<Credencial> credenciais = new HashSet<>();
	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
	private Set<Mercadoria> mercadorias = new HashSet<>();
	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
	private Set<Venda> vendas = new HashSet<>();
	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
	private Set<Veiculo> veiculos = new HashSet<>();
	@ManyToOne(fetch = FetchType.EAGER)
	private Empresa empresa;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return perfis.stream().map(p -> new SimpleGrantedAuthority(p.name())).collect(Collectors.toSet());
	}

	@Override
	public String getPassword() {
		try {
			if (credenciais == null) {
				return null;
			}
			
			String senha = credenciais.stream()
				.filter(c -> c instanceof CredencialUsuarioSenha)
				.map(c -> ((CredencialUsuarioSenha) c).getSenha())
				.findFirst().orElse(null);
			return senha;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

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

	// === CLASSE INTERNA PARA RECEBER EMPRESA NO JSON ===

	public static class EmpresaInput {
		private Long id;

		public Long getId() { return id; }
		public void setId(Long id) { this.id = id; }
	}
}