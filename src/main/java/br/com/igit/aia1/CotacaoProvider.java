package br.com.igit.aia1;

import java.text.DecimalFormat;
import java.util.Properties;
import java.util.Random;
import javax.jms.ConnectionFactory;
import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.DeliveryMode;
import javax.jms.TextMessage;
import javax.jms.JMSException;
import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class CotacaoProvider {

    @Resource(mappedName = "MyConnectionFactory")
    private static ConnectionFactory cf;
    @Resource(mappedName = "MyQueue")
    private static Queue queue;
    static String defORBInitialHost = "localhost";
    static String defORBInitialPort = "3700";
    static String url = defORBInitialHost + ":" + defORBInitialPort;
    static String MYCF_LOOKUP_NAME = "MyConnectionFactory";
    static String MYQUEUE_LOOKUP_NAME = "MyQueue";
    static Connection connection;
    static Session session;
    static MessageProducer msgProducer;
    static MessageConsumer msgConsumer;
    static TextMessage msg, rcvMsg;

    public static void main(String args[]) throws InterruptedException {
        accessBroker(url);
    }

    public static void accessBroker(String url) throws InterruptedException {
        Context ctx = null;
        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
        props.setProperty("java.naming.factory.state", "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
        props.setProperty("org.omg.CORBA.ORBInitialHost", defORBInitialHost);
        props.setProperty("org.omg.CORBA.ORBInitialPort", defORBInitialPort);

        try {
            ctx = new InitialContext(props);
        } catch (NamingException ne) {
            System.err.println("FalhaaocriarInitialContext.");
            System.err.println("O Context.PROVIDER_URLespecificado: " + url);
            System.err.println("\nDetalhes da exceção:");
            ne.printStackTrace();
            System.exit(-1);
        }

        try {
            System.out.println("Pesquisando o objeto Connection Factory object com o nome: " + MYCF_LOOKUP_NAME);
            cf = (javax.jms.ConnectionFactory) ctx.lookup(MYCF_LOOKUP_NAME);
            System.out.println("Connection Factory encontrado.");
        } catch (NamingException ne) {
            System.err.println("Falhou a pesquisapara o objeto Connection Factory.");
            System.err.println("\nDetalhes da exceção:");
            ne.printStackTrace();
            System.exit(-1);
        }

        try {
            System.out.println("Pesquisandoobjeto de filacom o nome:" + MYQUEUE_LOOKUP_NAME);
            queue = (javax.jms.Queue) ctx.lookup(MYQUEUE_LOOKUP_NAME);
            System.out.println("Objeto Queue encontrado.");
        } catch (NamingException ne) {
            System.err.println("Falhou a pesquisa para o objeto Queue.");
            System.err.println("\nDetalhes da exceção:");
            ne.printStackTrace();
            System.exit(-1);
        }

        try {
            System.out.println("Criando conexão com o broker");
            connection = cf.createConnection();
            System.out.println("Conexão com o broker foicriada.");
        } catch (JMSException e) {
            System.err.println("Falhou a criação com o broker.");
            System.err.println("\nDetalhes da exceção:");
            e.printStackTrace();
            System.exit(-1);
        }

        try {
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // Create the MessageProducer and MessageConsumer msgProducer = session.createProducer(queue);
            // Tell the provider to start sending messages. connection.start();
            Double valor = 12.45;

            while (true) {
                DecimalFormat df = new DecimalFormat("#.##");
                String strValor = df.format(valor);
                String strMsg = "PETR - ON -> " + strValor;
                msg = session.createTextMessage(strMsg); // Publish the message
                System.out.println("Publicando uma mensagem para a Fila: " + queue.getQueueName() + " Valor: " + strValor);
                msgProducer.send(msg, DeliveryMode.NON_PERSISTENT, 4, 0);
                valor = valor + sorteiaNumero();
                Thread.sleep(5000);
            }
        } catch (JMSException e) {
            System.err.println("JMS Exception: " + e);
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static Double sorteiaNumero() {
        Random r = new Random();
        Double num = r.nextDouble();
        if (r.nextBoolean()) {
            num = -num;
        }

        return num;
    }

}
