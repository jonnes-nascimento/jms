package br.com.caelum.jms;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.InitialContext;

public class ProdutorFila {

	// teste utilizando o JMS 1.1
	public static void main(String[] args) throws Exception {

		// cria os parametros de propriedades em runtime, ao inves de usar os parametros do arquivo jndi.properties
		Properties properties = new Properties();
		properties.setProperty("java.naming.factory.initial", "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		properties.setProperty("java.naming.provider.url", "tcp://localhost:61616");
		properties.setProperty("queue.financeiro", "fila.financeiro");

		InitialContext context = new InitialContext(properties); // cria o contexto de nomes do JNDI

		ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory"); // procura pelo connection factory do activemq no JNDI

		Connection connection = factory.createConnection("admin", "admin");

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE); // abstrai o trabalho transacional e a resposta de recebimento da mensagem. false = sem transacao. nao e necessario dar um session.commit() ou rollback

		connection.start();

		System.out.print("Sending the messages...");

		Destination fila = (Destination) context.lookup("financeiro"); // procura pelo objeto fila do activemq no jndi (atraves do arquivo jndi.properties do classpath)

		MessageProducer producer = session.createProducer(fila);

		for (int i = 0; i < 1000; i++) {

			Message message = session.createTextMessage("{\"pedido\": \"" + i + "\"}");
			producer.send(message);
		}

		session.close();
		connection.close();
		context.close();

		System.out.print("sent!");
	}

}
