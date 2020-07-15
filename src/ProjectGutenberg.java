import java.io.*;
import java.util.*;

public class ProjectGutenberg {
    public static void main(String[] arg) throws IOException {
        File file = new File("./215-0.txt");
        System.out.println("Total number of words: " + getTotalNumberOfWords(file));
        System.out.println("Total number of unique words: " + getTotalUniqueWords(file));

        System.out.println("20 Most Frequent Words:");
        String[][] result = get20MostFrequentWords(file);
        String frequentWords = "[";
        for (String[] s : result) {
            frequentWords += "[" + s[0] + "," + s[1] + "]";
        }
        System.out.println(frequentWords + "]");

        System.out.println("20 Most Interesting Frequent Words:");
        String[][] interestingWords = get20MostInterestingFrequentWords(file);
        String interestingWordsString = "[";
        for (String[] s : interestingWords) {
            interestingWordsString += "[" + s[0] + "," + s[1] + "]";
        }
        System.out.println(interestingWordsString + "]");

        System.out.println("20 Least Frequent Words");
        String[][] leastFrequentWords = get20LeastFrequentWords(file);
        String leastFrequentWordsString = "[";
        for (String[] s : leastFrequentWords) {
            leastFrequentWordsString += "[" + s[0] + "," + s[1] + "]";
        }
        System.out.println(leastFrequentWordsString + "]");

        System.out.println("Word by Chapter");
        int[] frequenciesPerChapter = getFrequencyOfWord(file, "spitz");
        String s = "[";
        for (int i = 0; i < frequenciesPerChapter.length; i++) {
            if (i == 0)
                s += frequenciesPerChapter[i];
            else
                s += ", " + frequenciesPerChapter[i];
        }
        System.out.println(s + "]");

        System.out.println("Get Chapter by Quote");
        System.out.println(getChapterQuoteAppears(file, "Love, genuine passionate love, was his for the first time."));

        System.out.println("Generate Sentence");
        System.out.println(generateSentence(file));
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
        PriorityQueue<String> pq = new PriorityQueue<String>(Comparator.comparing(stringFrequencies::get));
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

        //find first line with Chapter in it
        while ((line = bufferedReader.readLine()) != null && !line.contains("Chapter ")) { }

        while (line != null) {
            int count = 0;
            while ((line = bufferedReader.readLine()) != null && !line.contains("Chapter ")) {
                String[] words = line.toLowerCase().replaceAll("[\\-+]", " ").replaceAll("[^0-9a-zA-Z\\s]", "").split("\\s+");
                for (String lineWord : words) {
                    if (lineWord.equals(word))
                        count++;
                }
            }
            frequencies.add(count);
        }

        int[] result = new int[frequencies.size()];
        for (int i = 0; i < frequencies.size(); i++) {
            result[i] = frequencies.get(i);
        }
        return result;
    }

    public static int getChapterQuoteAppears(File file, String quote) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line = "";

        //find first line with Chapter in it
        while ((line = bufferedReader.readLine()) != null && !line.contains("Chapter ")) { }

        int chapterNumber = 1;
        String chapter = "";
        while (line != null) {
            chapter = "";
            while ((line = bufferedReader.readLine()) != null && !line.contains("Chapter ")) {
                chapter += " " + line;
            }
            if (chapter.contains(quote))
                return chapterNumber;
            chapterNumber++;
        }
        return -1;
    }

    public static String generateSentence(File file) throws IOException {
        String sentence = "The";
        String currentWord = "the";

        HashMap<String, Integer> stringFrequencies = getStringFrequencies(file);
        PriorityQueue<String> pq = new PriorityQueue<String>(Comparator.comparing(stringFrequencies::get));

        ArrayList<String> nextWords = new ArrayList<String>();

        for (int i = 0; i < 19; i++) {
            //parse through book for possible next words
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                String[] words = line.replaceAll("[\\-+]", " ").replaceAll("[^0-9a-zA-Z\\s]", "").toLowerCase().split("\\s+");
                for (int j = 0; j < words.length - 1; j++) {
//                    if (words[j].equals(currentWord) && !pq.contains(words[j+1]))
//                        pq.add(words[j+1]);
                    if(words[j].equals(currentWord) && !nextWords.contains(words[j+1]))
                        nextWords.add(words[j+1]);
                }
            }
            //find most likely word and add it to the sentence
//            String newWord = pq.poll();
            //get random word in HashSet
            int randomIndex = (int)(Math.floor(Math.random()*nextWords.size()));
            String newWord = nextWords.get(randomIndex);
            sentence += " " + newWord;
            currentWord = newWord;
            pq.clear();
        }

        return sentence + ".";
    }
}

