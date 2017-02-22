package br.com.igit.aia1;

import javax.naming.*;
import javax.jms.ConnectionFactory;
import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.JMSException;
import java.util.*;
import javax.annotation.Resource;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public final class HomeBrokerSwing extends JPanel {

    private static final long serialVersionUID = 1L;

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
    static String msgCurrent = "";
    JLabel label;
    Timer timer;

    public HomeBrokerSwing() {
        this.add(this.getLabel());
        this.go();
    }

    public JLabel getLabel() {
        if (this.label == null) {
            this.label = new JLabel(HomeBrokerSwing.msgCurrent);
            this.label.setPreferredSize(new Dimension(150, 22));
        }

        return this.label;
    }

    public void go() {
        ActionListener action = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                label.setText(msgCurrent);
            }
        };
        this.timer = new Timer(1000, action);
        this.timer.start();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new HomeBrokerSwing());
        frame.setSize(300, 150);
        frame.setVisible(true);
        accessBroker(url);
    }

    public static void accessBroker(String url) {
        Context ctx = null;
        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInit ContextFactory");
        props.setProperty("java.naming.factory.state", "com.sun.corba.ee.impl.presentation.r mi.JNDIStateFactoryImpl");
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
            System.out.println("Pesquisando o objeto Connection Factory object com o nome: "
                    + MYCF_LOOKUP_NAME);
            cf = (javax.jms.ConnectionFactory) ctx.lookup(MYCF_LOOKUP_NAME);
            System.out.println("Connection Factory encontrado.");
        } catch (NamingException ne) {
            System.err.println("Falhou a pesquisapara o objeto Connection Factory.");
            System.err.println("\nDetalhes da exceção:");
            ne.printStackTrace();
            System.exit(-1);
        }

        try {
            System.out.println("Looking up Queue object with lookup name: "
                    + MYQUEUE_LOOKUP_NAME);
            queue = (javax.jms.Queue) ctx.lookup(MYQUEUE_LOOKUP_NAME);
            System.out.println("objeto Queue encontrado.");
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
            msgConsumer = session.createConsumer(queue);
            // Tell the provider to start sending messages. connection.start();
            while (true) {
                rcvMsg = (TextMessage) msgConsumer.receive();
                if (rcvMsg != null) {
                    msgCurrent = rcvMsg.getText();
                    System.out.println("Cotações: " + msgCurrent);
                    System.out.println("Recebida as seguintes mensagens: " + rcvMsg.getText());
                }
            }
        } catch (JMSException e) {
            System.err.println("JMS Exception: " + e);
            e.printStackTrace();
            System.exit(-1);
        }

        try {
            connection.close();
        } catch (JMSException e) {
            System.err.println("JMS Exception: " + e);
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
