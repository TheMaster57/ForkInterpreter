
import java.util.Scanner;

/**
 *
 * @author Maxime
 */
public class Principale {
    
    public static void main(String[] args) {
        /* Permet la lecture au clavier */
        Scanner sc = new Scanner(System.in);
        
        String cmd;
        while(true) {
            /* Lecture de la commande */
            System.out.print(">>> ");
            cmd = sc.nextLine();
            
            if(cmd.equals("EXIT")) {
                /* Fermeture du programme */
                System.exit(1);
            }
            
            
        }
    }
}