package com.seedfinding.minemap.util.data;

import com.seedfinding.minemap.config.KeyboardsConfig;
import com.seedfinding.minemap.init.KeyShortcuts;
import com.seedfinding.minemap.init.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Searchable list of help messages. Queries are ranked (not exact matched): stop words are dropped, words are
 * stemmed, synonyms and typos are tolerated and rare words weigh more than common ones (TF-IDF flavoured), so
 * a question like "how do I take a picture of the map?" finds the screenshot shortcut.
 * A query wrapped in slashes (/regex/) is a plain case-insensitive grep instead.
 */
public class HelpIndex {
    public static final int MAX_RESULTS = 10;
    /** Words of the section heading ("Settings menu: ...") weigh more, asking for "settings" should find that section. */
    private static final double HEADING_BOOST = 2.0;
    private static final double MIN_RELATIVE_SCORE = 0.3;

    private static final String[] STATIC_MESSAGES = {
        "Mouse: left click and drag to move the map",
        "Mouse: scroll wheel to zoom in/out on the map",
        "Mouse: scroll wheel with the modifier key held to zoom the biome layer",
        "Mouse: right click on the map for the tab menu (save, rename, settings, chests, portal)",
        "Mouse: click a structure icon to display its chest loot (when the feature is selected)",
        "Tools: pick line, polygon, circle or ruler in the right toolbar, then left click on the map to draw",
        "Biome search: Ctrl+F, type a biome name, Enter jumps to the nearest one, Up/Down selects, Esc closes",
        "Tabs: each seed lives in its own tab, right click a tab to rename or save it",
        "Dock arrows: the < and > buttons on the right edge slide the side panels in and out, the upper one is the loot chest, the lower one is this help (hide them with {About > Hide dock arrows})",
        // loot chest panel
        "Loot arrow: click a structure icon that has loot, then open the upper < arrow on the right edge to see its chest",
        "Chest panel: the < and > buttons next to the chest count (like 1/3) cycle through the chests of the selected structure",
        "Chest panel: the title shows the structure name and its x and z position",
        "Chest panel: the Spread button shows the items in their real chest slots, Reassemble packs them back together",
        "Chest panel: hover an enchanted item or a potion to see its enchantments or effects in a tooltip",
        // menu overviews
        "Home options: New From Seed, Recent Seeds, Screenshot, Open Screenshot Folder and Close",
        "World options: Go to Coordinates, Go to Spawn, Go to Structure, Find Biome, Load Shadow Seed and Change Salts",
        "Utilities options: List N Structures, Find Chest Loot, Plan Route and Structure Seed Mode",
        "Settings options: Fragment Metric, Layer Switch Key, Restrict Maximum Zoom, Show height, Show heightmap instead of biomes, Fast startup, Disable stronghold and Shortcuts",
        "About options: UI Look, Biome Style, Hide dock arrows, Change Icons Size, Optimize heightmap, Open Settings Folder and About Minemap",
        // Home menu
        "Home menu: {Home > New From Seed} opens a seed in a new tab, you pick the version, thread count, biome and river size and the dimensions to load",
        "Home menu: {Home > Recent Seeds} lists the seeds you opened before, click one to load it again",
        "Home menu: {Home > Screenshot} saves a picture of the map, {Home > Open Screenshot Folder} opens the folder where they go",
        "Home menu: {Home > Close} quits MineMap",
        // World menu
        "World menu: {World > Go to Coordinates} asks for an x and a z and centers the map there",
        "World menu: {World > Go to Spawn} centers the map on the world spawn",
        "World menu: {World > Go to Structure} jumps to a structure of the type you choose",
        "World menu: {World > Find Biome} searches a biome by name and jumps to the nearest one (Ctrl+F)",
        "World menu: {World > Load Shadow Seed} opens the shadow seed of the current seed in a new tab (Overworld only)",
        "World menu: {World > Change Salts} edits the salt of each structure for the current version, the reset button restores the defaults",
        // Utilities menu
        "Utilities menu: {Utilities > List N Structures} lists the N closest structures of a type (1 to 300) with their coordinates",
        "Utilities menu: {Utilities > Find Chest Loot} finds the closest chests of a structure holding the item you choose",
        "Utilities menu: {Utilities > Plan Route} plans a short route through the N closest structures of a type and draws it on the map, tick Return to the starting point to make it a loop",
        "Utilities menu: {Utilities > Structure Seed Mode} shows structures without checking biomes or terrain, so they are valid for all the sister seeds sharing the same 48 lower bits",
        // Settings menu
        "Settings menu: {Settings > Fragment Metric} (Euclidean, Manhattan or Chebyshev) is the distance used to decide which map tiles load first around the center",
        "Settings menu: {Settings > Layer Switch Key} (Ctrl, Shift, Alt, AltGr or Meta) is the key to hold with the mouse wheel to zoom the biome layer",
        "Settings menu: {Settings > Restrict Maximum Zoom} limits how far you can zoom in",
        "Settings menu: {Settings > Show height} adds the terrain height under the cursor to the coordinates display",
        "Settings menu: {Settings > Show heightmap instead of biomes} draws the terrain height in grayscale instead of the biome colors",
        "Settings menu: {Settings > Fast startup (Warning Flashes!)} does not wait for the stronghold calculation, the map redraws when it is done and may flash",
        "Settings menu: {Settings > Disable stronghold (Even faster!)} skips the stronghold calculation completely",
        "Settings menu: {Settings > Shortcuts} lets you change any keyboard shortcut, Reset shortcuts restores the defaults",
        // About menu
        "About menu: {About > Change Icons Size} sets the size of the structure icons on the map",
        "About menu: {About > Optimize heightmap} sets how coarsely the terrain height is sampled (1 to 16), a higher value is faster but less precise",
        "About menu: {About > Open Settings Folder} opens the folder where your settings are saved",
        // seed terminology
        "World Seed: a 64 bit seed, it is the only thing that determines a world (except the time of creation for some events not related to world generation)",
        "Shadow Seed: a 64 bit seed that has the same biomes as the world seed but not the same terrain nor the same structures, see {World > Load Shadow Seed}",
        "Structure Seed: the 48 lower bits of the world seed, it is salted and seeded with the chunk x and chunk z to find the structure positions",
        "Population Seed: determined from the coordinates and the structure seed, used for all the elements created on the terrain, also called chunkseed",
        "Chunkseed: see Population Seed, the seed derived from a chunk position and the structure seed",
        "Terrain Seed: determined from the structure seed and the chunk x and chunk z, used to make the chunk height values",
        "Decoration Seed: one per element of the population stage, fully determined by a step index (depending on the importance of the decorator) and an index in that step, it uses the population seed",
        "Slime Seed: the seed that decides slime chunks, made from the structure seed and the chunk x and chunk z, it is one of the only ones that is not a linear equation (it is a second order polynomial)",
        "Offsets: how far a number is from another, for an LCG it is the addend part of (multiplier * seed + addend)",
        "Salts: a number mixed into the seed of each structure so that every structure type gets different positions, see {World > Change Salts} to edit them",
        "Attack: cracking a seed, the means of exploiting a weakness in a generator or an algorithm (for example the LCG behind Java Random) to recover the seed",
        "Seed Space: the entire set of seeds that are potential candidates for the original seed of Java Random",
        "Theme: {About > UI Look} picks a light or dark theme, {About > Biome Style} changes the biome colors",
        "Help: ask a question or type a few keywords, the best matches are ranked; /regex/ greps instead",
    };

