package fr.univtln.pegliasco.encryption.differential_privacy;

import com.google.privacy.differentialprivacy.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.Random;

/**
 * La classe {@code MakeNoise} fournit des méthodes pour appliquer la
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
public class MakeNoise {
    private static final Logger LOGGER = Logger.getLogger(MakeNoise.class.getName());

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
     * Ajoute du bruit gaussien à une valeur donnée.
     *
     * @param value           La valeur à laquelle ajouter du bruit.
     * @param epsilon         Le paramètre de confidentialité (plus il est petit,
     *                        plus la confidentialité est forte).
     * @param delta           Le paramètre delta pour le bruit gaussien (nécessaire
     *                        pour garantir la confidentialité).
     * @param l0Sensitivity   La sensibilité L0 (nombre maximal de contributions par
     *                        utilisateur).
     * @param lInfSensitivity La sensibilité L∞ (différence maximale qu'une seule
     *                        entrée peut provoquer).
     * @return La valeur bruitée.
     */
    public static double addGaussianNoise(double value, double epsilon, double delta, int l0Sensitivity,
            double lInfSensitivity) {
        double l2Sensitivity = Math.sqrt(l0Sensitivity) * lInfSensitivity;
        double sigma = l2Sensitivity / epsilon * Math.sqrt(2 * Math.log(1.25 / delta));
        Random random = new Random();
        double gaussianNoise = random.nextGaussian() * sigma; // Génère un bruit gaussien
        return value + gaussianNoise;
    }

    /**
     * Ajoute du bruit laplacien à une valeur donnée.
     *
     * @param value           La valeur à laquelle ajouter du bruit.
     * @param epsilon         Le paramètre de confidentialité (plus il est petit,
     *                        plus la confidentialité est forte).
     * @param l0Sensitivity   La sensibilité L0 (nombre maximal de contributions par
     *                        utilisateur).
     * @param lInfSensitivity La sensibilité L∞ (différence maximale qu'une seule
     *                        entrée peut provoquer).
     * @return La valeur bruitée.
     */
    public static double addLaplaceNoise(double value, double epsilon, double lower, double upper) {
        double sensitivity = upper - lower; // Sensibilité de la fonction
        double scale = sensitivity / epsilon;
        Random random = new Random();
        double uniform = random.nextDouble() - 0.5; // Génère un nombre entre -0.5 et 0.5
        double laplaceNoise = -scale * Math.signum(uniform) * Math.log(1 - 2 * Math.abs(uniform));
        return value + laplaceNoise;
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
        List<Double> filmRatings = List.of(
                4.5, 3.0, 5.0, 2.5, 4.0, 3.5, 1.0, 2.0, 4.8, 3.2,
                4.1, 3.3, 4.7, 2.8, 3.9, 4.4, 1.5, 2.3, 4.6, 3.1,
                4.2, 3.4, 4.9, 2.7, 3.8, 4.3, 1.8, 2.6, 4.0, 3.6,
                4.0, 3.7, 4.5, 2.9, 3.5, 4.1, 1.2, 2.4, 4.3, 3.0,
                4.6, 3.2, 4.8, 2.5, 3.7, 4.2, 1.7, 2.1, 4.4, 3.3,
                4.9, 3.1, 5.0, 2.6, 3.9, 4.7, 1.4, 2.2, 4.1, 3.4,
                4.3, 3.5, 4.6, 2.8, 3.8, 4.0, 1.9, 2.7, 4.5, 3.6,
                4.7, 3.8, 4.2, 2.9, 3.3, 4.4, 1.3, 2.0, 4.9, 3.7,
                4.8, 3.9, 4.1, 2.4, 3.6, 4.5, 1.6, 2.3, 4.0, 3.2,
                4.4, 3.0, 4.7, 2.1, 3.5, 4.3, 1.1, 2.8, 4.6, 3.1);

        // Calculer min et max automatiquement
        double lower = getMin(filmRatings);
        double upper = getMax(filmRatings);
        LOGGER.info("Min des données : " + lower);
        LOGGER.info("Max des données : " + upper);

        // Paramètre de confidentialité
        double epsilon = .5; // Plus il est petit, plus la confidentialité est forte

        // Appliquer la differential privacy
        double noiseSum = applyDifferentialPrivacy(filmRatings, epsilon, lower, upper);

        // Afficher les résultats
        double realSum = filmRatings.stream().mapToDouble(Double::doubleValue).sum();
        double realAverage = realSum / filmRatings.size();
        System.out.println("Moyenne réelle : " + realAverage);

        // Paramètres de confidentialité
        double delta = 1e-5; // Nécessaire pour le bruit gaussien
        int l0Sensitivity = 1; // Nombre maximal de contributions par utilisateur
        double lInfSensitivity = 0.5; // Différence maximale qu'une seule entrée peut provoquer

        // Ajouter du bruit laplacien
        List<Double> noisyRatings = filmRatings.stream()
                .map(value -> addLaplaceNoise(value, epsilon, lower, upper))
                .collect(Collectors.toList());

        double noisyAverage = noisyRatings.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        LOGGER.info("Moyenne bruitée (Laplace) : " + noisyAverage);

        // Ajouter du bruit gaussien
        List<Double> noisyRatings2 = filmRatings.stream()
                .map(value -> addGaussianNoise(value, epsilon, delta, l0Sensitivity, lInfSensitivity))
                .collect(Collectors.toList());

        double noisyAverage2 = noisyRatings2.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        LOGGER.info("Moyenne bruitée (Gauss) : " + noisyAverage2);

    }

}