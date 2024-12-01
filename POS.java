import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * @author Tasnim Chowdhury
 */

public class POS {

    /*
     * HMM is defined by its states (here part of speech tags), transitions (tag to tag, with weights), and observations (here tag to word, with weights).
     * pseudocode
    currStates = { start }
    currScores = map { start=0 }

    add more
    for i from 0 to # observations - 1
      nextStates = {}
      nextScores = empty map
      for each currState in currStates
        for each transition currState -> nextState
          add nextState to nextStates
          nextScore = currScores[currState] +                       // path to here
                      transitionScore(currState -> nextState) +     // take a step to there
                      observationScore(observations[i] in nextState) // make the observation there
          if nextState isn't in nextScores or nextScore > nextScores[nextState]
            set nextScores[nextState] to nextScore
            remember that pred of nextState @ i is curr
      currStates = nextStates
      currScores = nextScores
     */

    private HashMap<String, String> currStates = new HashMap<>();
    private HashMap<String, Double> currScores = new HashMap<>();
    private HashMap<String, String> nextStates = new HashMap<>();
    private HashMap<String, Double> nextScores = new HashMap<>();

    private HashMap<String, HashMap<String, Double>> transition = new HashMap<>();
    private HashMap<String, HashMap<String, Double>> observation = new HashMap<>();

    private HashMap<String, Double> observationTotal = new HashMap<>();
    private HashMap<String, Double> transitionTotal = new HashMap<>();

    private ArrayList<HashMap<String, String>> backPointer = new ArrayList<>();

    private static final int  unseen = -1000; // the penalty if we took the wrong path.

    /**--FOR TESTING PURPOSES---*/
    public POS() {
        initializeHMM();
    }

    public void initializeHMM() {
        // Clear existing data
        transition.clear();
        observation.clear();
    }

    public void setTransitionProbabilities(HashMap<String, HashMap<String, Double>> transitions) {
        this.transition = transitions;
    }

    public void setObservationProbabilities(HashMap<String, HashMap<String, Double>> observations) {
        this.observation = observations;
    }
    /**-----------------------*/

    public void addToMap(HashMap<String, HashMap<String, Double>> map, String keyset1, String keyset2) {
        if (map.containsKey(keyset1)) {
            if (map.get(keyset1).containsKey(keyset2)) map.get(keyset1).put(keyset2, map.get(keyset1).get(keyset2) + 1);
            else map.get(keyset1).put(keyset2, 1.0);
        } else {
            HashMap<String, Double> map2 = new HashMap<>();
            map2.put(keyset2, 1.0);
            map.put(keyset1, map2);
        }
    }

    public void voidPrecisionIssues(HashMap<String, HashMap<String, Double>> map,HashMap<String, Double> total) {
        for (String tag : map.keySet())
            for (String tag2: (map.get(tag)).keySet())
                map.get(tag).put(tag2, Math.log(map.get(tag).get(tag2) / total.get(tag)));
    }

