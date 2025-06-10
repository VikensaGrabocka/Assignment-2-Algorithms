package entropy;

import java.util.*;
import java.util.stream.Collectors;

/*
 * Copyright (c) 7/6/2025 . Author @Vikensa Grabocka
 */

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
//        return lowerCase.replaceAll("[!\"#$%&'()*+,-./:;<=>?@\\[\\]^_`{|}~0-9]","");
//        return lowerCase.replaceAll("[^\\p{L} ]+", ""). replaceAll("\\s+", " ").trim();
        String normalized = lowerCase.replaceAll("[^a-zA-ZëçËÇ ]+", "")
                .replaceAll("\\s+", " ")
                .trim();
        return normalized.isEmpty() ? null : normalized;
    }


    /**Method that populates the tokenFrequency map
     *For each line it generates the n-gram tokens
     * and calculates their corresponding frequency
     * @param line
     */
    public void calculateTokenFrequency(String line){
       String normalizedLine = normalizeText(line);
//       String[] words = normalizedLine.trim().split("\\s+");
        if(normalizedLine == null || normalizedLine.isEmpty()){
            return;
        }
       String[] words = normalizedLine.trim().split(" ");
       for(String word : words){
           //here we check if the string has more letters than n of the n-gram
           if(word.length()>=nGram){
               String token;
               for(int i=0; i<word.length()-nGram + 1; i++){
                   token = word.substring(i, i+nGram);
                   if (!token.matches("^[a-zA-ZëçËÇ]+$")) {  // ^ and $ ensure full-string match
                       continue;
                   }

                   if(tokenFrequency.containsKey(token)){
                       tokenFrequency.put(token,(tokenFrequency.get(token))+1);
                   }else{
                       tokenFrequency.put(token,1L);
                   }

               }
           }
           else{//this means that the string is less than ngram size so we just add it
               if(tokenFrequency.containsKey(word)){
                   tokenFrequency.put(word,(tokenFrequency.get(word)+1));
               }else{
                   tokenFrequency.put(word,1L);
               }
           }
       }

    }

    public double calculateEntropy(){
       double entropy = 0.0;
        if(nGram==0){
            double prob = (double) 1/ (double) numLetters;
            double logBase2 = Math.log(prob)/Math.log(2);
            entropy =  numLetters *  prob * logBase2;
        }else{
            long totalFrequency = tokenFrequency.values().stream().mapToLong(Long::longValue).sum();
            for(Map.Entry<String, Long> entry : tokenFrequency.entrySet()){
                double prob = (double) entry.getValue() / (double) totalFrequency;
                double logBase2 = Math.log(prob)/Math.log(2);
                entropy += prob * logBase2;
            }
        }
//        return  nGram== 0 ? -entropy: -entropy/nGram;
        return -entropy;
    }

    public long getTotalNumberOfTokens(){
       return tokenFrequency.values().stream().mapToLong(Long::longValue).sum();
    }

    public List<String> get5FrequentTokens(){
        return tokenFrequency.entrySet()
                .stream()
                .sorted(Map.Entry.<String,Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry:: getKey)
                .collect(Collectors.toList());

    }

    public List<String> get5LessFrequentTokens(){
        return tokenFrequency.entrySet()
                .stream()
                .sorted(Map.Entry.<String,Long>comparingByValue())
                .limit(5)
                .map(Map.Entry:: getKey)
                .collect(Collectors.toList());

    }

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

        return (double) (numerator/(Math.sqrt(sum1)*Math.sqrt(sum2)));

    }

    public Map<String, Long> getTokenFrequency(){
       return tokenFrequency;
    }


}
