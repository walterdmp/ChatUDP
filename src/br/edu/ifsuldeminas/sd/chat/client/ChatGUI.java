package br.edu.ifsuldeminas.sd.chat.client;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import br.edu.ifsuldeminas.sd.chat.ChatException;
import br.edu.ifsuldeminas.sd.chat.ChatFactory;
import br.edu.ifsuldeminas.sd.chat.MessageContainer;
import br.edu.ifsuldeminas.sd.chat.Sender;

public class ChatGUI extends JFrame implements MessageContainer {

    private JTextField txtPortaLocal, txtIpRemoto, txtPortaRemota, txtMensagem;
    private JTextArea areaChat;
    private JButton btnEnviar, btnConectar;
    private Sender sender;

    public ChatGUI() {
        setTitle("Chat UDP");
        setSize(400, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        JPanel painelConfig = new JPanel(new GridLayout(4, 2, 5, 5));
        
        painelConfig.add(new JLabel(" Porta Local:"));
        txtPortaLocal = new JTextField("1025");
        painelConfig.add(txtPortaLocal);
        
        painelConfig.add(new JLabel(" IP Remoto:"));
        txtIpRemoto = new JTextField("127.0.0.1");
        painelConfig.add(txtIpRemoto);
        
        painelConfig.add(new JLabel(" Porta Remota:"));
        txtPortaRemota = new JTextField("1026");
        painelConfig.add(txtPortaRemota);
        
        painelConfig.add(new JLabel(""));
        btnConectar = new JButton("Conectar");
        painelConfig.add(btnConectar);
        
        add(painelConfig, BorderLayout.NORTH);

        areaChat = new JTextArea();
        areaChat.setEditable(false);
        add(new JScrollPane(areaChat), BorderLayout.CENTER);

        JPanel painelEnvio = new JPanel(new BorderLayout(5, 5));
        txtMensagem = new JTextField();
        btnEnviar = new JButton("Enviar");
        btnEnviar.setEnabled(false); 
        
        painelEnvio.add(txtMensagem, BorderLayout.CENTER);
        painelEnvio.add(btnEnviar, BorderLayout.EAST);
        
        add(painelEnvio, BorderLayout.SOUTH);

        btnConectar.addActionListener(e -> conectar());
        txtMensagem.addActionListener(e -> enviar()); 
        btnEnviar.addActionListener(e -> enviar());
    }

    private void conectar() {
        try {
            int pLocal = Integer.parseInt(txtPortaLocal.getText());
            int pRemota = Integer.parseInt(txtPortaRemota.getText());
            String ip = txtIpRemoto.getText();

            sender = ChatFactory.build(ip, pRemota, pLocal, this);
            
            areaChat.append("Conectado na porta " + pLocal + "\n");
            btnConectar.setEnabled(false);
            btnEnviar.setEnabled(true);
            txtMensagem.requestFocus();
        } catch (Exception ex) {
            areaChat.append("Erro ao conectar. Verifique os dados.\n");
        }
    }

    private void enviar() {
        String msg = txtMensagem.getText();
        if (!msg.isEmpty() && sender != null) {
            try {
                sender.send(msg + MessageContainer.FROM + "Eu");
                areaChat.append("Eu: " + msg + "\n");
                txtMensagem.setText(""); 
            } catch (ChatException ex) {
                areaChat.append("Erro ao enviar mensagem.\n");
            }
        }
    }

    @Override
    public void newMessage(String message) {
        if (message != null) {
            String[] partes = message.split(MessageContainer.FROM);
            if (partes.length == 2) {
                areaChat.append(partes[1] + ": " + partes[0] + "\n");
            } else {
                areaChat.append(message + "\n");
            }
        }
    }

    public static void main(String[] args) {
        new ChatGUI().setVisible(true);
    }
}