    // Method to perform Viterbi decoding to find the best sequence of tags for a line (sequence of words); Draw from each line from both sentences and tags files, and build upon it the similarities between the word of each line in sentence file and match it with each word of each line in the tag file.
    public String viterbiProcessor(String[] words) {
        currStates.clear();
        currScores.clear();
        currStates.put("#", null);
        currScores.put("#", 0.0);

        backPointer.clear(); // Clear the backPointer list

        for (int i = 0; i < words.length; i++) words[i] = words[i].toLowerCase();

        //Debug
        //System.out.println("Initial States: " + currStates);
        //System.out.println("Initial Scores: " + currScores);

        String[] currentPath = new String[words.length]; // the path we used, if needed to go backtracking.


        int currentIndex = 0;
        double score;

        for (String word : words) {

            nextStates.clear();
            nextScores.clear();
            backPointer.add(new HashMap<>());

            //Debug for each word
           // System.out.println("\nProcessing word: " + word);
           // System.out.println("Current States: " + currStates);
           // System.out.println("Current Scores: " + currScores);

            for (String currentState : currStates.keySet()) {

               // System.out.println("Current State: " + currentState); // Debug for current state
                if (transition.containsKey(currentState)) { // the beginning of the graph, that we will walk from.
                    for (String nextState : transition.get(currentState).keySet()) { // our next move, which is node
                        // Debug for transition and observation
                        // System.out.println("Checking transition from " + currentState + " to " + nextState);
                        // System.out.println("Transition probability: " + transition.get(currentState).get(nextState));
                        // System.out.println("Observation probability for " + word + " in " + nextState + ": " + (observation.get(nextState).containsKey(word) ? observation.get(nextState).get(word) : unseen));

                        /** we will be checking if it contains the word that we are looking for.
                         if so, the score would increase, giving more probability that it's more likely to
                         find this word if we went through the next (the one we are trying to find) node.
                         Otherwise, if we entered a path to a node that does not contain that word,
                         it means that the chance to find this word again in this path is so minimal, more like void,
                         so we decrease the score by a variable unseen (U), showcasing that the chance to go this specific
                         path is decreasing, and there are other paths that has better chance and higher score. */

                        nextStates.put(nextState, null); // Add nextState to nextStates

                        // Score calculation
                        double transProb = transition.get(currentState).get(nextState);

                        double obsProb;
                        if (observation.get(nextState).containsKey(word)) { // here if we found the word in the next node
                            obsProb = observation.get(nextState).get(word);
                        } else { //if we didn't find the word that we are looking for in the next node
                            obsProb = unseen;
                        }

                        score = currScores.get(currentState) + transProb + obsProb;

                        // Debug for score comparison
                        // System.out.println("Calculated score for transition to " + nextState + ": " + score);

                        // check if we've gone through the nextNode before from a different node and compare the chance between the current node and the other node that we went through  nextNode from.
                        if (!nextScores.containsKey(nextState) || score > nextScores.get(nextState)) {
                            // System.out.println("Comparing score for state " + nextState + ": current score = " + nextScores.getOrDefault(nextState, Double.NEGATIVE_INFINITY) + ", new score = " + score); //Debug
                            nextScores.put(nextState, score);
                            backPointer.get(currentIndex).put(nextState, currentState);
                            // System.out.println("Back Pointer updated for " + nextState + ": " + currentState); //Debug
                        }
                    }

                }
            }

            // Update current states and scores for the next iteration
            currStates = new HashMap<>(nextStates);
            currScores = new HashMap<>(nextScores);
            currentIndex++;

            //Debug for updated scores and states
            // System.out.println("Updated Current Scores: " + currScores);
            // System.out.println("Updated Next Scores: " + nextScores);
            // System.out.println("Back Pointers: " + backPointer);
        }

        // Backtracking to find the best path
        double temp = -10000000000.0;
        String maxTag = "";
        for (String node : currScores.keySet()) {
            if (currScores.get(node) >= temp) {
                maxTag = node;
                temp = currScores.get(node);
            }
        }

        for (int i = words.length - 1; i >= 0; i--){
            currentPath[i] = maxTag;
            maxTag = backPointer.get(i).get(maxTag); // get the path we came from, and store it in currentPath
        }

        String str = "";
        for (String word: currentPath)
            str += word + " "; // make up a word from the path we followed

        // Debug for final path
        // System.out.println("Final Path: " + String.join(", ", currentPath));

        return str;

    }

