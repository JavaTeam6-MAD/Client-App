package com.mycompany.presentation.networkgame;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GameRecorder {

    private static GameRecorder instance;
    private boolean isRecording = false;
    private RecordedGame currentGame;

    private GameRecorder() {
    }

    public static GameRecorder getInstance() {
        if (instance == null) {
            instance = new GameRecorder();
        }
        return instance;
    }

    public void startRecording(String gameId, String player1, String player2, String p1Symbol, String p2Symbol) {
        isRecording = true;
        currentGame = new RecordedGame();
        currentGame.gameId = gameId;
        currentGame.player1 = player1;
        currentGame.player2 = player2;
        currentGame.date = new Date().getTime();
        currentGame.moves = new ArrayList<>();
    }

    public void recordMove(int row, int col, String symbol) {
        if (!isRecording || currentGame == null)
            return;
        currentGame.moves.add(new Move(row, col, symbol));
    }

    public void stopRecording() {
        isRecording = false;
        currentGame = null;
    }

    public void saveGame(String winner, String status) {
        if (!isRecording || currentGame == null)
            return;

        currentGame.winner = winner;

        // Save to file
        String fileName = "recorded_games/game_" + currentGame.date + ".json";
        File dir = new File("recorded_games");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(toJson(currentGame));
            System.out.println("Game saved to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        stopRecording();
    }

    public boolean isRecording() {
        return isRecording;
    }

    // Simple JSON construction manually to avoid dependency issues if Gson is
    // missing
    private String toJson(RecordedGame game) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"gameId\": \"").append(game.gameId).append("\",\n");
        sb.append("  \"player1\": \"").append(game.player1).append("\",\n");
        sb.append("  \"player2\": \"").append(game.player2).append("\",\n");
        sb.append("  \"date\": ").append(game.date).append(",\n");
        sb.append("  \"winner\": \"").append(game.winner == null ? "DRAW" : game.winner).append("\",\n");
        sb.append("  \"moves\": [\n");
        for (int i = 0; i < game.moves.size(); i++) {
            Move m = game.moves.get(i);
            sb.append("    {\"r\":").append(m.r).append(", \"c\":").append(m.c).append(", \"s\":\"").append(m.s)
                    .append("\"}");
            if (i < game.moves.size() - 1)
                sb.append(",");
            sb.append("\n");
        }
        sb.append("  ]\n");
        sb.append("}");
        return sb.toString();
    }

    // Inner classes
    public static class RecordedGame {
        public String gameId;
        public String player1;
        public String player2;
        public long date;
        public String winner;
        public List<Move> moves;
    }

    public List<RecordedGame> listRecordings() {
        List<RecordedGame> games = new ArrayList<>();
        File dir = new File("recorded_games");
        if (!dir.exists() || !dir.isDirectory())
            return games;

        for (File f : dir.listFiles()) {
            if (f.getName().endsWith(".json")) {
                try {
                    RecordedGame g = parseGame(f);
                    if (g != null)
                        games.add(g);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return games;
        // Sort by date desc?
    }

    private RecordedGame parseGame(File f) throws IOException {
        java.util.Scanner s = new java.util.Scanner(f).useDelimiter("\\A");
        String content = s.hasNext() ? s.next() : "";
        s.close();

        // Very basic manual parsing consistent with toJson
        RecordedGame g = new RecordedGame();
        g.gameId = extract(content, "gameId");
        g.player1 = extract(content, "player1");
        g.player2 = extract(content, "player2");
        String dateStr = extractOnly(content, "date\": ", ",");
        try {
            g.date = Long.parseLong(dateStr.trim());
        } catch (Exception e) {
        }
        g.winner = extract(content, "winner");

        // Parse moves - rough approx
        g.moves = new ArrayList<>();
        String movesBlock = content.substring(content.indexOf("\"moves\": [") + 10, content.lastIndexOf("]"));
        String[] moveLines = movesBlock.split("\\},"); // Split by object end
        for (String line : moveLines) {
            if (line.trim().isEmpty())
                continue;
            int r = Integer.parseInt(extractOnly(line, "\"r\":", ","));
            int c = Integer.parseInt(extractOnly(line, "\"c\":", ","));
            String sym = extractOnly(line, "\"s\":\"", "\"");
            g.moves.add(new Move(r, c, sym));
        }

        return g;
    }

    private String extract(String json, String key) {
        String pattern = "\"" + key + "\": \"";
        int start = json.indexOf(pattern);
        if (start == -1)
            return "";
        start += pattern.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }

    private String extractOnly(String text, String startMarker, String endMarker) {
        int start = text.indexOf(startMarker);
        if (start == -1)
            return "0";
        start += startMarker.length();
        int end = text.indexOf(endMarker, start);
        if (end == -1) // maybe end of string or }
            end = text.indexOf("}", start);
        return text.substring(start, end).trim();
    }

    public static class Move {
        public int r;
        public int c;
        public String s;

        public Move(int r, int c, String s) {
            this.r = r;
            this.c = c;
            this.s = s;
        }
    }
}
