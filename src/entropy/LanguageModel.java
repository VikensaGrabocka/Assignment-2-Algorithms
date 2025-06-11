/*
 * Copyright (c) 10/6/2025 . Author @Vikensa Grabocka
 */


package entropy;

import java.util.*;
import java.util.stream.Collectors;



public class LanguageModel {
   private final int nGram;
   private int numLetters;
   private Map<String, Long> tokenFrequency = new HashMap<>();

    public LanguageModel(int nGram) {
        this.nGram = nGram;
    }


   public LanguageModel(int nGram, int numLetters) {
       this.nGram = nGram;
       this.numLetters = numLetters;
   }

    /**
     * Private method that normalizes one line of text. It removes all non-letter characters
     * @param line
     * @return the normalized version of the line
     */

    private String normalizeText(String line){
        String lowerCase = line.toLowerCase();
        String normalized = lowerCase.replaceAll("[^\\p{L} ]+", ""). replaceAll("\\s+", " ").trim();
        return normalized.isEmpty() ? null : normalized;
    }


    /**Method that populates the tokenFrequency map
     *For each line it generates the n-gram tokens
     * and calculates their corresponding frequency
     * @param line
     */
    public void calculateTokenFrequency(String line){
       String normalizedLine = normalizeText(line);
        if(normalizedLine == null || normalizedLine.isEmpty()){
            return;
        }
       String[] words = normalizedLine.trim().split(" ");
       for(String word : words){
           if(word.length()>=nGram){
               String token;
               for(int i=0; i<word.length()-nGram + 1; i++){
                   token = word.substring(i, i+nGram);
                   if(tokenFrequency.containsKey(token)){
                       tokenFrequency.put(token,(tokenFrequency.get(token))+1);
                   }else{
                       tokenFrequency.put(token,1L);
                   }

               }
           }
           else{
               if(tokenFrequency.containsKey(word)){
                   tokenFrequency.put(word,(tokenFrequency.get(word)+1));
               }else{
                   tokenFrequency.put(word,1L);
               }
           }
       }

    }

    /**
     * Method that calculates the entropy of the model
     * @return the entropy
     */
    public double calculateEntropy(){
       double entropy = 0.0;
        if(nGram==0){
            double prob = (double) 1/ (double) numLetters;
            entropy = Math.log(prob)/Math.log(2);
        }else{
            long totalFrequency = tokenFrequency.values().stream().mapToLong(Long::longValue).sum();
            for(Map.Entry<String, Long> entry : tokenFrequency.entrySet()){
                double prob = (double) entry.getValue() / (double) totalFrequency;
                double logBase2 = Math.log(prob)/Math.log(2);
                entropy += prob * logBase2;
            }
        }
        return -entropy;
    }

    /** Method that calculates the total number
     * of tokens encountered in the model
     * @return the total number of tokens
     */
    public long getTotalNumberOfTokens(){
       return tokenFrequency.values().stream().mapToLong(Long::longValue).sum();
    }

    /**
     * Method that finds the five most frequent tokens that appear in the model
     * @return a list that contains five most frequent tokens
     */
    public List<String> get5FrequentTokens(){
        return tokenFrequency.entrySet()
                .stream()
                .sorted(Map.Entry.<String,Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry:: getKey)
                .collect(Collectors.toList());

    }


    /**
     * Method that finds the five least frequent tokens that appear in the model
     * @return a list that contains five least frequent tokens
     */
    public List<String> get5LessFrequentTokens(){
        return tokenFrequency.entrySet()
                .stream()
                .sorted(Map.Entry.<String,Long>comparingByValue())
                .limit(5)
                .map(Map.Entry:: getKey)
                .collect(Collectors.toList());

    }

    /**
     * Method that calculates the resemblance between the language model and another model based
     * on cosine-similarity
     * @param unknownLanguageTokens
     * @return the cosine value, which corresponds to the resemblance between two models
     */
    public double calculateResemblance(Map<String, Long> unknownLanguageTokens){
       HashMap<String, Long> componentProduct = new HashMap<>();
       componentProduct.putAll(unknownLanguageTokens);
       for(String key : componentProduct.keySet()){
           if(tokenFrequency.containsKey(key)){
               componentProduct.put(key, componentProduct.get(key)*tokenFrequency.get(key));
           }else{
               componentProduct.put(key, 0L);
           }
       }
       long numerator = componentProduct.values().stream().mapToLong(Long::longValue).sum();

        long sum1 = unknownLanguageTokens.values().stream()
                .reduce( 0L, (a, b)->a+b*b);

        long sum2 = tokenFrequency.values().stream()
                .reduce(0L, (a,b)->a+b*b);

        return  (numerator/(Math.sqrt(sum1)*Math.sqrt(sum2)));

    }

    /**
     * Method that is used to get the map that contains the tokens and their corresponding
     * frequencies
     * @return
     */
    public Map<String, Long> getTokenFrequency(){
       return tokenFrequency;
    }


}
