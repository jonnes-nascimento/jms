package br.com.caelum.jms;

import java.util.Scanner;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.InitialContext;

public class ConsumidorTopicoFinanceiro {

	// teste utilizando o JMS 1.1
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {

		InitialContext context = new InitialContext(); // cria o contexto de nomes do JNDI

		ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory"); // procura pelo connection factory do activemq no JNDI

		Connection connection = factory.createConnection("admin", "admin");
		connection.setClientID("financeiro");

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE); // abstrai o trabalho transacional e a resposta de recebimento da mensagem - false = sem transacao. nao e necessario dar um session.commit() ou rollback

		connection.start();

		System.out.println("Waiting for messages...\n");

		Topic topico = (Topic) context.lookup("loja"); // procura pelo objeto topico do activemq no jndi (atraves do arquivo jndi.properties do classpath)

		MessageConsumer consumer = session.createDurableSubscriber(topico, "consumidor");

		consumer.setMessageListener((m) -> {
			try {
				TextMessage textMessage = (TextMessage) m;
				System.out.println(textMessage.getText());
			} catch (JMSException e) {
				e.printStackTrace();
			}
		});

		new Scanner(System.in).nextLine(); // usado somente para esperar uma tecla ser pressionada

		session.close();
		connection.close();
		context.close();
	}

}
