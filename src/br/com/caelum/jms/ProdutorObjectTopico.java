package br.com.caelum.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.InitialContext;

import br.com.caelum.modelo.Pedido;
import br.com.caelum.modelo.PedidoFactory;

public class ProdutorObjectTopico {

	// teste utilizando o JMS 1.1
	public static void main(String[] args) throws Exception {

		InitialContext context = new InitialContext(); // cria o contexto de nomes do JNDI

		ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory"); // procura pelo connection factory do activemq no JNDI

		Connection connection = factory.createConnection("admin", "admin");

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE); // abstrai o trabalho transacional e a resposta de recebimento da mensagem. false = sem transacao. nao e necessario dar um session.commit() ou rollback

		connection.start();

		System.out.print("Sending the messages...");

		Destination topico = (Destination) context.lookup("loja"); // procura pelo objeto loja do activemq no jndi (atraves do arquivo jndi.properties do classpath)

		MessageProducer producer = session.createProducer(topico);
		
		Pedido pedido = new PedidoFactory().geraPedidoComValores();
		
		Message message = session.createObjectMessage(pedido); // envia um objeto que implementa serializable para o consumidor
		
		// cria as propriedades para restricao de recebimento dos subscribers do topico
		message.setBooleanProperty("ebook", true);
		
		producer.send(message);

		session.close();
		connection.close();
		context.close();

		System.out.print("sent!");
	}

}
