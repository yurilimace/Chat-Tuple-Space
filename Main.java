import net.jini.core.entry.Entry;


public class Main  {

    public static void main(String[] args)   {

        Cliente cli = new Cliente();
        Interface teste = new Interface(cli);
        teste.show(cli);
    }

}
