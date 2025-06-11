/*
 * Copyright (c) 10/6/2025 . Author @Vikensa Grabocka
 */


package commitOwners;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CommitOwnersProcessing {
    private String weld;
    private Map<Long,String> employees = new HashMap<Long,String>();

    public CommitOwnersProcessing(String weld) {
        this.weld = weld;
    }


    /**
     * Method that is used to construct the map that creates
     * the corresponding pairs employee id - employee full name
     * @param line
     */
    public void createDictionary(String line){
        String[] parts = line.split(",");
        long id = Long.parseLong(parts[0]);
        String surname = parts[1];
        String name = parts[2];
        employees.put(id,name + " " + surname);
    }


    /**
     * Method used to find the configuration with the most commits
     * from the given weld value
     * @return a list that contains the names of the employees
     */

    public List<String> findCommitOwners(){
       List<String> commitOwners = new LinkedList<>();
        char[] weldChars = weld.toCharArray();
        long[] weldDigits = new long[weldChars.length];
        for (int i = 0; i < weldDigits.length; i++) {
            weldDigits[i] = weldChars[i] - '0';
        }

        int endIndex = 0;
        StringBuilder idCand = new StringBuilder();
        while (endIndex < weldDigits.length) {
            idCand.append(weldDigits[endIndex]);
            long id = Long.parseLong(idCand.toString());

            if(employees.containsKey(id)){
                commitOwners.add(employees.get(id));
                idCand.setLength(0);
            }
            endIndex++;
        }

    return commitOwners;
    }


    /**
     * Method used to calculate the number of different configurations
     * the weld can be interpreted
     * @return
     */
    public long calculateNumOfDecompositions(){
        long[] numDecompositions = new long[weld.length()+1];
        numDecompositions[0] = 1;
        for(int i = 1; i < numDecompositions.length; i++){
            for(long validId : employees.keySet()){
                int lengthOfId = String.valueOf(validId).length();

                if(i >= lengthOfId && weld.substring(i-lengthOfId,i).equals(String.valueOf(validId))){
                    numDecompositions[i] += numDecompositions[i-lengthOfId];
                }
            }
        }
        return numDecompositions[numDecompositions.length-1];
    }


}

