package net.pladform.cribbage.ml;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Dan Barrese
 */
public class RoundStatsBuckets {

    private Map<Boolean, Map<Integer, RoundStats>> dealerToStateSizeToStats;

    public RoundStatsBuckets() {
        dealerToStateSizeToStats = new ConcurrentHashMap<>();
        dealerToStateSizeToStats.put(false, new ConcurrentHashMap<>());
        dealerToStateSizeToStats.put(true, new ConcurrentHashMap<>());
        for (int i = 12; i <= 20; i++) {
            dealerToStateSizeToStats.get(false).put(i, new RoundStats());
        }
        for (int i = 12; i <= 20; i++) {
            dealerToStateSizeToStats.get(true).put(i, new RoundStats());
        }
    }

    public void add(String state, int finalPoints) {
        boolean dealer = state.startsWith("*");
        Integer stateLen = dealer ? state.length() - 1 : state.length();
        if (dealer) {
            state = state.substring(1);
        }
        dealerToStateSizeToStats.get(dealer).get(stateLen).add(state, finalPoints);
    }

    @Override
    public String toString() {
        return dealerToStateSizeToStats.toString();
    }

}
