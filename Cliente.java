
import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace;

import javax.swing.*;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class Cliente {
    public String nome;
    public JavaSpace space;
    public ArrayList<String> contatos = new ArrayList<String>();
    public ArrayList<String> salas = new ArrayList<String>();
    public String select_room ="";

    public Cliente(){}


    public void Connect_Space() {
        try {
            System.out.println("Procurando pelo servico JavaSpace...");
            Lookup finder = new Lookup(JavaSpace.class);
            this.space = (JavaSpace) finder.getService();
            if (this.space == null) {
                System.out.println("O servico JavaSpace nao foi encontrado. Encerrando...");
                System.exit(-1);
            }
            System.out.println("Conectado com o espaço de Tupla");
            System.out.println(this.space);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Boolean VerifyUserName() throws TransactionException, UnusableEntryException, RemoteException, InterruptedException {
        ListaUsuario lista = new ListaUsuario();
        lista.identificador = "usuarios";
        ListaUsuario response = (ListaUsuario) this.space.read(lista,null,3*1000);
        if(response == null){
            lista.identificador = "usuarios";
            lista.listausarios = new ArrayList<String>();
            lista.listausarios.add(this.nome);
            this.space.write(lista,null,1000*1000);
            return true;
        }
        else{
            System.out.println(response.listausarios);
            if(!response.listausarios.contains(this.nome)){
                ListaUsuario retrive = (ListaUsuario) this.space.take(response,null,2*1000);
                retrive.listausarios.add(this.nome);
                this.space.write(retrive,null,1000*1000);
                return true;
            }
            else{
                return false;
            }
        }
    }

    public void Send_msg(String text) throws RemoteException, TransactionException, InterruptedException {

        if (text == null) {
            System.exit(0);
        }
        Message msg = new Message();

        if (!this.contatos.isEmpty()) {
            for (int i = 0; i < this.contatos.size(); i++) {
                msg.recieve = this.nome;
                msg.chatRoom = this.select_room;
                msg.name = (String) this.contatos.get(i);
                msg.content = text;
                //System.out.println(msg.chatRoom);
                this.space.write(msg,null,300*1000);
            }
        } else {
            System.out.println("lista de contatos vazia");
        }


    }

    public void Send_msg_direct(String contact_select,String message) throws RemoteException, TransactionException {
        Message msg = new Message();
        msg.recieve = this.nome;
        msg.chatRoom = this.select_room;
        msg.name = contact_select;
        msg.content = message;
        this.space.write(msg,null,300*1000);
    }

    public void Recieve_msg(JTextArea chat,String room_name) throws TransactionException, UnusableEntryException, RemoteException, InterruptedException {
        Message template = new Message();
        template.name = this.nome;
        template.chatRoom = room_name;
        Message msg = (Message) this.space.take(template, null, (long) (2 * 1000));
        if (msg != null) {
            chat.append(msg.recieve + ":" + msg.content + "\r\n");
            System.out.println("\r\n" + "Mensagem recebida: " + msg.content + "\r\n");

        }

    }

    // todos os contatos que estão em uma sala
    public void getContatos(JList lista,String roomName) throws TransactionException, UnusableEntryException, RemoteException, InterruptedException {
        Contatos temp = new Contatos();
        temp.name = roomName;
        //System.out.println(roomName + "aquiiii");
        Contatos response = (Contatos) this.space.read(temp, null, 2 * 1000);
        if (response != null && this.contatos.size() < response.teste.size() - 1) {
            for (int i = 0; i < response.teste.size(); i++) {
                if (!response.teste.get(i).equals(this.nome) && !this.contatos.contains(response.teste.get(i))) {
                    this.contatos.add(response.teste.get(i));
                    System.out.println("\r\n" + this.contatos);
                    lista.setListData(this.contatos.toArray());
                    //System.out.println("teste");
                    System.out.println("entrou aqui");
                }
            }
        }
    }

    public void enterRoom(String roomName) throws TransactionException, UnusableEntryException, RemoteException, InterruptedException {
        Contatos room = new Contatos();
        room.name = roomName;
        Contatos response = (Contatos) this.space.read(room,null,2*1000);
        if(response == null){
            room.name = roomName;
            room.teste = new ArrayList<String>();
            room.teste.add(this.nome);
            this.space.write(room,null,1000*1000);
            this.select_room = roomName;
        }
        else{
            Contatos retrive = (Contatos) this.space.take(response, null, 2 * 1000);
            if (retrive == null) {
                System.out.println("Deu Ruim");
            } else {
                response.teste.add(this.nome);
                this.space.write(response, null, 1000 * 1000);
                this.select_room = roomName;
            }
        }
    }

    public Boolean VerifyRoomName(String roomName) throws TransactionException, UnusableEntryException, RemoteException, InterruptedException {
        SalaChat temp = new SalaChat();
        temp.idname = "chat";
        SalaChat response = (SalaChat) this.space.read(temp, null, 2 * 1000);
        if(response != null){
            if(response.salas.contains(roomName)){
                return false;
            }
        }
        return true;

    }

    public void createRoom(String roomName) throws RemoteException, TransactionException, InterruptedException, UnusableEntryException {
            SalaChat temp = new SalaChat();
            temp.idname = "chat";
            SalaChat response = (SalaChat) this.space.read(temp, null, 2 * 1000);
            if (response == null) {

                System.out.println(response + " sem nada");
                temp.idname = "chat";
                temp.salas = new ArrayList<String>();
                temp.salas.add(roomName);
                this.space.write(temp, null, 1000 * 1000);

            }
            else {
                SalaChat retrive = (SalaChat) this.space.take(response, null, 2 * 1000);
                if (retrive.salas.contains(roomName)) {
                    System.out.println("Deu Ruim");
                } else {
                    response.salas.add(roomName);
                    this.space.write(response, null, 1000 * 1000);

                }

            }


    }


    public void getRooms(JList lista) throws TransactionException, UnusableEntryException, RemoteException, InterruptedException {
        SalaChat update = new SalaChat();
        update.idname = "chat";
        SalaChat response = (SalaChat) this.space.readIfExists(update, null, 2 * 1000);

        if (response != null) {
            for (int i = 0; i < response.salas.toArray().length; i++) {
                if(!this.salas.contains(response.salas.get(i))){
                    this.salas.add(response.salas.get(i));
                    lista.setListData(this.salas.toArray());
                }
            }
        }


    }

}