    private static final String HELP_DIR = "/others/help/";
    private static final String STOP_WORDS_FILE = "stopwords.txt";
    private static final String SYNONYMS_FILE = "synonyms.txt";

    /** Loaded from {@code stopwords.txt}. */
    private static final Set<String> STOP_WORDS = loadStopWords();
    /** Stemmed word -> stemmed words with the same meaning, loaded from {@code synonyms.txt}. */
    private static final Map<String, List<String>> SYNONYMS = loadSynonyms();

    private static Set<String> loadStopWords() {
        Set<String> words = new HashSet<>();
        for (String line : readLines(STOP_WORDS_FILE)) {
            for (String word : line.trim().toLowerCase(Locale.ROOT).split("\\s+")) {
                if (!word.isEmpty()) words.add(word);
            }
        }
        return words;
    }

    private static Map<String, List<String>> loadSynonyms() {
        Map<String, List<String>> synonyms = new HashMap<>();
        for (String line : readLines(SYNONYMS_FILE)) {
            List<String> group = new ArrayList<>();
            for (String word : line.split(",")) {
                String stemmed = stem(word.trim());
                if (!stemmed.isEmpty() && !group.contains(stemmed)) group.add(stemmed);
            }
            for (String word : group) {
                List<String> others = synonyms.computeIfAbsent(word, k -> new ArrayList<>());
                for (String other : group) {
                    if (!other.equals(word) && !others.contains(other)) others.add(other);
                }
            }
        }
        return synonyms;
    }

