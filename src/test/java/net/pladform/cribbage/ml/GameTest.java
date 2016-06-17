package net.pladform.cribbage.ml;

import net.pladform.cribbage.engine.Deck;
import net.pladform.cribbage.engine.Game;
import net.pladform.cribbage.engine.Player;
import net.pladform.cribbage.engine.Scoreboard;
import net.pladform.perf.Timer;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Dan Barrese
 */
public class GameTest {

    private static Map<String, List<Integer>> stats = new HashMap<>();

    @Test
    public void testPlay() {
        Timer.start("game setup");
        Scoreboard scoreboard = new Scoreboard();
        Deck deck = new Deck();
        Game game = new Game(scoreboard, deck);
        Player dan = new Player("Dan", game);
        Player ken = new Player("Ken", game);
        scoreboard.setPlayers(new Player[]{dan, ken});
        scoreboard.setDealer(dan);
        Timer.stop("game setup");
        Timer.start("game play");
        Map<String, List<Integer>> gameStats = game.play();
        gameStats.forEach((state, finalPoints) -> stats.merge(state, finalPoints, (integers, integers2) -> {
            integers.addAll(integers2);
            return integers;
        }));
        Timer.stop("game play");
    }

    @Test
    public void testGames() throws Exception {
        Timer.start("test run time");
        int gamesPerThread = 10_000;
        int threads = Runtime.getRuntime().availableProcessors();
        System.out.println(String.format("threads: %d", threads));
        ExecutorService es = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            es.submit(() -> {
                for (int game = 0; game < gamesPerThread; game++) {
                    try {
                        Timer.start("entire game");
                        testPlay();
                        Timer.stop("entire game");
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        es.shutdown();
        es.awaitTermination(1L, TimeUnit.DAYS);
        Timer.stop("test run time");
        System.out.println(Timer.getStats());
        RoundStatsBuckets roundStats = new RoundStatsBuckets();
        stats.forEach((state, pointSpread) -> pointSpread.forEach(points -> roundStats.add(state, points)));
//        System.out.println(roundStats);
    }

}