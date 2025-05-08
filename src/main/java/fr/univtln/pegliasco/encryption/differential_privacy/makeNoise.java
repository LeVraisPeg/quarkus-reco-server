package fr.univtln.pegliasco.encryption.differential_privacy;

import com.google.privacy.differentialprivacy.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * La classe {@code makeNoise} fournit des méthodes pour appliquer la
 * confidentialité différentielle
 * (Differential Privacy) à des données numériques à l'aide de la bibliothèque
 * Google Differential Privacy.
 * 
 * <p>
 * Elle permet de :
 * <ul>
 * <li>Calculer les bornes minimales et maximales d'une liste de données.</li>
 * <li>Appliquer un bruit différentiel sur la somme des données.</li>
 * <li>Afficher les résultats bruités et réels pour comparaison.</li>
 * </ul>
 * 
 * <p>
 * Exemple d'utilisation :
 * 
 * <pre>
 * List<Double> data = List.of(4.0, 5.0, 3.0);
 * double noisySum = makeNoise.applyDifferentialPrivacy(data, 1.0, 0.0, 5.0);
 * </pre>
 * 
 * @author Enzo
 * @version 1.0
 */
public class makeNoise {
    private static final Logger LOGGER = Logger.getLogger(makeNoise.class.getName());

    /**
     * Retourne la valeur minimale d'une liste de données numériques.
     *
     * @param data La liste de données dont on veut calculer le minimum.
     * @return La valeur minimale de la liste. Retourne 0.0 si la liste est vide.
     */
    public static double getMin(List<Double> data) {
        return data.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
    }

    /**
     * Retourne la valeur maximale d'une liste de données numériques.
     *
     * @param data La liste de données dont on veut calculer le maximum.
     * @return La valeur maximale de la liste. Retourne 0.0 si la liste est vide.
     */
    public static double getMax(List<Double> data) {
        return data.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
    }

    /**
     * Applique la confidentialité différentielle sur la somme d'une liste de
     * données numériques.
     * Cette méthode utilise l'algorithme BoundedSum de la bibliothèque Google
     * Differential Privacy.
     *
     * @param data    La liste de données sur laquelle appliquer la confidentialité
     *                différentielle.
     * @param epsilon Le paramètre de confidentialité (plus il est petit, plus la
     *                confidentialité est forte).
     * @param lower   La borne inférieure des valeurs de la liste.
     * @param upper   La borne supérieure des valeurs de la liste.
     * @return La somme bruitée des données.
     */
    public static double applyDifferentialPrivacy(List<Double> data, double epsilon, double lower, double upper) {
        BoundedSum boundedSum = BoundedSum.builder()
                .epsilon(epsilon)
                .lower(lower)
                .upper(upper)
                .maxPartitionsContributed(1)
                .build();

        for (double value : data) {
            boundedSum.addEntry(value);
        }

        return boundedSum.computeResult();
    }

    /**
     * Point d'entrée principal du programme.
     * <p>
     * Génère une liste aléatoire de notes de films (entre 0 et 5), applique la
     * confidentialité différentielle,
     * puis affiche la somme réelle et la somme bruitée.
     * </p>
     *
     * @param args Les arguments de la ligne de commande (non utilisés ici).
     */
    public static void main(String[] args) {
        // Générer 1000 notes de films entre 0.0 et 5.0
        List<Double> filmRatings = new java.util.Random().doubles(1000, 0.0, 5.0)
                .boxed()
                .collect(Collectors.toList());

        // Calculer min et max automatiquement
        double lower = getMin(filmRatings);
        double upper = getMax(filmRatings);
        LOGGER.info("Min des données : " + lower);
        LOGGER.info("Max des données : " + upper);

        // Paramètre de confidentialité
        double epsilon = 1.0;

        // Appliquer la differential privacy
        double noiseSum = applyDifferentialPrivacy(filmRatings, epsilon, lower, upper);

        // Afficher les résultats
        double realSum = filmRatings.stream().mapToDouble(Double::doubleValue).sum();
        System.out.println("Somme réelle : " + realSum);
        System.out.println("Somme bruitée : " + noiseSum);
    }
}
