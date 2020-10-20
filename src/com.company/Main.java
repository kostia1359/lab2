package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        File file = Paths.get("test.txt").toFile();

        try {
            Automate automate = new Automate(file);

            System.out.println("Enter the length of word:");

            while (sc.hasNextLine()){
                int length = sc.nextInt();

                if (length > 0){
                    automate.setAllWords(length);

                    automate.printResult();
                } else
                    return;

            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
    }
}