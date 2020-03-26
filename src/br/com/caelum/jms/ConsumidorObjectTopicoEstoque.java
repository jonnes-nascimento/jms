package br.com.caelum.jms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.InitialContext;

import org.apache.activemq.ActiveMQConnectionFactory;

import br.com.caelum.modelo.Pedido;

public class ConsumidorObjectTopicoEstoque {

	// teste utilizando o JMS 1.1
	@SuppressWarnings({ "resource", "unchecked" })
	public static void main(String[] args) throws Exception {

		InitialContext context = new InitialContext(); // cria o contexto de nomes do JNDI

		//ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory"); // procura pelo connection factory do activemq no JNDI
		
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
		
		//configura todos os pacotes acreditados que podem ser desserializados como objetos
		factory.setTrustedPackages(new ArrayList(Arrays.asList("java.lang,java.math,java.util,sun.util,org.apache.activemq.test,br.com.caelum.modelo".split(","))));

		Connection connection = factory.createConnection("admin", "admin");
		connection.setClientID("estoque");

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE); // abstrai o trabalho transacional e a resposta de recebimento da mensagem - false = sem transacao. nao e necessario dar um session.commit() ou rollback

		connection.start();

		System.out.println("Waiting for messages...\n");

		Topic topico = (Topic) context.lookup("loja"); // procura pelo objeto topico do activemq no jndi (atraves do arquivo jndi.properties do classpath)

		MessageConsumer consumer = session.createDurableSubscriber(topico, "consumidor");

		consumer.setMessageListener((m) -> {
			try {
				ObjectMessage objectMesage = (ObjectMessage) m;
				
				Pedido pedido = (Pedido) objectMesage.getObject();
				
				System.out.println(pedido.getCodigo());
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
