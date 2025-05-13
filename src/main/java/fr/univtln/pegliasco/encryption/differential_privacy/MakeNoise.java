package fr.univtln.pegliasco.encryption.differential_privacy;

import java.util.List;
import java.util.logging.Logger;

import com.arjuna.ats.internal.jdbc.drivers.modifiers.list;

import fr.univtln.pegliasco.tp.model.Rating;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
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
    public static double getMax(List<Double> data) {
        return data.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
    }

    /**
     * Adds Gaussian noise to a given value.
     *
     * @param epsilon     The privacy parameter.
     * @param sensitivity The sensitivity of the data.
     * @return The noisy value.
     */
    public static double generateGaussianNoise(double epsilon, double sensitivity) {
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
    private static double generateLaplaceNoise(double epsilon, double sensitivity) {
        double privacyBudget = sensitivity / epsilon;
        double randomValue = RANDOM.nextDouble() - 0.5;
        return privacyBudget * Math.signum(randomValue) * Math.log(1 - 2 * Math.abs(randomValue));
    }

    public static List<Rating> applyLapplaceNoise(List<Rating> ratings) {
        ratings.forEach(rating -> {
            double noise = generateLaplaceNoise(0.5, 1);
            rating.setRate((float) (rating.getRate() + noise));
        });
        return ratings;
    }

    // Parameters for differential privacy
    // double delta = 1e-5;
    // int l0Sensitivity = 1;
    // double lInfSensitivity = 0.5;
    // double epsilon = 0.5;

}