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
        System.out.println("20 Least Frequent Words");
        String[][] leastFrequentWords = get20LeastFrequentWords(file);
        for (String[] s : leastFrequentWords) {
            System.out.println("[" + s[0] + "," + s[1] + "]");
        }
        System.out.println("Word by Chapter");
        int[] frequenciesPerChapter = getFrequencyOfWord(file, "darcy");
        for (int i = 0; i < frequenciesPerChapter.length; i++) {
            System.out.println(frequenciesPerChapter[i]);
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
            words = line.replaceAll("[\\-+]", " ").replaceAll("[^0-9a-zA-Z\\s]", "").toLowerCase().split("\\s+");
            for (String word : words) {
                if (!uniqueWords.contains(word))
                    uniqueWords.add(word);
            }
        }
        return uniqueWords.size();
    }

    public static HashMap<String, Integer> getStringFrequencies(File file) throws IOException {
        HashMap<String, Integer> stringFrequencies = new HashMap<String, Integer>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line = "";

        while ((line = bufferedReader.readLine()) != null) {
            String[] words = line.replaceAll("[\\-+]", " ").replaceAll("[^0-9a-zA-Z\\s]", "").toLowerCase().split("\\s+");
            for (String word : words) {
                if (!word.equals(""))
                    stringFrequencies.put(word, (stringFrequencies.getOrDefault(word, 0)) + 1);
            }
        }

        return stringFrequencies;
    }

    public static String[][] formatStringCount (PriorityQueue<String> pq, HashMap<String, Integer> stringFrequencies) {
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

    public static String[][] get20MostFrequentWords(File file) throws IOException {
        HashMap<String, Integer> stringFrequencies = getStringFrequencies(file);
        PriorityQueue<String> pq = new PriorityQueue<String>( (a, b) -> stringFrequencies.get(a).compareTo(stringFrequencies.get(b)));
        for (String word : stringFrequencies.keySet()) {
            pq.add(word);
            if (pq.size() > 20) {
                pq.poll();
            }
        }
        return formatStringCount(pq, stringFrequencies);
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

        HashMap<String, Integer> stringFrequencies = getStringFrequencies(file);
        PriorityQueue<String> pq = new PriorityQueue<String>(Comparator.comparing(stringFrequencies::get));
        for (String word : stringFrequencies.keySet()) {
            if (!commonWords.contains(word))
                pq.add(word);
            if (pq.size() > 20) {
                pq.poll();
            }
        }
        return formatStringCount(pq, stringFrequencies);
    }

    public static String[][] get20LeastFrequentWords(File file) throws IOException {
        HashMap<String, Integer> stringFrequencies = getStringFrequencies(file);
        PriorityQueue<String> pq = new PriorityQueue<String>( (a, b) -> stringFrequencies.get(b).compareTo(stringFrequencies.get(a)));
        for (String word : stringFrequencies.keySet()) {
            pq.add(word);
            if (pq.size() > 20) {
                pq.poll();
            }
        }
        return formatStringCount(pq, stringFrequencies);
    }

    public static int[] getFrequencyOfWord(File file, String word) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line = "";

        ArrayList<Integer> frequencies = new ArrayList<Integer>();

        int chapterNumber = 1;

        while ((line = bufferedReader.readLine()) != null) {
            if (line.contains("Chapter " + chapterNumber)) {
                //start counting occurrences of the word
                int count = 0;
                while ((line = bufferedReader.readLine()) != null && !line.contains("Chapter " + (chapterNumber + 1))) {
                    String[] words = line.toLowerCase().replaceAll("[\\-+]", " ").replaceAll("[^0-9a-zA-Z\\s]", "").split("\\s+");
                    for (String lineWord : words) {
                        if (lineWord.equals(word))
                            count++;
                    }
                }
                frequencies.add(count);
                chapterNumber++;
            }
        }

        int[] result = new int[frequencies.size()];

        for (int i = 0; i < frequencies.size(); i++) {
            result[i] = frequencies.get(i);
        }
        return result;
    }
}