    // Method to train a model (observation and transition probabilities) on corresponding lines (sentence and tags) from a pair of training files.
    public void learnData(String trainSentence, String trainTag) throws IOException {
        BufferedReader sentenceToTrain = new BufferedReader(new FileReader(trainSentence));
        BufferedReader tagToTrain = new BufferedReader(new FileReader(trainTag));
        String sentenceLine; String tagLine;
        while((sentenceLine = sentenceToTrain.readLine()) != null && (tagLine = tagToTrain.readLine()) != null) {
            String[] words = sentenceLine.split(" ");
            String[] tags = tagLine.split(" ");

            // Lowercase every word in words, the case doesn't matter if it's upper or lower.
            for (int i = 0; i < words.length; i++) {
                words[i] = words[i].toLowerCase();
            }

            int i = 0;
            while(i < words.length && i < tags.length) {
                addToMap(observation, tags[i], words[i]);
                // set the center of the transition graph from the beginning of the line.
                if(i == 0) addToMap(transition, "#", tags[i]);
                else addToMap(transition, tags[i-1], tags[i]);
                i++;
            }
        }
        double observationTotalLine; double transitionTotalLine;
        for (String tag : transition.keySet()) {
            transitionTotalLine = 0;
            for (String tag2 : transition.get(tag).keySet()) {
                transitionTotalLine += transition.get(tag).get(tag2);
            }
            transitionTotal.put(tag, transitionTotalLine);
        }

        for (String tag : observation.keySet()) {
            observationTotalLine = 0;
            for (String word : (observation.get(tag)).keySet())
                observationTotalLine += observation.get(tag).get(word);
            observationTotal.put(tag, observationTotalLine);
        }
        voidPrecisionIssues(transition, transitionTotal);
        voidPrecisionIssues(observation, observationTotal);
    }

    // Method to test POS tagging from console input
    public void testPOSFromConsole() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a sentence to tag (type 'exit' to quit):");

        while (true) {
            String inputLine = scanner.nextLine();

            // Check for exit condition
            if (inputLine.equalsIgnoreCase("exit")) {
                break;
            }

            // Process the input line
            String[] words = inputLine.split("\\s+");
            String tags = viterbiProcessor(words);

            // Display the output
            System.out.println("Tags: " + tags);
            System.out.println("\nEnter another sentence to tag (type 'exit' to quit):");
        }

        scanner.close();
    }

    // Method to evaluate POS tagging performance on test files
    public void evaluatePerformance(String testSentenceFile, String testTagFile) throws IOException {
        // Create BufferedReader objects to read from the test sentence and tag files
        BufferedReader testSentenceReader = new BufferedReader(new FileReader(testSentenceFile));
        BufferedReader testTagReader = new BufferedReader(new FileReader(testTagFile));

        // Variables to keep track of the total number of tags and the number of correctly predicted tags
        String sentenceLine, tagLine;
        int totalTags = 0;
        int correctTags = 0;

        // Read each line from both the sentence and tag files
        while ((sentenceLine = testSentenceReader.readLine()) != null && (tagLine = testTagReader.readLine()) != null) {
            // Split the sentence into words and the tag line into individual tags
            String[] words = sentenceLine.split("\\s+");
            String[] actualTags = tagLine.split("\\s+");

            // Use the Viterbi algorithm to predict tags for the current line of words
            String predictedTagLine = viterbiProcessor(words);
            String[] predictedTags = predictedTagLine.trim().split("\\s+");

            // Ensure that the lengths of actual and predicted tags are the same
            if (actualTags.length != predictedTags.length) {
                System.out.println("Mismatch in number of tags for sentence: " + sentenceLine);
                continue;
            }

            // Compare the predicted tags with the actual tags
            for (int i = 0; i < actualTags.length; i++) {
                // If the predicted tag matches the actual tag, increment the correctTags counter
                if (actualTags[i].equals(predictedTags[i])) {
                    correctTags++;
                }
                // Increment the totalTags counter for each tag processed
                totalTags++;
            }
        }

        // Close the file readers
        testSentenceReader.close();
        testTagReader.close();

        // Calculate and print the performance metrics
        double accuracy = totalTags > 0 ? (double) correctTags / totalTags : 0;
        System.out.println("Accuracy: " + accuracy);
        System.out.println("Right: " + correctTags + " Wrong: " + (totalTags - correctTags));
    }

    public static void main(String[] args) throws IOException {

        String trainS = "texts/simple-train-sentences.txt";
        String trainT = "texts/simple-train-tags.txt";

        String testS = "texts/simple-test-sentences.txt";
        String testT = "texts/simple-test-tags.txt";


        POS pos = new POS();
        pos.learnData(trainS,trainT);
        pos.testPOSFromConsole();
        //pos.evaluatePerformance(testS, testT);

    }



}

