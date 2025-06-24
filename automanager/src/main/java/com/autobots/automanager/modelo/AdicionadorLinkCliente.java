package com.autobots.automanager.modelo;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.ClienteControle;
import com.autobots.automanager.entidades.Cliente;

@Component
public class AdicionadorLinkCliente implements AdicionadorLink<Cliente> {

	@Override
	public void adicionarLink(List<Cliente> lista) {
		for (Cliente cliente : lista) {
			long id = cliente.getId();
			Link selfLink = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(ClienteControle.class).obterCliente(id))
				.withSelfRel();
			Link clientesLink = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(ClienteControle.class).obterClientes())
				.withRel("clientes");
			Link cadastroLink = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(ClienteControle.class).cadastrarCliente(new Cliente()))
				.withRel("cadastro");
			Link updateLink = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(ClienteControle.class).atualizarCliente(new Cliente()))
				.withRel("atualizar");
			Link deleteLink = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(ClienteControle.class).excluirCliente(new Cliente()))
				.withRel("excluir");
			cliente.add(selfLink, clientesLink, cadastroLink, updateLink, deleteLink);
		}
	}

	@Override
	public void adicionarLink(Cliente objeto) {
		long id = objeto.getId();
		Link selfLink = WebMvcLinkBuilder
			.linkTo(WebMvcLinkBuilder.methodOn(ClienteControle.class).obterCliente(id))
			.withSelfRel();
		Link clientesLink = WebMvcLinkBuilder
			.linkTo(WebMvcLinkBuilder.methodOn(ClienteControle.class).obterClientes())
			.withRel("clientes");
		Link cadastroLink = WebMvcLinkBuilder
			.linkTo(WebMvcLinkBuilder.methodOn(ClienteControle.class).cadastrarCliente(new Cliente()))
			.withRel("cadastro");
		Link updateLink = WebMvcLinkBuilder
			.linkTo(WebMvcLinkBuilder.methodOn(ClienteControle.class).atualizarCliente(new Cliente()))
			.withRel("atualizar");
		Link deleteLink = WebMvcLinkBuilder
			.linkTo(WebMvcLinkBuilder.methodOn(ClienteControle.class).excluirCliente(new Cliente()))
			.withRel("excluir");
		objeto.add(selfLink, clientesLink, cadastroLink, updateLink, deleteLink);
	}
} 