package br.com.igti.aia1;

import static br.com.igti.aia1.CotacaoProvider.accessBroker;
import static br.com.igti.aia1.CotacaoProvider.url;
import javax.naming.Context;
import javax.jms.ConnectionFactory;
import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.JMSException;
import javax.annotation.Resource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.Timer;

public class HomeBrokerSwing extends javax.swing.JFrame {

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

    /**
     * Creates new form HomeBrokerSwing
     */
    public HomeBrokerSwing() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        label = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        label.setText("Teste");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(HomeBrokerSwing.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HomeBrokerSwing.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HomeBrokerSwing.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HomeBrokerSwing.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            HomeBrokerSwing homeBrokerSwing = new HomeBrokerSwing();
            homeBrokerSwing.setVisible(true);
            homeBrokerSwing.accessBroker(url);
        });
    }
    
    public void accessBroker(String url) {
        Context ctx = null;
        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
        props.setProperty("java.naming.factory.state", "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
        props.setProperty("org.omg.CORBA.ORBInitialHost", defORBInitialHost);
        props.setProperty("org.omg.CORBA.ORBInitialPort", defORBInitialPort);

        try {
            ctx = new InitialContext(props);
        } catch (NamingException ne) {
            System.err.println("Falha ao criar InitialContext.");
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
            System.err.println("Falhou a pesquisa para o objeto Connection Factory.");
            System.err.println("\nDetalhes da exceção:");
            ne.printStackTrace();
            System.exit(-1);
        }

        try {
            System.out.println("Pesquisand o objeto de filacom o nome: " + MYQUEUE_LOOKUP_NAME);
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
            System.out.println("Conexão com o broker foi criada.");
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
                    label.setText(msgCurrent);
                    System.out.println("Cotações: " + msgCurrent);
                    System.out.println("Recebida as seguintes mensagens: " + rcvMsg.getText());
                }
            }
        } catch (JMSException e) {
            System.err.println("JMS Exception: " + e);
            e.printStackTrace();
            System.exit(-1);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel label;
    // End of variables declaration//GEN-END:variables
}
