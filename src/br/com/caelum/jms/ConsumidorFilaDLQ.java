package br.com.caelum.jms;

import java.util.Scanner;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.naming.InitialContext;

public class ConsumidorFilaDLQ {

	// teste utilizando o JMS 1.1
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {

		InitialContext context = new InitialContext(); // cria o contexto de nomes do JNDI

		ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory"); // procura pelo connection factory do activemq no JNDI

		Connection connection = factory.createConnection("admin", "admin");

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE); // abstrai o trabalho transacional e a resposta de recebimento da mensagem - false = sem transacao. nao e necessario dar um session.commit() ou rollback

		connection.start();

		System.out.println("Waiting for messages...\n");

		Destination fila = (Destination) context.lookup("dlq"); // procura pelo objeto fila do activemq no jndi (atraves do arquivo jndi.properties do classpath)

		MessageConsumer consumer = session.createConsumer(fila);

		// Message message = consumer.receive(); // este metodo e usado para receber somente uma mensagem

		consumer.setMessageListener((m) -> {
			
			System.out.println(m);
		});

		new Scanner(System.in).nextLine(); // usado somente para esperar uma tecla ser pressionada

		session.close();
		connection.close();
		context.close();
	}

}
