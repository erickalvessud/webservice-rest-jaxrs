package br.com.alura.loja;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;

import br.com.alura.loja.modelo.Carrinho;
import br.com.alura.loja.modelo.Produto;
import junit.framework.Assert;

public class ClienteTest {

	private HttpServer servidor;
	private WebTarget target;
	private Client client;
	
	@Before
	public void startaServidor(){
		this.servidor = Servidor.startaServidor();
		
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.register(new LoggingFilter());
		
		this.client = ClientBuilder.newClient(clientConfig);
		this.target = this.client.target("http://localhost:8081");
	}
	
	@After
	public void paraServidor(){
		this.servidor.stop();
	}
	
	@Test
	public void testaQueBuscarUmCarrinhoTrazOCarrinhoEsperado(){
		
		String conteudo = target.path("/carrinhos/1").request().get(String.class);
		Carrinho carrinho = (Carrinho) new XStream().fromXML(conteudo);
		Assert.assertEquals("Rua Vergueiro 3185, 8 andar", carrinho.getRua());
		client.close();
	}
	
	@Test
	public void testaAdicionarUmCarrinhoViaPost(){
		
		Carrinho carrinho = new Carrinho();
		carrinho.adiciona(new Produto(314l, "Tablet", 999, 1));
		carrinho.setRua("Rua Vergueiro");
		carrinho.setCidade("Sao Paulo");
		
		String conteudo = carrinho.toXML();
		
		Entity<String> entity = Entity.entity(conteudo, MediaType.APPLICATION_XML);
		
		Response response = target.path("/carrinhos").request().post(entity);
		
		Assert.assertEquals(201, response.getStatus());
		
		String location = response.getHeaderString("Location");
		
		conteudo = client.target(location).request().get(String.class);
		Assert.assertTrue(conteudo.contains("Tablet"));
		
	}
}
