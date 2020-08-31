import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.TransactionException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

public class Interface implements Entry {

    private JPanel panel1;
    private JPanel login;
    private JPanel room;
    private JTextField loginField;
    private JButton btn_login;
    private JButton btn_criar_sala;
    private JList lista_contatos;
    private JTextField msg_entry;
    private JList lista_salas;
    private JTextField criar_sala_entry;
    private JTextPane chat_field;
    private JTextArea textArea1;
    private JScrollPane teste_scroll;
    private JScrollPane list1Scroll;
    private JScrollPane list2Scroll;
    private JLabel errorLabel;
    private JLabel show_selected_room;
    private JLabel show_select_friend;
    private JLabel logError;
    private int contats_size;
    public String room_context = "";
    public String selected_friend = "";
    //controlador das trheads 
    public volatile boolean teste = true;
    public volatile boolean teste2 = false;

    public Interface(Cliente cli)   {

        lista_contatos.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(e.getValueIsAdjusting()){
                    selected_friend = lista_contatos.getSelectedValue().toString();
                    show_select_friend.setText(lista_contatos.getSelectedValue().toString());
                    show_select_friend.setVisible(true);
                    show_select_friend.updateUI();
                }
            }
        });

        lista_salas.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(e.getValueIsAdjusting()){
                    teste = false;
                    teste2 = false;
                    try {
                            String select = lista_salas.getSelectedValue().toString();
                            if(!room_context.equals(select)){
                                cli.enterRoom(select);
                                teste = true;
                                //teste2 = true;
                                //ReceiveMsg(cli);
                                updateFriendList(cli,select);
                                room_context = cli.select_room;
                                textArea1.setText("");
                                show_selected_room.setText(cli.select_room);
                                show_selected_room.setVisible(true);
                                show_selected_room.updateUI();
                                textArea1.updateUI();
                                //ReceiveMsg(cli);
                            }

                            //cli.contatos.clear();

                            //ReceiveMsg(cli);
                            //updateFriendList(cli,lista_salas.getSelectedValue().toString());




                    } catch (TransactionException transactionException) {
                        transactionException.printStackTrace();
                    } catch (UnusableEntryException unusableEntryException) {
                        unusableEntryException.printStackTrace();
                    } catch (RemoteException remoteException) {
                        remoteException.printStackTrace();
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
            }
        });

        btn_login.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isuniqueName = false;
                cli.nome = loginField.getText();
                System.out.println(cli.nome);
                cli.Connect_Space();
                try {
                     isuniqueName = cli.VerifyUserName();
                } catch (TransactionException transactionException) {
                    transactionException.printStackTrace();
                } catch (UnusableEntryException unusableEntryException) {
                    unusableEntryException.printStackTrace();
                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                if(isuniqueName){
                    panel1.removeAll();
                    teste_scroll.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                    list1Scroll.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                    list2Scroll.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                    panel1.add(room);
                    textArea1.setEditable(false);
                    panel1.updateUI();
                    //updateFriendList(cli);
                    UpdateRooms(cli);
                    //updateFriendList(cli,room_context);
                    ReceiveMsg(cli);
                    //ReceiveMsg(cli);

                }
                else{
                   errorLabel.setVisible(true);
                }



            }
        });

        msg_entry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String formated_string;
                if(selected_friend == ""){

                    try {
                        cli.Send_msg(msg_entry.getText());

                        formated_string = cli.nome + ":" + msg_entry.getText()  + "\r\n";
                        textArea1.append(formated_string);
                        msg_entry.setText("");
                        textArea1.setCaretPosition(textArea1.getDocument().getLength());
                        textArea1.updateUI();
                        msg_entry.updateUI();
                        lista_salas.getSelectedValue();

                    } catch (RemoteException remoteException) {
                        remoteException.printStackTrace();
                    } catch (TransactionException transactionException) {
                        transactionException.printStackTrace();
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
                else{
                    try {
                        cli.Send_msg_direct(selected_friend,msg_entry.getText());
                        formated_string = cli.nome + ":" + msg_entry.getText()  + "\r\n";
                        textArea1.append(formated_string);
                        msg_entry.setText("");
                        textArea1.setCaretPosition(textArea1.getDocument().getLength());
                        textArea1.updateUI();
                        msg_entry.updateUI();
                        lista_salas.getSelectedValue();
                        selected_friend = "";
                        show_select_friend.setVisible(false);
                        lista_contatos.clearSelection();

                    } catch (RemoteException remoteException) {
                        remoteException.printStackTrace();
                    } catch (TransactionException transactionException) {
                        transactionException.printStackTrace();
                    }
                }



            }
        });


        btn_criar_sala.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String roomName = criar_sala_entry.getText();

                try {
                    Boolean isUniqueName = cli.VerifyRoomName(roomName);
                    if(isUniqueName){
                        cli.createRoom(roomName);
                        criar_sala_entry.setText("");
                        criar_sala_entry.updateUI();
                    }
                    else{
                        logError.setText("Ja existe uma sala com esse nome no espaço");
                        logError.setVisible(true);
                        logError.updateUI();
                    }
                } catch (TransactionException transactionException) {
                    transactionException.printStackTrace();
                } catch (UnusableEntryException unusableEntryException) {
                    unusableEntryException.printStackTrace();
                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }


            }
        });

    }

    public void UpdateRooms(Cliente cli){
        new Thread(()-> {
            try {
                while(true){
                   cli.getRooms(lista_salas);
                    Thread.sleep(500);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        lista_salas.updateUI();
        //lista_contatos.updateUI();
    }

    public void ReceiveMsg(Cliente cli){

        new Thread(() -> {

                System.out.println("rodando trhead recepção");
                while (true){

                    try {
                        cli.Recieve_msg(textArea1,room_context);

                    } catch (TransactionException e) {
                        e.printStackTrace();
                    } catch (UnusableEntryException e) {
                        e.printStackTrace();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                //Thread.currentThread().interrupt();
                //System.out.println("mudou de contexto recepcao");



        }).start();
        textArea1.updateUI();

    }

    public void updateFriendList(Cliente cli,String select){
        this.contats_size = 0;
        new Thread(()-> {
            try {
                System.out.println("rodando thread");
                System.out.println(Thread.currentThread().getId());
                teste2 = true;
                //ReceiveMsg(cli);
                //ReceiveMsg(cli,Thread.currentThread());

                while(teste){
                        cli.getContatos(lista_contatos,select);
                        //System.out.println(cli.contatos);
                        if(cli.contatos.size() > contats_size){
                            this.contats_size = cli.contatos.size();
                            System.out.println("update");
                        }
                        //Thread.sleep(500);
                    }
                    //Thread.sleep(1000);
                    Thread.currentThread().interrupt();
                    System.out.println("mudou de contexto");
                    cli.contatos.clear();
                    lista_contatos.setListData(cli.contatos.toArray());
                    //lista_contatos.updateUI();

                }
            catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        lista_contatos.updateUI();


    }





    public void show(Cliente cli) {
        JFrame frame = new JFrame("Interface");
        frame.setPreferredSize(new Dimension(400, 300));
        frame.setContentPane(new Interface(cli).panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }




}
