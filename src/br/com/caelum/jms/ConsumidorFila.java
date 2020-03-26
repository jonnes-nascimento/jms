package br.com.caelum.jms;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

public class ConsumidorFila {

	// teste utilizando o JMS 1.1
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {

		// cria os parametros de propriedades em runtime, ao inves de usar os parametros do arquivo jndi.properties
		Properties properties = new Properties();
		properties.setProperty("java.naming.factory.initial", "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		properties.setProperty("java.naming.provider.url", "tcp://localhost:61616");
		properties.setProperty("queue.financeiro", "fila.financeiro");

		InitialContext context = new InitialContext(properties); // cria o contexto de nomes do JNDI

		ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory"); // procura pelo connection factory do activemq no JNDI

		Connection connection = factory.createConnection("admin", "admin");

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE); // abstrai o trabalho transacional e a resposta de recebimento da mensagem - false = sem transacao. nao e necessario dar um session.commit() ou rollback

		connection.start();

		System.out.println("Waiting for messages...\n");

		Destination fila = (Destination) context.lookup("financeiro"); // procura pelo objeto fila do activemq no jndi (atraves do arquivo jndi.properties do classpath)

		MessageConsumer consumer = session.createConsumer(fila);

		// Message message = consumer.receive(); // este metodo e usado para receber somente uma mensagem

		consumer.setMessageListener((m) -> {
			try {
				TextMessage textMessage = (TextMessage) m;
				System.out.println(textMessage.getText());
			} catch (JMSException e) {
				e.printStackTrace();
			}
		});

		//new Scanner(System.in).nextLine(); // usado somente para esperar uma tecla ser pressionada

		session.close();
		connection.close();
		context.close();
	}

}
