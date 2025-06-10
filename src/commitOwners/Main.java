package commitOwners;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("Please enter the path of the file that contains the employee information: ");
        String path = input.nextLine();
        System.out.println("Please enter the weld value:  ");
        String weldValue = input.nextLine();

        CommitOwnersProcessing commitOwnersProcessing = new CommitOwnersProcessing(weldValue);
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                commitOwnersProcessing.createDictionary(line);
            }
            System.out.println("Commiters: ");
            List<String> committers = commitOwnersProcessing.findCommitOwners();
            for (String committer : committers) {
                System.out.println(committer);
            }

            System.out.println("Number of different decompositions: " + commitOwnersProcessing.calculateNumOfDecompositions());

        }catch(IOException e){
            System.out.println("Error processing the file!");
        }

    }
}
