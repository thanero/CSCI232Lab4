import edu.princeton.cs.algs4.TrieSET;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Created by Marie Morin, Thane O'Brien, and Brandon Klise.
 * This project is the Spell Checker Problem. We read in multiple files that the user inputs
 * and then spell check each word in each file and save the updated files once done.
 */

public class SpellChecker {  
    //creates an arraylist for all of the words in a file
    ArrayList<String> words = new ArrayList<>();
    //creates a Trie tree that will be used for saving the dictionary
    TrieSET dictionary = new TrieSET();
    
    public static void main(String[] args) {
       SpellChecker checker = new SpellChecker();
       //reads in the dictionary
       checker.readDictionary(args[0]);
       //reads in the files passed into args and checks them against the dictionary
       checker.readFiles(args);
    }
    
    //reads in a dictionary from the first text file passed in and saves the dictionary to the TRIE
    public void readDictionary(String dictionaryFile) {
        try {
            FileReader fr = new FileReader(dictionaryFile);
            BufferedReader br = new BufferedReader(fr);
            String sCurrentLine;
            int i = 0;
            
            //adds the words in the file to the dictionary TRIE
            while ((sCurrentLine = br.readLine()) != null) {
                dictionary.add(sCurrentLine);
            }
            
            br.close();
            fr.close();
        } //print any errors that have been caught
        catch (Exception ex) {
            System.err.println(ex);
        }
    }
    
    //reads in the user-inputted files one by one and runs the spell checker on each file
    public void readFiles(String[] args) {
        if(args.length > 0) {
            FileInputStream is = null;
            try {
            for(int i = 1; i < args.length; i++) {
                System.out.println("\t------ Current file: " + args[i] + " ------");
                is = new FileInputStream(new File(args[i]));
                Scanner sc = new Scanner(is);
                //add the words in the current file to the words list
                while (sc.hasNext())
                {
                    words.add(sc.next());
                }
                //call spell check on the generated word list
                spellCheck(words, args[i]);
                words = new ArrayList<>();
            } //print any errors that have been caught
            } catch (Exception ex) {
                System.err.println(ex);
            }
        } 
    }
    
    //spell checks all the words in the wordlist passed in
    public void spellCheck(ArrayList<String> wordlist, String filename) {
        ArrayList<String> correctlist = new ArrayList<>();
        String temp = null;
        
        //iterate through the words in the file
        for(int i = 0; i < wordlist.size(); i++) {
            //casts the word to lowercase
            String word = words.get(i).toLowerCase();
            //removes all apostrophes and periods from the word
            word = word.replaceAll("'", "");
            if(word.contains("."))
                word = word.substring(0, word.length()-1);
            //if the word is misspelled, then find replacements for the word
            if(!dictionary.contains(word)) {
                System.out.println(word + ": did you mean:");
                /*find all the possible matches of the word and saves them to the correctlist*/
                
                //finds all possibilities with one mismatch
                for(int j = 0; j < word.length(); j++) {
                    temp = word.substring(0, j) + "." + word.substring(j+1);
                    for(Object s: dictionary.keysThatMatch(temp))
                    {
                        if(!correctlist.contains((String) s))
                        correctlist.add((String) s);
                    }
                }
                //finds all possibilities with removing each letter independently
                for(int j = 0; j < word.length(); j++) {
                    temp = word.substring(0, j) + word.substring(j+1);
                    for(Object s: dictionary.keysThatMatch(temp))
                    {
                        if(!correctlist.contains((String) s))
                        correctlist.add((String) s);
                    }
                }
                //finds all possibilities with one more character
                for(int j = 0; j < word.length()+1; j++) {
                    temp = word.substring(0, j) + "." + word.substring(j);
                        for(Object s: dictionary.keysThatMatch(temp))
                        {
                            if(!correctlist.contains((String) s))
                            correctlist.add((String) s);
                        }
                }
                /*
                //finds all possibilities with one less character
                for(int j = 0; j < word.length()-1; j++) {
                    temp = word.substring(0,j) + word.substring(j+1);
                    for(int k = 0; k<temp.length();k++) {
                        temp = word.substring(0, k) + "." + word.substring(k+2);
                        for(Object s: dictionary.keysThatMatch(temp))
                        {
                            if(!correctlist.contains((String) s))
                            correctlist.add((String) s);
                        }
                    }
                }
                */
                
            //prompt user to choose the correct word
            chooseWord(correctlist, i);
            //resets the correctlist for the next word
            correctlist = new ArrayList<>();
            }
        }
        
        //once the corrections are done, save them to a corrected version of the file
        try (PrintWriter writer = new PrintWriter(filename.substring(0,filename.length()-4) + "-checked.txt" , "UTF-8")) {
            while(!words.isEmpty()) {
                writer.write(words.get(0) + " ");
                words.remove(0);
                if(words.size() == 1) {
                    writer.write(words.get(0));
                    words.remove(0);
                }
            }
        } //print any errors that have been caught
        catch (Exception ex) {
            System.err.println(ex);
        }  
    }
    
    //finds all possibilites for the incorrect word and prompts user to change it
    public void chooseWord(ArrayList<String> correctlist, int curIndex) {
        //prints out the correct list
        for(int k = 0; k < correctlist.size(); k++)
            System.out.println(k + ". " + correctlist.get(k));
        System.out.println("-1. something else");
        //ask the user for their choice
        System.out.println("Enter your choice and hit the enter button when done:");
        Scanner scan = new Scanner(System.in);
        int choice = scan.nextInt();
        scan.nextLine();
        String word = null;
        //if the word was not found ask the user for the correct word
        if(choice == -1) {
            System.out.println("Enter the word below, hit the enter button when done:");
            word = scan.nextLine();
        }
        //otherwise grab the correct word that the user selected
        else
            word = correctlist.get(choice);
        //appends a new line once the choice has been made
        System.out.println();
        //replaces the new word with the old one in the list
        words.set(curIndex, word);
    }
    
    
}