package arbre;

import exception.SyntaxErrorException;
import memoire.Memoire;
import java.util.Arrays;
import memoire.ValeurTemporaire;

/**
 *
 * @author Antoine NOSAL
 */
public class InterpreterArbre {

    /**
     * Méthode qui interprète un AST en paramètre.
     *
     * @param ast l'AST
     * @throws exception.SyntaxErrorException Erreur de syntaxe
     */
    public static String interpreterArbreSyntaxique(Arbre ast) throws SyntaxErrorException {
        String res = "";

        // ON RECUPÈRE LE NOEUD RACINE
        Noeud racine = ast.getRacine();

        // ON INTERPRETE LE NOEUD RACINE
        res = interpreterNoeud(racine);

        return res;
    }

    /**
     * Méthode qui interprète un noeud et renvoie le résultat de l'interprétation
     * @param n Noeud à interpréter
     * @return résultat de l'interprétation
     */
    private static String interpreterNoeud(Noeud n) {
        String res = "";
        // ON SWITCH SUR LA VALEUR DU NOEUD POUR CHOISIR LA BONNE METHODE D'INTERPRETATION
        switch (n.getValeur()) {
            case ":=":
                System.out.println("    --> ASSIGNATION");
                interpreterAssignation(n);
                break;
            case "if":
                System.out.println("    --> CONDITION");
                res = interpreterCondition(n);
                break;
            case "while":
                System.out.println("    --> BOUCLE");
                res = interpreterBoucle(n);
                break;
            case "return":
                System.out.println("    --> RETURN");
                res = interpreterReturn(n);
                break;
            case ";":
                System.out.println("    --> POINT-VIRGULE");
                res = interpreterPointVirgule(n);
                break;
            case "let":
                System.out.println("    --> LET");
                res = interpreterLet(n);
                break;
            default:
                System.out.println("    --> PAS ENCORE IMPLÉMENTÉ");
                break;
        }
        return res;
    }

    /**
     * Méthode qui interprète l'assignation
     * @param n Noeud à interpréter
     * @return résultat de l'interprétation
     */
    private static void interpreterAssignation(Noeud n) {
        // ON TROUVE LA VALEUR DANS LE NOEUD GAUCHE
        Noeud nGauche = n.getFils().get(1);
        String valeurDeVariable = trouverValeur(nGauche);

        // ON TROUVE LE NOM DE LA VARIABLE DANS LE NOEUD DROITE
        Noeud nDroite = n.getFils().get(0);
        String nomDeVariable = nDroite.getValeur();

        // ON ASSOCIE DANS LA MÉMOIRE, LA VALEUR À LA VARIABLE
        Memoire.ajouter(nomDeVariable, valeurDeVariable);
    }

    /**
     * Méthode qui interprète un if
     * @param n Noeud à interpréter
     * @return résultat de l'interprétation
     */
    private static String interpreterCondition(Noeud n) {
        String res = "";
        
        // ON TROUVE LA VALEUR DU NOEUD DE LA CONDITION
        Noeud nCondition = n.getFils().get(0);
        String valeurCondition = trouverValeur(nCondition);

        String vTest = valeurEnMemoire(valeurCondition);
        if (vTest != null) {
            valeurCondition = vTest;
        }

        // SI LA CONDITION EST BIEN, AU FINAL, TRUE OU FALSE
        if ((valeurCondition.equals("true")) || (valeurCondition.equals("false"))) {
            // SI C'EST VRAI
            if (valeurCondition.equals("true")) {
                // ON INTERPRETE LE NOEUD VRAI
                Noeud nVrai = n.getFils().get(1);
                res = interpreterNoeud(nVrai);
            }

            // SI C'EST FAUX
            if (valeurCondition.equals("false")) {
                // ON INTERPRETE LE NOEUD FAUX S'IL EXISTE
                if (n.getFils().size() == 3) {
                    Noeud nFaux = n.getFils().get(2);
                    res = interpreterNoeud(nFaux);
                }
            }
        } else {
            System.out.println("Problème : Une condition doit être une valeur booléenne !");
        }
        return res;
    }

