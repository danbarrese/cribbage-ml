package net.pladform.cribbage.ml;

import net.pladform.cribbage.engine.Card;
import net.pladform.cribbage.engine.Deck;
import net.pladform.cribbage.engine.Game;
import net.pladform.cribbage.engine.Player;
import net.pladform.cribbage.engine.Scoreboard;
import net.pladform.cribbage.engine.Suit;
import net.pladform.perf.Timer;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        Scoreboard scoreboard = new Scoreboard();
        Deck deck = new Deck();
        Game game = new Game(scoreboard, deck);
        Player dan = new Player("Dan", game);
        Player ken = new Player("Ken", game);
        scoreboard.setPlayers(new Player[]{dan, ken});
        scoreboard.setDealer(dan);
        Timer.start("game play");
        Map<String, List<Integer>> gameStats = game.play();
        Timer.stop("game play");
        gameStats.forEach((state, finalPoints) -> stats.merge(state, finalPoints, (integers, integers2) -> {
            integers.addAll(integers2);
            return integers;
        }));
        System.out.println(gameStats);
    }

    @Test
    public void testGames() throws Exception {
//        Timer.disable();
        int gamesPerThread = 10;
//        int threads = Runtime.getRuntime().availableProcessors();
        int threads = 1;
        System.out.println(String.format("threads: %d", threads));
        ExecutorService es = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            es.submit(() -> {
                for (int game = 0; game < gamesPerThread; game++) {
                    try {
                        Timer.start("entire game");
                        testPlay();
                        Timer.stop("entire game");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        es.shutdown();
        es.awaitTermination(1L, TimeUnit.DAYS);
        System.out.println(Timer.getStats());
        RoundStatsBuckets roundStats = new RoundStatsBuckets();
        stats.forEach((state, pointSpread) -> pointSpread.forEach(points -> roundStats.add(state, points)));
//        System.out.println(roundStats);
    }

//    @Test
//    public void testGameFromStartingPoint() throws Exception {
//        Scoreboard scoreboard = new Scoreboard();
//        Deck deck = new Deck();
//        Game game = new Game(scoreboard, deck);
//        Player dan = new Player("Dan", game);
//        Player ken = new Player("Ken", game);
//        scoreboard.setPlayers(new Player[]{dan, ken});
//        scoreboard.setDealer(dan);
//
//        Set<Card> cards = new HashSet<>();
//        cards.add(deck.select(Card.Type.ACE, Suit.CLUBS));
//        cards.add(deck.select(Card.Type._2, Suit.SPADES));
//        cards.add(deck.select(Card.Type._3, Suit.DIAMONS));
//        cards.add(deck.select(Card.Type._4, Suit.HEARTS));
//        cards.add(deck.select(Card.Type._5, Suit.CLUBS));
//        cards.add(deck.select(Card.Type._6, Suit.SPADES));
//
//        Timer.start("game play");
//        Map<String, List<Integer>> gameStats = game.playFromStartingPoint(dan, cards);
//        Timer.stop("game play");
//        gameStats.forEach((state, finalPoints) -> stats.merge(state, finalPoints, (integers, integers2) -> {
//            integers.addAll(integers2);
//            return integers;
//        }));
//    }

}