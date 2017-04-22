package br.com.alura.loja;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;

import br.com.alura.loja.modelo.Projeto;
import junit.framework.Assert;

public class ProjetoTest {

	private HttpServer servidor;
	
	@Before
	public void startaServidor(){
		this.servidor = Servidor.startaServidor();
	}
	
	@After
	public void paraServidor(){
		this.servidor.stop();
	}
	
	@Test
	public void testaQueORecursoParaOProjetoFunciona(){
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:8081");
		String resposta = target.path("/projetos/1").request().get(String.class);
		Projeto projeto = (Projeto) new XStream().fromXML(resposta);
		Assert.assertEquals("Minha loja", projeto.getNome());
		client.close();
	}
	
	@Test
	public void testaQueMetodoAdicionaProjetoFunciona(){
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:8081");
		
		Projeto projeto = new Projeto(1l, "Casa na Praia", 2012);
		String conteudo = projeto.toXML();
		
		Entity<String> entity = Entity.entity(conteudo, MediaType.APPLICATION_XML);
		Response response = target.path("/projetos").request().post(entity);
		
		Assert.assertEquals(201, response.getStatus());
		
		String location = response.getHeaderString("Location");
		
		conteudo = client.target(location).request().get(String.class);
		
		Assert.assertTrue(conteudo.contains("Casa na Praia"));
	}
}









