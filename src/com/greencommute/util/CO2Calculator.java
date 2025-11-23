package com.greencommute.util;

public class CO2Calculator {
    // CO2 emissions in kg per km
    private static final double CAR_EMISSION = 0.192;
    private static final double BUS_EMISSION = 0.089;
    private static final double BIKE_EMISSION = 0.0;
    private static final double WALK_EMISSION = 0.0;
    private static final double TRAIN_EMISSION = 0.041;
    private static final double CARPOOL_EMISSION = 0.096;

    public static double calculateCO2Saved(String mode, double distance) {
        double carEmission = CAR_EMISSION * distance;
        double modeEmission = 0;

        switch (mode.toLowerCase()) {
            case "bus":
                modeEmission = BUS_EMISSION * distance;
                break;
            case "bike":
            case "walk":
                modeEmission = 0;
                break;
            case "train":
                modeEmission = TRAIN_EMISSION * distance;
                break;
            case "carpool":
                modeEmission = CARPOOL_EMISSION * distance;
                break;
            default:
                modeEmission = carEmission;
        }

        return Math.max(0, carEmission - modeEmission);
    }

    public static int calculatePoints(String mode, double distance, double co2Saved) {
        int basePoints = 0;

        switch (mode.toLowerCase()) {
            case "walk":
            case "bike":
                basePoints = 20;
                break;
            case "bus":
            case "train":
                basePoints = 15;
                break;
            case "carpool":
                basePoints = 10;
                break;
            default:
                basePoints = 5;
        }

        // Add bonus points for distance (1 point per km)
        int distanceBonus = (int) distance;

        // Add bonus for CO2 saved (10 points per kg)
        int co2Bonus = (int) (co2Saved * 10);

        return basePoints + distanceBonus + co2Bonus;
    }

    public static double calculateTreesEquivalent(double co2Kg) {
        // 1 tree absorbs approximately 21 kg of CO2 per year
        return co2Kg / 21.0;
    }
}
