package de.thm.holdem.model.game.poker;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class BlindHelper {


    /**
     * Method to calculate the small blinds for each level as per
     * <a href="https://poker.stackexchange.com/questions/203/looking-for-the-bb-m-math-to-build-good-nlhe-tourney-blind-structures">...</a>
     *
     * @param numOfPlayers the amount of players in the tournament
     * @param buyIn the buy in of the tournament
     * @param totalTournamentTime the total time the tournament is expected to run
     * @param timeToRaiseBlinds the time between each blind increase
     * @return list of small blind values, big blinds will be double the small blind
     */
    public static List<BigInteger> calculateBlindLevels(int numOfPlayers, BigInteger buyIn, int totalTournamentTime, int timeToRaiseBlinds) {
        int calculatedBuyIn = buyIn.intValue();
        List<BigInteger> blindLevels = new ArrayList<>();
        int finalBigBlind = (numOfPlayers * calculatedBuyIn) / 10;
        int numOfBlindIncreases = numberOfLevels(timeToRaiseBlinds, totalTournamentTime);

        double growthFactor = calculateGrowthFactor(finalBigBlind, numOfBlindIncreases);

        for (int i = 0; i < numOfBlindIncreases; i++) {
            int blindLevel = calculateBlindLevel(growthFactor, i);
            blindLevels.add(BigInteger.valueOf(blindLevel));
        }
        return blindLevels;
    }

    /**
     * Method to calculate the growth factor for the blinds
     *
     * @param finalBigBlind the final big blind
     * @param numberOfLevels the number of blind increases
     * @return the growth factor
     */
    private static double calculateGrowthFactor(int finalBigBlind, int numberOfLevels) {
        return (double) finalBigBlind / Math.pow(1.5, numberOfLevels - 1);
    }

    /**
     * Method to calculate the number of blind increases
     *
     * @param timeToRaiseBlinds the time between each blind increase
     * @param totalTournamentTime the total time the tournament is expected to run
     * @return the number of blind increases
     */
    private static int numberOfLevels(int timeToRaiseBlinds, int totalTournamentTime) {
        return totalTournamentTime / timeToRaiseBlinds;
    }

    /**
     * Method to calculate the blind at a given level
     *
     * <p>
     *     The blind will be rounded to the nearest multiple of 50 if the difference to the next multiple of 50 is less
     *     than 6. If the difference to the next multiple of 25 is less than 5, the blind will be rounded to the nearest
     *     multiple of 25. Otherwise the blind will be rounded to the nearest multiple of 5.
     * </p>
     *
     * @param growthFactor the growth factor
     * @param level the level at which the blind should be calculated
     * @return the blind at the given level
     */
    private static int calculateBlindLevel(double growthFactor, int level) {
        double blindLevel = growthFactor * Math.pow(1.5, level);
        int roundedValue = (int) Math.round(blindLevel);

        int nextMultipleOf50 = (roundedValue / 50 + 1) * 50;
        int differenceToNextMultiple50 = nextMultipleOf50 - roundedValue;

        if (differenceToNextMultiple50 <= 6) {
            roundedValue = nextMultipleOf50;
        } else {
            int nextMultipleOf25 = (roundedValue / 25 + 1) * 25;
            int differenceToNextMultiple25 = nextMultipleOf25 - roundedValue;
            if (differenceToNextMultiple25 <= 5) {
                roundedValue = nextMultipleOf25;
            } else {
                roundedValue = roundToNearestMultipleOf5(roundedValue);
            }
        }

        // Ensure that the minimum small blind is 5
        return Math.max(roundedValue, 5);
    }

    /**
     * Method to round a value to the nearest multiple of 5
     *
     * @param value the value to round
     * @return the rounded value
     */
    private static int roundToNearestMultipleOf5(int value) {
        return (int) (5 * (Math.round((double) value / 5)));
    }
}
