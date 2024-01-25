import java.util.*;
import java.io.*;

public class MultinomialNaiveBayes {
    private TreeMap<String, Integer[]> vocabulary;
    private HashMap<ArrayList<String>,Integer> reviews;
    private int[] total_class_word_counts = {0,0};
    private ArrayList<int[]> data;
    private int word_count = 0;
    private double[] IG;

    
    public MultinomialNaiveBayes() {
        reviews = new HashMap<ArrayList<String>,Integer>();
        vocabulary = new TreeMap<String, Integer[]>();
        data = new ArrayList<int[]>();
    }
    
    public void add_to_vocabulary(ArrayList<String> review,int classIndex){
        //build vocabulary
        for (String word : review) {
            total_class_word_counts[classIndex]++;
            if (vocabulary.containsKey(word)) {
                vocabulary.get(word)[classIndex]+=1;
            } else {
                Integer[] count = {0,0,word_count};
                count[classIndex] = 1;
                vocabulary.put(word,count);
            }
        }

    }

	public void train(String pathname[]){
        for(int sentiment = 0; sentiment < 2; sentiment++){
            File folder = new File(pathname[sentiment]);
            File[] listOfFiles = folder.listFiles();
        
            for (int i = 0; i < listOfFiles.length; i++) {
                File file = listOfFiles[i];
                ArrayList<String> text_words;
                if (file.isFile() && file.getName().endsWith(".txt")) {
                text_words= Input.readfiles(pathname[sentiment]+file.getName());
                }else{
                    text_words= null;
                    return;
                }
                reviews.put(text_words,sentiment);
                add_to_vocabulary(text_words,sentiment);
            }
        }
    
        int m = 86;
        int k = 1455;
        int least_frequent_removed = 0;
        int most_frequent_removed = 0;
        Set<Map.Entry<String,Integer[]>> removal = new HashSet<Map.Entry<String,Integer[]>>(); 
        for(Map.Entry<String,Integer[]> entries:vocabulary.entrySet()){
            if(entries.getValue()[0]<=m||entries.getValue()[1]<=m||entries.getValue()[0]>k||entries.getValue()[1]>k){
                removal.add(entries);
                total_class_word_counts[0]-=entries.getValue()[0];
                total_class_word_counts[1]-=entries.getValue()[1];
            }
            if(entries.getValue()[0]<=m||entries.getValue()[1]<=m){

                least_frequent_removed++;
            }
            if(entries.getValue()[0]>k||entries.getValue()[1]>k){

                most_frequent_removed++;
            }
        }
        vocabulary.entrySet().removeAll(removal);
        System.out.println("Most frequent removed: "+ most_frequent_removed + ". Least frequent removed: "+least_frequent_removed+".");
        System.out.println("Words remaining in vocabulary: " + vocabulary.size()+".");
        /*/for(int word = 0; word < m; word++){
            total_class_word_counts[0]-=vocabulary.firstEntry().getValue()[0];
            total_class_word_counts[1]-=vocabulary.firstEntry().getValue()[1];
            vocabulary.pollFirstEntry();
        }
        for(int word = n; word < n+k; word++){
            total_class_word_counts[0]-=vocabulary.lastEntry().getValue()[0];
            total_class_word_counts[1]-=vocabulary.lastEntry().getValue()[1];
            vocabulary.pollLastEntry();
        }/* */

        for(String word: vocabulary.keySet()){
            vocabulary.get(word)[2]=word_count;
            word_count++;
        }

        for(ArrayList<String> review: reviews.keySet()){
            addToData(review,reviews.get(review));
        }

        IG =InformationGain.calculateIG(data);
    }
    
    public void addToData(ArrayList<String> review, int sentiment) {
        int[] existance= new int[vocabulary.size()+1];
        Arrays.fill(existance, sentiment);
        existance[vocabulary.size()] = sentiment;
            for (String word : review) {
                
                if(vocabulary.get(word)==null){continue;}
                existance[vocabulary.get(word)[2]]=1;
            }
        data.add(existance);  
    }

    public String predict(ArrayList<String> review){
        String prediction = null;
        double maxProb = Double.NEGATIVE_INFINITY;

        for (int sentiment = 0; sentiment < 2; sentiment++) {
            double prob = 0.5;

            for (String word : review){

                if(vocabulary.get(word)==null){continue;}
                int word_index = vocabulary.get(word)[2];
                if(IG[word_index]==0){continue;}
                int count = vocabulary.get(word)[sentiment];
                double wordProb = (double)count *IG[word_index] / total_class_word_counts[sentiment];
                prob *= wordProb;
            }

            if (prob > maxProb) {
                maxProb = prob;
                if(sentiment==0){
                    prediction = "positive";
                }else{
                    prediction = "negative";
                }
            }
        }
        return prediction;
    }
}