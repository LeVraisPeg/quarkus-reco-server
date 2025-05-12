package fr.univtln.pegliasco.encryption.differential_privacy;

import com.google.privacy.differentialprivacy.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.Random;

/**
 * The {@code MakeNoise} class provides methods to apply Differential Privacy
 * to numerical data using Google's Differential Privacy library.
 * 
 * <p>
 * Features:
 * <ul>
 * <li>Compute the minimum and maximum bounds of a dataset.</li>
 * <li>Apply differential privacy to the sum of a dataset.</li>
 * <li>Generate noisy results for comparison with real values.</li>
 * </ul>
 * 
 * <p>
 * Example usage:
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
    private static final Random RANDOM = new Random();

    /**
     * Returns the minimum value of a numerical dataset.
     *
     * @param data The dataset.
     * @return The minimum value, or 0.0 if the dataset is empty.
     */
    public static double getMin(List<Double> data) {
        return data.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
    }

    /**
     * Returns the maximum value of a numerical dataset.
     *
     * @param data The dataset.
     * @return The maximum value, or 0.0 if the dataset is empty.
     */
    public double getMax(List<Double> data) {
        return data.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
    }

    /**
     * Adds Gaussian noise to a given value.
     *
     * @param epsilon     The privacy parameter.
     * @param sensitivity The sensitivity of the data.
     * @return The noisy value.
     */
    public double generateGaussianNoise(double epsilon, double sensitivity) {
        double sigma = (Math.sqrt(2) * sensitivity) / epsilon;
        return RANDOM.nextGaussian() * sigma;
    }

    /**
     * Adds Laplace noise to a given value.
     *
     * @param epsilon     The privacy parameter.
     * @param sensitivity The sensitivity of the data.
     * @return The noisy value.
     */
    public double generateLaplaceNoise(double epsilon, double sensitivity) {
        double privacyBudget = sensitivity / epsilon;
        double randomValue = RANDOM.nextDouble() - 0.5;
        return privacyBudget * Math.signum(randomValue) * Math.log(1 - 2 * Math.abs(randomValue));
    }

    /**
     * Main entry point of the program.
     * 
     * <p>
     * Generates a fixed list of movie ratings (between 0 and 5), applies
     * differential privacy, and displays the real and noisy results.
     * </p>
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        MakeNoise mn = new MakeNoise();

        // Generate 100 movie ratings between 0.0 and 5.0
        List<Double> filmRatings = List.of(
                4.5, 3.0, 5.0, 2.5, 4.0, 3.5, 1.0, 2.0, 4.8, 3.2,
                4.1, 3.3, 4.7, 2.8, 3.9, 4.4, 1.5, 2.3, 4.6, 3.1,
                4.0, 3.5, 2.0, 1.0, 4.2, 3.8, 4.9, 2.7, 3.6, 4.3,
                4.5, 3.0, 5.0, 2.5, 4.0, 3.5, 1.0, 2.0, 4.8, 3.2,
                4.1, 3.3, 4.7, 2.8, 3.9, 4.4, 1.5, 2.3, 4.6, 3.1,
                4.0, 3.5, 2.0, 1.0, 4.2, 3.8, 4.9, 2.7, 3.6, 4.3,
                4.5, 3.0, 5.0, 2.5, 4.0, 3.5, 1.0, 2.0, 4.8, 3.2,
                4.1, 3.3, 4.7, 2.8, 3.9, 4.4, 1.5, 2.3, 4.6, 3.1,
                4.0, 3.5, 2.0, 1.0, 4.2, 3.8, 4.9, 2.7, 3.6, 4.3,
                4.5, 3.0, 5.0, 2.5, 4.0, 3.5, 1.0, 2.0, 4.8, 3.2);

        // Automatically calculate min and max
        double lower = mn.getMin(filmRatings);
        double upper = mn.getMax(filmRatings);

        // Privacy parameter
        double epsilon = 0.5;

        // Display results
        double realSum = filmRatings.stream().mapToDouble(Double::doubleValue).sum();
        double realAverage = realSum / filmRatings.size();
        System.out.println("Real average: " + realAverage);

        // Parameters for Gaussian noise
        double delta = 1e-5;
        int l0Sensitivity = 1;
        double lInfSensitivity = 0.5;

        // Add Laplace noise
        List<Double> noisyRatings = filmRatings.stream()
                .map(value -> value + mn.generateLaplaceNoise(epsilon, lInfSensitivity))
                .collect(Collectors.toList());

        double noisyAverage = noisyRatings.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        LOGGER.info("Noisy average (Laplace): " + noisyAverage);

        // Add Gaussian noise
        List<Double> noisyRatings2 = filmRatings.stream()
                .map(value -> value + mn.generateGaussianNoise(epsilon, lInfSensitivity))
                .collect(Collectors.toList());

        double noisyAverage2 = noisyRatings2.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        LOGGER.info("Noisy average (Gaussian): " + noisyAverage2);
    }
}