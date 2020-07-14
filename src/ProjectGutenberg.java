import java.io.*;
import java.util.*;

public class ProjectGutenberg {
    public static void main(String[] arg) throws IOException {
        File file = new File("./1342.txt");
        System.out.println(getTotalNumberOfWords(file));
        System.out.println(getTotalUniqueWords(file));
        System.out.println("20 Most Frequent Words:");
        String[][] result = get20MostFrequentWords(file);
        for (String[] s : result) {
            System.out.println("[" + s[0] + "," + s[1] + "]");
        }
        System.out.println("20 Most Interesting Frequent Words:");
        String[][] interestingWords = get20MostInterestingFrequentWords(file);
        for (String[] s : interestingWords) {
            System.out.println("[" + s[0] + "," + s[1] + "]");
        }
    }

    public static int getTotalNumberOfWords(File file) throws IOException {
        int countWords = 0;
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line = "";
        String[] words;
        while ((line = bufferedReader.readLine()) != null) {
            words = line.split("\\s+");
            countWords += words.length;
        }
        return countWords;
    }

    public static int getTotalUniqueWords(File file) throws IOException {
        Set<String> uniqueWords = new HashSet<String>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line = "";
        String[] words;
        while ((line = bufferedReader.readLine()) != null) {
            words = line.replaceAll("[^0-9a-zA-Z\\s]", "").toLowerCase().split("\\s+");
            for (String word : words) {
                if (!uniqueWords.contains(word))
                    uniqueWords.add(word);
            }
        }
        return uniqueWords.size();
    }

    public static String[][] get20MostFrequentWords(File file) throws IOException {
        HashMap<String, Integer> stringFrequencies = new HashMap<String, Integer>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line = "";

        while ((line = bufferedReader.readLine()) != null) {
            String[] words = line.replaceAll("[^0-9a-zA-Z\\s]", "").toLowerCase().split("\\s+");
            for (String word : words) {
                if (!word.equals(""))
                    stringFrequencies.put(word, (stringFrequencies.getOrDefault(word, 0)) + 1);
            }
        }

        PriorityQueue<String> pq = new PriorityQueue<String>( (a, b) -> stringFrequencies.get(a).compareTo(stringFrequencies.get(b)));
        for (String word : stringFrequencies.keySet()) {
            pq.add(word);
            if (pq.size() > 20) {
                pq.poll();
            }
        }

        String[][] result = new String[20][2];
        int index = 19;
        while (!pq.isEmpty() && index >= 0) {
            String word = pq.poll();
            String[] t = {word, Integer.toString(stringFrequencies.get(word))};
            result[index] = t;
            index--;
        }

        return result;
    }

    public static String[][] get20MostInterestingFrequentWords(File file) throws IOException {
        //Read most common words from 1-1000.txt
        Set<String> commonWords = new HashSet<String>();
        File commonWordsFile = new File("./1-1000.txt");
        BufferedReader commonWordsReader = new BufferedReader(new FileReader(commonWordsFile));
        String l = "";
        for (int i = 0; i < 100; i++) {
            l = commonWordsReader.readLine();
            commonWords.add(l.toLowerCase().replaceAll("\\s+", ""));
        }

        HashMap<String, Integer> stringFrequencies = new HashMap<String, Integer>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line = "";

        while ((line = bufferedReader.readLine()) != null) {
            String[] words = line.replaceAll("[^0-9a-zA-Z\\s]", "").toLowerCase().split("\\s+");
            for (String word : words) {
                if (!word.equals("") && !commonWords.contains(word))
                    stringFrequencies.put(word, (stringFrequencies.getOrDefault(word, 0)) + 1);
            }
        }

        PriorityQueue<String> pq = new PriorityQueue<String>( (a, b) -> stringFrequencies.get(a).compareTo(stringFrequencies.get(b)));
        for (String word : stringFrequencies.keySet()) {
            pq.add(word);
            if (pq.size() > 20) {
                pq.poll();
            }
        }

        String[][] result = new String[20][2];
        int index = 19;
        while (!pq.isEmpty() && index >= 0) {
            String word = pq.poll();
            String[] t = {word, Integer.toString(stringFrequencies.get(word))};
            result[index] = t;
            index--;
        }
        return result;
    }
}