    /**
     * Méthode qui interprète un while
     * @param n Noeud à interpréter
     * @return résultat de l'interprétation
     */
    private static String interpreterBoucle(Noeud n) {
        String res = "";
        // ON TROUVE LA VALEUR DU NOEUD DE LA CONDITION DE LA BOUCLE
        Noeud nCondition = n.getFils().get(0);
        String valeurCondition = trouverValeur(nCondition);

        String vTest = valeurEnMemoire(valeurCondition);
        if (vTest != null) {
            valeurCondition = vTest;
        }

        /* OLD:
         // SI LA MEMOIRE CONTIENT UNE VARIABLE DE CE NOM, ON RECUPERE SA VALEUR
         if (Memoire.getMemoire().containsKey(valeurCondition)) {
         valeurCondition = Memoire.getMemoire().get(valeurCondition);
         } */
        // SI LA CONDITION DE LA BOUCLE EST BIEN, AU FINAL, TRUE
        if (valeurCondition.equals("true")) {
            // ON INTERPRETE LE NOEUD VRAI
            Noeud nVrai = n.getFils().get(1);
            res = interpreterNoeud(nVrai);
            // ON INTERPRETE A NOUVEAU LA BOUCLE (récursivité)
            res = res + interpreterBoucle(n);
        } else {
            System.out.println("Fin de boucle !");
        }
        return res;
    }

    /**
     * Méthode qui interprète un return
     * @param n Noeud à interpréter
     * @return résultat de l'interprétation
     */
    private static String interpreterReturn(Noeud n) {
        String res = "";
        // ON INTERPRETE LE NOEUD RETURN
        Noeud nReturn = n.getFils().get(0);
        res = trouverValeur(nReturn);
        return res;
    }

    /**
     * Méthode qui interprète un noeud ";" qui sépare deux commandes
     * @param n Noeud à interpréter
     * @return résultat de l'interprétation
     */
    private static String interpreterPointVirgule(Noeud n) {
        String res = "";
        // ON INTERPRETE LE NOEUD GAUCHE
        Noeud nGauche = n.getFils().get(0);
        res = interpreterNoeud(nGauche);

        // ON INTERPRETE LE NOEUD DROITE
        Noeud nDroite = n.getFils().get(1);
        res = res + "\n" + interpreterNoeud(nDroite);
        return res;
    }

    /**
     * Méthode qui interprète la déclaration d'une variable locale
     * ou d'un aliasing
     * @param n Noeud à interpréter
     * @return résultat de l'interprétation
     */
    private static String interpreterLet(Noeud n) {
        String res = "";
        String nom1, nom2;
        if (n.getFils().size() == 2) {
            // CAS DE LA DÉCLARATION : let variable in com end
            nom1 = n.getFils().get(0).getValeur();
            Memoire.memoireLet.add(new ValeurTemporaire(nom1, valeurEnMemoire(nom1)));
            Noeud nCom = n.getFils().get(1);
            res = interpreterNoeud(nCom);
            Memoire.memoireLet.remove(Memoire.memoireLet.size() - 1);
        } else {
            // CAS DE L'ALIASING : let variable1 be variable2 in com end
            nom1 = n.getFils().get(0).getValeur();
            nom2 = n.getFils().get(1).getValeur();
            Memoire.memoireLet.add(new ValeurTemporaire(nom1, valeurEnMemoire(nom2)));
            Noeud nCom = n.getFils().get(2);
            res = interpreterNoeud(nCom);
            Memoire.memoireLet.remove(Memoire.memoireLet.size() - 1);
        }
        return res;
    }

    /**
     * Méthode qui évalue un noeud
     * (soit une valeur, soit une variable, soit une expression arithmétique ou booléenne)
     * @param n Noeud à interpréter
     * @return résultat de l'interprétation
     */
    private static String trouverValeur(Noeud n) {
        String v = null;
        String vNoeud = n.getValeur();
        String vTest = valeurEnMemoire(vNoeud);
        // SI LA VALEUR DU NOEUD EST UN ENTIER OU UN BOOLEEN, ON RETOURNERA CETTE VALEUR
        if (estEntierOuBooleen(vNoeud)) {
            v = vNoeud;
        } else if (vTest != null) {
            v = vTest;
        } else {
            String[] symbolesAcceptables = {"+", "-", "*", "/", ">", "<", "=", "and", "or"};
            // SI LA VALEUR DANS LE NOEUD EST UN SYMBOLE ACCEPTÉ, ON CONTINUE (récursivité)
            if (Arrays.asList(symbolesAcceptables).contains(vNoeud)) {
                // EN FONCTION DU SYMBOLE, ON EFFECTUE LA BONNE OPÉRATION
                v = faireOperation(n, vNoeud);
            } else {
                System.out.println("Erreur de syntaxe");
            }
        }
        return v;
    }

