package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Automate {
    private int wordLength;
    private final int alphabetLen;
    private final int stateQty;
    private final int startState;
    private final int finalStateQty;
    private final List<Integer> finalStates = new LinkedList<>();
    private ArrayList<Character>[][] transitionFunction;
    private int[][] neighboursMatrix;
    private ArrayList<ArrayList<Integer>> wordPaths = new ArrayList<>();
    private HashSet<String> words = new HashSet<>();
    private HashSet<Character> allLetters = new HashSet<>();
    private ArrayList<String> allProbWord = new ArrayList<>();

    Automate(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        String finalStateLine = scanner.nextLine();
        var finalStateString = finalStateLine.split(" ");

        alphabetLen = Integer.parseInt(scanner.nextLine());
        stateQty = Integer.parseInt(scanner.nextLine());
        startState = Integer.parseInt(scanner.nextLine());
        finalStateQty = Integer.parseInt(finalStateString[0]);

        for (int i = 1; i <= finalStateQty; i++) {
            finalStates.add(Integer.parseInt(finalStateString[i]));
        }

        initTransitionFunction();
        initNeighbours();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            var statesAndLetters = line.split(" ");
            int state1 = Integer.parseInt(statesAndLetters[0]);
            int state2 = Integer.parseInt(statesAndLetters[2]);
            var letter = statesAndLetters[1].charAt(0);

            neighboursMatrix[state1][state2] += 1;

            transitionFunction[state1][state2].add(letter);
            
            allLetters.add(letter);
        }
    }

    private void initTransitionFunction() {
        transitionFunction = new ArrayList[stateQty][stateQty];

        for (int i = 0; i < stateQty; i++) {
            for (int j = 0; j < stateQty; j++) {
                transitionFunction[i][j] = new ArrayList<>();
            }

        }
    }

    private void initNeighbours() {
        neighboursMatrix = new int[stateQty][stateQty];

        for (int i = 0; i < stateQty; i++) {
            for (int j = 0; j < stateQty; j++) {
                neighboursMatrix[i][j] = 0;
            }
        }
    }


    private void findPathInMachine(Integer startState, ArrayList<Integer> currentWord) {

        if (finalStates.contains(startState)) {
            ArrayList<Integer> word = new ArrayList<>(currentWord);
            wordPaths.add(word);
        } else {
            for (int i = 0; i < stateQty; i++) {
                if (neighboursMatrix[startState][i] > 0) {
                    currentWord.add(i);

                    neighboursMatrix[startState][i] -= 1;
                    findPathInMachine(i, currentWord);
                    neighboursMatrix[startState][i] += 1;

                    currentWord.remove(currentWord.lastIndexOf(i));
                }

            }
        }

    }

    private ArrayList<ArrayList<Character>> convertPathToCharsArray(ArrayList<Integer> wordInt) {
        Integer currentState = wordInt.get(0);
        ArrayList<ArrayList<Character>> charsArray = new ArrayList<>();

        for (int i = 1; i < wordInt.size(); i++) {
            Integer nextState = wordInt.get(i);
            ArrayList<Character> chars = new ArrayList<>(transitionFunction[currentState][nextState]);

            currentState = nextState;
            charsArray.add(chars);
        }
        return charsArray;
    }


    private ArrayList<Integer> convertPathToIntegerArray(ArrayList<Integer> wordInt) {
        ArrayList<Integer> path = new ArrayList<>();
        int currentState = wordInt.get(0);

        for (int i = 1; i < wordInt.size(); i++) {
            int nextState = wordInt.get(i);

            path.add(currentState * stateQty + nextState);
            currentState = nextState;
        }
        return path;
    }

    private void makeWordsFromCharsArray(ArrayList<ArrayList<Character>> charsArray, int start,
                                         ArrayList<Character> current, ArrayList<Integer> path) {
        if (start == charsArray.size()) {
            if (checkWord(path, current)) {
                StringBuilder builder = new StringBuilder();
                ArrayList<Character> word = new ArrayList<>(current);

                for (Character ch : word) {
                    builder.append(ch);
                }

                words.add(builder.toString());
            }

        } else {
            for (int j = 0; j < charsArray.get(start).size(); j++) {
                current.add(charsArray.get(start).get(j));

                makeWordsFromCharsArray(charsArray, start + 1, current, path);

                current.remove(current.size() - 1);
            }
        }
    }


    public void setAllWords(int wordLen) {
        ArrayList<Integer> word = new ArrayList<>();
        word.add(startState);

        this.wordLength = wordLen;
        allProbWord.clear();

        findPathInMachine(startState, word);

        for (ArrayList<Integer> integers : wordPaths) {
            ArrayList<Character> current = new ArrayList<>();

            makeWordsFromCharsArray(convertPathToCharsArray(integers), 0, current,
                    convertPathToIntegerArray(integers));
        }
        StringBuilder builder = new StringBuilder();
        allProbabilisticWords(builder);
    }

    public void printAutomateWords() {
        for (String word : words) {
            if (word.length() == wordLength) {
                System.out.print(word);
                System.out.print(" ");
            }
        }

        System.out.println();
    }

    private boolean checkWord(ArrayList<Integer> integerWord, ArrayList<Character> word) {
        HashMap<Integer, ArrayList<Character>> temp = new HashMap<>();

        for (int i = 0; i < integerWord.size(); i++) {
            if (temp.containsKey(integerWord.get(i))) {
                if (temp.get(integerWord.get((i))).contains(word.get(i))) {
                    return false;
                } else {
                    temp.get(integerWord.get(i)).add(word.get(i));
                }
            } else {
                ArrayList<Character> letters = new ArrayList<>();
                letters.add(word.get(i));

                temp.put(integerWord.get(i), letters);
            }
        }
        return true;
    }

    private void allProbabilisticWords(StringBuilder builder) {
        if (builder.length() == wordLength) {
            allProbWord.add(builder.toString());
        } else {
            for (Character ch : allLetters) {
                builder.append(ch);

                allProbabilisticWords(builder);

                builder.deleteCharAt(builder.length() - 1);
            }
        }
    }

    public void printResult()
    {
        System.out.print("All possible words: ");
        printAllPossibleWords();
        System.out.print("All automate words: ");
        printAutomateWords();

        for (String word: allProbWord) {
            if (!words.contains(word)) {
                System.out.println(false);
                return;
            }
        }

        System.out.println(true);
    }

    public void printAllPossibleWords() {
        for (String word : allProbWord) {
            System.out.print(word);
            System.out.print(" ");
        }

        System.out.println();
    }
}