    /** Lines of a bundled file (in the resources "others/help" folder) without the # comments and blank lines. */
    private static List<String> readLines(String file) {
        try (InputStream in = HelpIndex.class.getResourceAsStream(HELP_DIR + file)) {
            if (in == null) {
                Logger.LOGGER.severe("Help file not found: " + HELP_DIR + file);
                return List.of();
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8).lines()
                .map(line -> line.contains("#") ? line.substring(0, line.indexOf('#')) : line)
                .filter(line -> !line.isBlank())
                .toList();
        } catch (IOException e) {
            Logger.LOGGER.severe("Could not read help file " + file + ": " + e);
            return List.of();
        }
    }

    private HelpIndex() {
    }

    /**
     * @return every help message: the static ones plus one line per keyboard shortcut.
     */
    public static List<String> all() {
        List<String> messages = new ArrayList<>(List.of(STATIC_MESSAGES));
        for (KeyShortcuts.ShortcutAction action : KeyShortcuts.ShortcutAction.values()) {
            String combo = KeyboardsConfig.getKeyComboString(action);
            if (combo == null || combo.isEmpty()) continue;
            messages.add("Shortcut: " + action.name().toLowerCase().replace('_', ' ') + " = " + combo);
        }
        return messages;
    }

    public static List<String> search(String query) {
        return search(all(), query);
    }