    /**
     * Méthode identifiant le type d'une donnée
     * Renvoie true si c'est entier ou booléen
     * @param s chaine contenant la donnée
     * @return booleen à true si la donnée est entière ou booléenne
     */
    public static boolean estEntierOuBooleen(String s) {
        boolean res = false;
        switch (s) {
            case "true":
                res = true;
                break;
            case "false":
                res = true;
                break;
            default:
                res = false;
                break;
        }
        try {
            Integer.parseInt(s);
            res = true;
        } catch (NumberFormatException nfe) {
        }
        return res;
    }

    /**
     * Méthode qui renvoie la valeur en mémoire globale ou temporaire
     * @param s nom de la variable
     * @return valeur de la variable
     */
    public static String valeurEnMemoire(String s) {
        // On recherche d'abord dans les variables temporaires
        if (!Memoire.memoireLet.isEmpty()) {
            for (int i = Memoire.memoireLet.size() - 1; i >= 0; i--) {
                if (Memoire.memoireLet.get(i).getNom().equals(s)) {
                    return Memoire.memoireLet.get(i).getValeur();
                }
            }
        }

        // Si on arrive ici, il faut chercher dans la mémoire globale
        if (Memoire.getMemoire().containsKey(s)) {
            return Memoire.getMemoire().get(s);
        }

        // On a pas trouvé s dans la mémoire
        return null;
    }

    /**
     * Méthode effectuant une opération arithmétique ou booléenne
     * @param n Noeud de l'opération
     * @param vNoeud valeur du Noeud
     * @return résultat de l'opération
     */
    private static String faireOperation(Noeud n, String vNoeud) {
        Integer i;
        Boolean b;
        String res = "";

        // ON TROUVE LA VALEUR DANS LES NOEUDS (GAUCHE & DROITE)
        Noeud nGauche = n.getFils().get(1);
        Noeud nDroite = n.getFils().get(0);

        // ON SWITCH SUR LA VALEUR DU NOEUD POUR FAIRE LA BONNE OPERATION
        switch (vNoeud) {
            // ADDITION
            case "+":
                i = Integer.parseInt(trouverValeur(nDroite)) + Integer.parseInt(trouverValeur(nGauche));
                res = i.toString();
                break;
            // SOUSTRACTION
            case "-":
                i = Integer.parseInt(trouverValeur(nDroite)) - Integer.parseInt(trouverValeur(nGauche));
                res = i.toString();
                break;
            // MULTIPLICATION
            case "*":
                i = Integer.parseInt(trouverValeur(nDroite)) * Integer.parseInt(trouverValeur(nGauche));
                res = i.toString();
                break;
            // DIVISION
            case "/":
                i = Integer.parseInt(trouverValeur(nDroite)) / Integer.parseInt(trouverValeur(nGauche));
                res = i.toString();
                break;
            // SUPERIORITE STRICTE
            case ">":
                b = Integer.parseInt(trouverValeur(nDroite)) > Integer.parseInt(trouverValeur(nGauche));
                res = b.toString();
                break;
            // SUPERIORITE OU EGALITE
            case ">=":
                b = Integer.parseInt(trouverValeur(nDroite)) >= Integer.parseInt(trouverValeur(nGauche));
                res = b.toString();
                break;
            // INFERIORITE STRICTE
            case "<":
                b = Integer.parseInt(trouverValeur(nDroite)) < Integer.parseInt(trouverValeur(nGauche));
                res = b.toString();
                break;
            // INFERIORITE OU EGALITE
            case "<=":
                b = Integer.parseInt(trouverValeur(nDroite)) <= Integer.parseInt(trouverValeur(nGauche));
                res = b.toString();
                break;
            // EGALITE
            case "=":
                b = Integer.parseInt(trouverValeur(nDroite)) == Integer.parseInt(trouverValeur(nGauche));
                res = b.toString();
                break;
            // AND
            case "and":
                b = Boolean.valueOf(trouverValeur(nDroite)) && Boolean.valueOf(trouverValeur(nGauche));
                res = b.toString();
                break;
            // OR
            case "or":
                b = Boolean.valueOf(trouverValeur(nDroite)) || Boolean.valueOf(trouverValeur(nGauche));
                res = b.toString();
                break;
            default:
                System.out.println("Pas encore possible ...");
                break;
        }

        return res;
    }

}
