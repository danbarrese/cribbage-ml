package net.pladform.cribbage.ml;

import java.text.DecimalFormat;

/**
 * @author Dan Barrese
 */
public class AvgRoundOutcome {

    private static final DecimalFormat df = new DecimalFormat("0.0");
    private double avgRoundPoints;
    private long count;

    public AvgRoundOutcome() {
        this.count = 0;
    }

    public void add(int finalPoints) {
        avgRoundPoints += (finalPoints - avgRoundPoints) / ++count;
    }

    @Override
    public String toString() {
        return df.format(avgRoundPoints);
    }

}
