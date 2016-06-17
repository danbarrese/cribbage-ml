package net.pladform.cribbage.ml;

import net.pladform.cribbage.engine.Round;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Dan Barrese
 */
public class RoundStats {

    private Map<String, AvgRoundOutcome> outcomes;

    public RoundStats() {
        outcomes = new ConcurrentHashMap<>();
    }

    public void add(String state, int finalPoints) {
        outcomes.compute(state, (s, outcome) -> {
            if (outcome == null) {
                outcome = new AvgRoundOutcome();
            }
            outcome.add(finalPoints);
            return outcome;
        });
    }

    @Override
    public String toString() {
        return outcomes.toString();
    }

}
