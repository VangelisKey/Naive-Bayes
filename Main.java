
import java.util.*;
import java.io.*;

public class Main{

    public static void main(String[] args) {
		long start = System.currentTimeMillis();
		String pathname_train[] = new String[2];
		String pathname_test[] = new String[2];
		pathname_train[0]="aclImdb\\train\\pos\\";
		pathname_train[1]="aclImdb\\train\\neg\\";
		pathname_test[0]="aclImdb\\test\\pos\\";
		pathname_test[1]="aclImdb\\test\\neg\\";

		System.out.println("Training started.");
		MultinomialNaiveBayes classifier = new MultinomialNaiveBayes();//m=115782,n=1922,k=254
		
		classifier.train(pathname_train);
		System.out.println("Training finished, starting testing.");

		String[] sentiments = {"positive","negative"};
		int[] totals = {0,0};
		int[] corrects ={0,0};
		for(int sentiment = 0; sentiment<2;sentiment++){
			File folder = new File(pathname_test[sentiment]);
			File[] listOfFiles = folder.listFiles();
			for (int i = 0; i < listOfFiles.length; i++) {
				File file = listOfFiles[i];
				ArrayList<String> text_words;
				if (file.isFile() && file.getName().endsWith(".txt")) {
				  text_words= Input.readfiles(pathname_test[sentiment]+file.getName());
				}else{
					text_words= null;
					return;
				}
				totals[sentiment]++;
				if(classifier.predict(text_words).equals(sentiments[sentiment])){corrects[sentiment]++;}
			}
		}

		int total=totals[0]+totals[1];
		int correct=corrects[0]+corrects[1];
		float percentage = (float)correct*100/total;
		float pos_percentage = (float)corrects[0]*100/totals[0];
		float neg_percentage = (float)corrects[1]*100/totals[1];
		System.out.println("Total: "+ total +" total correct: "+correct +" total correct percentage: "+percentage+"%.");
		System.out.println("Positives percentage: "+pos_percentage+"%.");
		System.out.println("Negatives percentage: "+neg_percentage+"%.");
		long finish = System.currentTimeMillis();
		long timeElapsed = finish - start;
		System.out.println("Ran in:"+timeElapsed/1000+" seconds.");
    }

	
}