    /**
     * Best matches first. A blank query returns everything in the original order.
     */
    public static List<String> search(List<String> messages, String query) {
        if (query == null || query.isBlank()) return new ArrayList<>(messages);
        String q = query.trim();
        if (q.length() > 2 && q.startsWith("/") && q.endsWith("/")) {
            return grep(messages, q.substring(1, q.length() - 1));
        }

        List<String> terms = significantTerms(q);
        if (terms.isEmpty()) return new ArrayList<>();

        List<List<String>> docs = new ArrayList<>();
        List<Set<String>> headings = new ArrayList<>();
        Map<String, Integer> docFrequency = new HashMap<>();
        for (String message : messages) {
            List<String> tokens = stemmedTokens(message);
            docs.add(tokens);
            int colon = message.indexOf(": ");
            headings.add(colon > 0 && colon < 20 ? new HashSet<>(stemmedTokens(message.substring(0, colon))) : Set.of());
            for (String token : new HashSet<>(tokens)) docFrequency.merge(token, 1, Integer::sum);
        }

        double[] scores = new double[messages.size()];
        double best = 0;
        for (int i = 0; i < docs.size(); i++) {
            scores[i] = score(terms, docs.get(i), headings.get(i), docFrequency, messages.size());
            best = Math.max(best, scores[i]);
        }
        if (best <= 0) return new ArrayList<>();

        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] >= best * MIN_RELATIVE_SCORE) order.add(i);
        }
        // stable sort keeps the original order for equal scores
        order.sort((a, b) -> Double.compare(scores[b], scores[a]));
        List<String> result = new ArrayList<>();
        for (int i : order) {
            if (result.size() >= MAX_RESULTS) break;
            result.add(messages.get(i));
        }
        return result;
    }

    /**
     * Basic grep: case-insensitive regex, falls back to a literal match if the regex is invalid.
     */
    public static List<String> grep(List<String> messages, String query) {
        if (query == null || query.isBlank()) return new ArrayList<>(messages);
        Pattern pattern;
        try {
            pattern = Pattern.compile(query.trim(), Pattern.CASE_INSENSITIVE);
        } catch (PatternSyntaxException e) {
            pattern = Pattern.compile(Pattern.quote(query.trim()), Pattern.CASE_INSENSITIVE);
        }
        List<String> result = new ArrayList<>();
        for (String message : messages) {
            if (pattern.matcher(message).find()) result.add(message);
        }
        return result;
    }

    private static double score(List<String> terms, List<String> doc, Set<String> heading, Map<String, Integer> docFrequency, int docCount) {
        double total = 0;
        int matchedTerms = 0;
        for (String term : terms) {
            double best = 0;
            for (String token : doc) {
                double weight = idf(docFrequency.getOrDefault(token, 1), docCount) * (heading.contains(token) ? HEADING_BOOST : 1.0);
                best = Math.max(best, weight * similarity(term, token));
            }
            if (best > 0) matchedTerms++;
            total += best;
        }
        // reward messages that cover more of the question
        return total * (0.5 + 0.5 * matchedTerms / terms.size());
    }

    private static double idf(int df, int docCount) {
        return Math.log(1.0 + (double) docCount / df);
    }

    /** 1 exact, 0.7 synonym, 0.6 prefix, 0.5 typo (edit distance 1), else 0. */
    private static double similarity(String term, String token) {
        if (term.equals(token)) return 1.0;
        List<String> synonyms = SYNONYMS.get(term);
        if (synonyms != null && synonyms.contains(token)) return 0.7;
        if (term.length() >= 3 && token.length() >= 3 && (token.startsWith(term) || term.startsWith(token))
            && Math.min(term.length(), token.length()) >= 0.75 * Math.max(term.length(), token.length())) return 0.6;
        if (term.length() >= 5 && Math.abs(term.length() - token.length()) <= 1 && editDistanceAtMostOne(term, token)) return 0.5;
        return 0;
    }

    private static boolean editDistanceAtMostOne(String a, String b) {
        if (a.length() < b.length()) return editDistanceAtMostOne(b, a);
        int i = 0;
        while (i < b.length() && a.charAt(i) == b.charAt(i)) i++;
        if (i == b.length()) return a.length() - b.length() <= 1;
        if (a.length() == b.length()) return a.substring(i + 1).equals(b.substring(i + 1)); // substitution
        return a.substring(i + 1).equals(b.substring(i)); // deletion
    }

    private static List<String> significantTerms(String query) {
        List<String> terms = new ArrayList<>();
        for (String raw : tokenize(query)) {
            if (STOP_WORDS.contains(raw)) continue;
            String s = stem(raw);
            if (!terms.contains(s)) terms.add(s);
        }
        return terms;
    }

    private static List<String> stemmedTokens(String text) {
        List<String> tokens = new ArrayList<>();
        for (String raw : tokenize(text)) {
            if (!STOP_WORDS.contains(raw)) tokens.add(stem(raw));
        }
        return tokens;
    }

    private static List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        // keep "+" and "-" out so "NumPad +" is just "numpad"; split on everything that is not a letter or digit
        for (String s : text.toLowerCase(Locale.ROOT).split("[^a-z0-9]+")) {
            if (!s.isEmpty()) tokens.add(s);
        }
        return tokens;
    }

    /** Tiny suffix stripper: enough to match zoom/zooming/zooms, biome/biomes, structure/structures. */
    static String stem(String word) {
        String w = word.toLowerCase(Locale.ROOT);
        if (w.length() > 5 && w.endsWith("ing")) w = w.substring(0, w.length() - 3);
        else if (w.length() > 4 && w.endsWith("ed")) w = w.substring(0, w.length() - 2);
        else if (w.length() > 4 && w.matches(".*(s|x|z|ch|sh)es")) w = w.substring(0, w.length() - 2);
        else if (w.length() > 3 && w.endsWith("s") && !w.endsWith("ss")) w = w.substring(0, w.length() - 1);
        return w;
    }
}
