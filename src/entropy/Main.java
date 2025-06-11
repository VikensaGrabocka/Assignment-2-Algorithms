/*
 * Copyright (c) 10/6/2025 . Author @Vikensa Grabocka
 */


package entropy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int numAlbanianLetters = 36;
        int numOfNGrams = 11;
        String path1 ="folder/Albanian/encyclopedia.txt";
        String path2 ="folder/Albanian/news.txt";
        String path3 ="folder/English/english.txt";
        String[][] textsInfo = {{path1,"encyclopedia"},{path2,"newspapers"}};


        LanguageModel[] languageModels = new LanguageModel[numOfNGrams];
        LanguageModel[] languageModels2 = new LanguageModel[numOfNGrams];
        for (int i = 0; i < languageModels.length; i++) {
            languageModels[i] = new LanguageModel(i, numAlbanianLetters);
            languageModels2[i] = new LanguageModel(i, numAlbanianLetters);//this array contains the languageModel objects for the second category
        }

        for (int k = 0; k < textsInfo.length; k++) {
            try (BufferedReader reader = new BufferedReader(new FileReader(textsInfo[k][0]))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    for (int i = 1; i < languageModels.length; i++) {
                        languageModels[i].calculateTokenFrequency(line);
                        if(k == 1){
                            languageModels2[i].calculateTokenFrequency(line);
                        }
                    }


                }
                displayResults( k == 0? languageModels : languageModels2, "-----Category:  " + textsInfo[k][1]+ "-----");


            }catch(IOException e){
                System.out.println("Error opening file");;
            }

        }

       displayResults(languageModels, "-----Albanian Language-----");


        try(BufferedReader reader = new BufferedReader(new FileReader(path3))){
           int numLettersEnglish = 26;
           LanguageModel languageModelEnglish = new LanguageModel(2, numLettersEnglish);
           String line;
           while ((line = reader.readLine()) != null) {
               languageModelEnglish.calculateTokenFrequency(line);
           }

            Scanner input = new Scanner(System.in);
            System.out.println("Please enter a sentence: ");
            String sentence = input.nextLine();

            LanguageModel unknownLanguageModel = new LanguageModel(2);
            unknownLanguageModel.calculateTokenFrequency(sentence);



            double resemblanceAlbanian = languageModels[2].calculateResemblance(unknownLanguageModel.getTokenFrequency());
            double resemblanceEnglish = languageModelEnglish.calculateResemblance(unknownLanguageModel.getTokenFrequency());

            System.out.println("Similarity  with English language is: "+ resemblanceEnglish);
            System.out.println("Similarity  with Albanian language is: "+ resemblanceAlbanian);
            if(resemblanceAlbanian == 0 && resemblanceEnglish == 0){
                System.out.println("Language not found!");
            }else if(resemblanceAlbanian > resemblanceEnglish){
                System.out.println("The sentence is in Albanian language!");
            }else if(resemblanceAlbanian < resemblanceEnglish) {
                System.out.println("The sentence is in English language!");
            }else{
                System.out.println("The sentence might be in English/ Albanian language!");
            }


        }catch (IOException e){
            System.out.println("Error opening file");
        }


    }


    /**
     * Private method that is used to display the results
     * for each model
     * @param languageModels
     * @param description
     */

    private static void displayResults(LanguageModel[] languageModels, String description) {
        for (int i = 0; i < languageModels.length; i++) {
            System.out.println(description);
            System.out.println(i + " gram");
            System.out.println("Entropy: " +  languageModels[i].calculateEntropy());
            System.out.println("Total number of tokens: " + (i==0 ? "N/A" : languageModels[i].getTotalNumberOfTokens()));
            System.out.println("Five most frequent tokens: " + (i==0 ? "N/A" : languageModels[i].get5FrequentTokens()));
            System.out.println("Five least frequent tokens: " + ( i==0 ? "N/A" :languageModels[i].get5LessFrequentTokens()));
        }
    }


}
