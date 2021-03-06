/*
 * Test de l'assignation
 */
package tests.creationarbre;

import static tests.creationarbre.TestArbre.afficherArbreAvecCommande;

public class TestAssignation {

    /**
     * Suite de tests (non exhaustive)
     * @param args 
     */
    public static void main(String[] args) {
        // Assignations simples
        afficherArbreAvecCommande("a := 3");
        afficherArbreAvecCommande("a :=");

        // Assignation avec opérateurs
        afficherArbreAvecCommande("a := 2 < 3 ; b := 6");
        afficherArbreAvecCommande("a := 2 < 3 <= 4");

        // Assignation avec calculs
        afficherArbreAvecCommande("a := 2 + 3");
        afficherArbreAvecCommande("a := 2 + 3 * 5");
        afficherArbreAvecCommande("a := 2 * 3 + 5");

        afficherArbreAvecCommande("a := 2 * 3 + a := 2 * 3 + 5 + 5");

        afficherArbreAvecCommande("a := 2 * 3 * 10");

        afficherArbreAvecCommande("b := a * 3 * 10");
    }

}
