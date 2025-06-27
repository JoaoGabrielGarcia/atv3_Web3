package com.autobots.automanager;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import com.autobots.automanager.entidades.CredencialUsuarioSenha;
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.entidades.Email;
import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.entidades.Servico;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Veiculo;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.enumeracoes.PerfilUsuario;
import com.autobots.automanager.enumeracoes.TipoVeiculo;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.RepositorioMercadoria;
import com.autobots.automanager.repositorios.RepositorioServico;
import com.autobots.automanager.repositorios.RepositorioUsuario;
import com.autobots.automanager.repositorios.RepositorioVeiculo;
import com.autobots.automanager.repositorios.RepositorioVenda;

@SpringBootApplication
public class AutomanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutomanagerApplication.class, args);
	}

	@Component
	public static class Runner implements ApplicationRunner {
		@Autowired
		private RepositorioEmpresa repositorioEmpresa;
		
		@Autowired
		private RepositorioUsuario repositorioUsuario;
		
		@Autowired
		private RepositorioMercadoria repositorioMercadoria;
		
		@Autowired
		private RepositorioServico repositorioServico;
		
		@Autowired
		private RepositorioVeiculo repositorioVeiculo;
		
		@Autowired
		private RepositorioVenda repositorioVenda;

		@Override
		public void run(ApplicationArguments args) throws Exception {
			// Verificar se já existem dados
			if (repositorioUsuario.count() > 0) {
				System.out.println("=== DADOS JÁ EXISTEM ===");
				return;
			}

			System.out.println("=== CRIANDO DADOS DE INICIALIZAÇÃO ===");

			// Criar Usuário Administrador
			Usuario administrador = new Usuario();
			administrador.setNome("João Silva");
			administrador.setNomeSocial("João Admin");
			administrador.setEmail("admin@carservice.com.br");
			administrador.getPerfis().add(PerfilUsuario.ADMINISTRADOR);

			CredencialUsuarioSenha credencialAdmin = new CredencialUsuarioSenha();
			credencialAdmin.setInativo(false);
			credencialAdmin.setEmail("admin@carservice.com.br");
			credencialAdmin.setSenha("admin123");
			credencialAdmin.setCriacao(new Date());
			credencialAdmin.setUltimoAcesso(new Date());
			administrador.getCredenciais().add(credencialAdmin);

			Endereco enderecoAdmin = new Endereco();
			enderecoAdmin.setEstado("São Paulo");
			enderecoAdmin.setCidade("São Paulo");
			enderecoAdmin.setBairro("Jardins");
			enderecoAdmin.setRua("Av. São Gabriel");
			enderecoAdmin.setNumero("200");
			enderecoAdmin.setCodigoPostal("01435-001");
			administrador.setEndereco(enderecoAdmin);

			Telefone telefoneAdmin = new Telefone();
			telefoneAdmin.setDdd("11");
			telefoneAdmin.setNumero("9854633728");
			administrador.getTelefones().add(telefoneAdmin);

			Documento cpfAdmin = new Documento();
			cpfAdmin.setTipo("CPF");
			cpfAdmin.setNumero("85647381922");
			administrador.getDocumentos().add(cpfAdmin);

			// Salvar Administrador
			repositorioUsuario.save(administrador);

			// Criar Usuário Funcionário
			Usuario funcionario = new Usuario();
			funcionario.setNome("Pedro Santos");
			funcionario.setNomeSocial("Pedro Funcionário");
			funcionario.setEmail("funcionario@carservice.com.br");
			funcionario.getPerfis().add(PerfilUsuario.FUNCIONARIO);

			CredencialUsuarioSenha credencialFunc = new CredencialUsuarioSenha();
			credencialFunc.setInativo(false);
			credencialFunc.setEmail("funcionario@carservice.com.br");
			credencialFunc.setSenha("func123");
			credencialFunc.setCriacao(new Date());
			credencialFunc.setUltimoAcesso(new Date());
			funcionario.getCredenciais().add(credencialFunc);

			Endereco enderecoFunc = new Endereco();
			enderecoFunc.setEstado("São Paulo");
			enderecoFunc.setCidade("São Paulo");
			enderecoFunc.setBairro("Vila Madalena");
			enderecoFunc.setRua("Rua Harmonia");
			enderecoFunc.setNumero("150");
			enderecoFunc.setCodigoPostal("05435-000");
			funcionario.setEndereco(enderecoFunc);

			Telefone telefoneFunc = new Telefone();
			telefoneFunc.setDdd("11");
			telefoneFunc.setNumero("987654321");
			funcionario.getTelefones().add(telefoneFunc);

			Documento cpfFunc = new Documento();
			cpfFunc.setTipo("CPF");
			cpfFunc.setNumero("12345678901");
			funcionario.getDocumentos().add(cpfFunc);

			// Salvar Funcionário
			repositorioUsuario.save(funcionario);

			// Criar Usuário Fornecedor
			Usuario fornecedor = new Usuario();
			fornecedor.setNome("Componentes Varejo de Partes Automotivas Ltda");
			fornecedor.setNomeSocial("Loja do Carro");
			fornecedor.setEmail("fornecedor@lojadocarro.com.br");
			fornecedor.getPerfis().add(PerfilUsuario.FORNECEDOR);

			CredencialUsuarioSenha credencialFornecedor = new CredencialUsuarioSenha();
			credencialFornecedor.setInativo(false);
			credencialFornecedor.setEmail("fornecedor@lojadocarro.com.br");
			credencialFornecedor.setSenha("forn123");
			credencialFornecedor.setCriacao(new Date());
			credencialFornecedor.setUltimoAcesso(new Date());
			fornecedor.getCredenciais().add(credencialFornecedor);

			Endereco enderecoFornecedor = new Endereco();
			enderecoFornecedor.setEstado("Rio de Janeiro");
			enderecoFornecedor.setCidade("Rio de Janeiro");
			enderecoFornecedor.setBairro("Centro");
			enderecoFornecedor.setRua("Av. República do Chile");
			enderecoFornecedor.setNumero("50");
			enderecoFornecedor.setCodigoPostal("20031-170");
			fornecedor.setEndereco(enderecoFornecedor);

			Telefone telefoneFornecedor = new Telefone();
			telefoneFornecedor.setDdd("21");
			telefoneFornecedor.setNumero("987654321");
			fornecedor.getTelefones().add(telefoneFornecedor);

			Documento cnpj = new Documento();
			cnpj.setTipo("CNPJ");
			cnpj.setNumero("00014556000100");
			fornecedor.getDocumentos().add(cnpj);

			// Salvar Fornecedor
			repositorioUsuario.save(fornecedor);

			// Criar Usuário Cliente
			Usuario cliente = new Usuario();
			cliente.setNome("Maria Silva");
			cliente.setNomeSocial("Maria Cliente");
			cliente.setEmail("cliente@email.com.br");
			cliente.getPerfis().add(PerfilUsuario.CLIENTE);

			CredencialUsuarioSenha credencialCliente = new CredencialUsuarioSenha();
			credencialCliente.setInativo(false);
			credencialCliente.setEmail("cliente@email.com.br");
			credencialCliente.setSenha("cli123");
			credencialCliente.setCriacao(new Date());
			credencialCliente.setUltimoAcesso(new Date());
			cliente.getCredenciais().add(credencialCliente);

			Endereco enderecoCliente = new Endereco();
			enderecoCliente.setEstado("São Paulo");
			enderecoCliente.setCidade("São José dos Campos");
			enderecoCliente.setBairro("Centro");
			enderecoCliente.setRua("Av. Dr. Nelson D'Ávila");
			enderecoCliente.setNumero("300");
			enderecoCliente.setCodigoPostal("12245-070");
			cliente.setEndereco(enderecoCliente);

			Telefone telefoneCliente = new Telefone();
			telefoneCliente.setDdd("12");
			telefoneCliente.setNumero("987654321");
			cliente.getTelefones().add(telefoneCliente);

			Documento cpfCliente = new Documento();
			cpfCliente.setTipo("CPF");
			cpfCliente.setNumero("12584698533");
			cliente.getDocumentos().add(cpfCliente);

			// Salvar Cliente
			repositorioUsuario.save(cliente);

			// Criar Mercadorias
			Mercadoria rodaLigaLeve = new Mercadoria();
			rodaLigaLeve.setCadastro(new Date());
			rodaLigaLeve.setFabricacao(new Date());
			rodaLigaLeve.setNome("Roda de Liga Leve Toyota Etios");
			rodaLigaLeve.setValidade(new Date());
			rodaLigaLeve.setQuantidade(30);
			rodaLigaLeve.setValor(300.0);
			rodaLigaLeve.setDescricao("Roda de liga leve original de fábrica da Toyota para modelos hatch");
			repositorioMercadoria.save(rodaLigaLeve);

			Mercadoria oleoMotor = new Mercadoria();
			oleoMotor.setCadastro(new Date());
			oleoMotor.setFabricacao(new Date());
			oleoMotor.setNome("Óleo de Motor 5W30");
			oleoMotor.setValidade(new Date());
			oleoMotor.setQuantidade(50);
			oleoMotor.setValor(45.0);
			oleoMotor.setDescricao("Óleo de motor sintético 5W30 para motores modernos");
			repositorioMercadoria.save(oleoMotor);

			// Criar Serviços
			Servico trocaRodas = new Servico();
			trocaRodas.setNome("Troca de Rodas");
			trocaRodas.setValor(50.0);
			trocaRodas.setDescricao("Troca das rodas do carro por novas");
			repositorioServico.save(trocaRodas);

			Servico alinhamento = new Servico();
			alinhamento.setNome("Alinhamento de Rodas");
			alinhamento.setValor(80.0);
			alinhamento.setDescricao("Alinhamento das rodas do carro");
			repositorioServico.save(alinhamento);

			Servico balanceamento = new Servico();
			balanceamento.setNome("Balanceamento de Rodas");
			balanceamento.setValor(60.0);
			balanceamento.setDescricao("Balanceamento das rodas do carro");
			repositorioServico.save(balanceamento);

			// Criar Veículos
			Veiculo civic = new Veiculo();
			civic.setTipo(TipoVeiculo.SEDAN);
			civic.setModelo("Honda Civic");
			civic.setPlaca("XYZ-5678");
			repositorioVeiculo.save(civic);

			// Criar Empresa
			Empresa empresa = new Empresa();
			empresa.setRazaoSocial("Auto Center São José Ltda");
			empresa.setNomeFantasia("Auto Center SJ");
			empresa.setCadastro(new Date());
			
			Telefone telefoneEmpresa = new Telefone();
			telefoneEmpresa.setDdd("12");
			telefoneEmpresa.setNumero("39451234");
			empresa.getTelefones().add(telefoneEmpresa);
			
			Email emailEmpresa = new Email();
			emailEmpresa.setEmail("contato@autocentersj.com.br");
			empresa.getEmails().add(emailEmpresa);
			
			Endereco enderecoEmpresa = new Endereco();
			enderecoEmpresa.setEstado("São Paulo");
			enderecoEmpresa.setCidade("São José dos Campos");
			enderecoEmpresa.setBairro("Centro");
			enderecoEmpresa.setRua("Av. 9 de Julho");
			enderecoEmpresa.setNumero("1000");
			enderecoEmpresa.setCodigoPostal("12211-000");
			enderecoEmpresa.setInformacoesAdicionais("Próximo ao shopping");
			empresa.setEndereco(enderecoEmpresa);
			
			repositorioEmpresa.save(empresa);

			System.out.println("=== DADOS DE INICIALIZAÇÃO CRIADOS COM SUCESSO ===");
			System.out.println("Usuários criados:");
			System.out.println("- Administrador: admin@carservice.com.br / admin123");
			System.out.println("- Funcionário: funcionario@carservice.com.br / func123");
			System.out.println("- Fornecedor: fornecedor@lojadocarro.com.br / forn123");
			System.out.println("- Cliente: cliente@email.com.br / cli123");
			System.out.println("=====================================");
		}
	}
}