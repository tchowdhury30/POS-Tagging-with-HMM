import java.util.HashMap;

public class Tests {

    public static void test0() {
        POS pos = new POS();

        // Set up transition probabilities
        HashMap<String, HashMap<String, Double>> transitions = new HashMap<>();

        HashMap<String, Double> startTransitions = new HashMap<>();
        startTransitions.put("N", 0.6);
        startTransitions.put("V", 0.4);
        transitions.put("#", startTransitions);

        HashMap<String, Double> nounTransitions = new HashMap<>();
        nounTransitions.put("V", 0.3);
        nounTransitions.put("Adj", 0.7);
        transitions.put("N", nounTransitions);

        HashMap<String, Double> verbTransitions = new HashMap<>();
        verbTransitions.put("N", 0.8);
        verbTransitions.put("Adj", 0.2);
        transitions.put("V", verbTransitions);

        // Set up observation probabilities
        HashMap<String, HashMap<String, Double>> observations = new HashMap<>();

        HashMap<String, Double> nounObservations = new HashMap<>();
        nounObservations.put("dog", 0.5);
        nounObservations.put("bark", 0.1);
        observations.put("N", nounObservations);

        HashMap<String, Double> verbObservations = new HashMap<>();
        verbObservations.put("bark", 0.6);
        verbObservations.put("loud", 0.2);
        observations.put("V", verbObservations);

        HashMap<String, Double> adjObservations = new HashMap<>();
        adjObservations.put("loud", 0.7);
        adjObservations.put("dog", 0.1);
        observations.put("Adj", adjObservations);


        // Set the probabilities in the POS object
        pos.setTransitionProbabilities(transitions);
        pos.setObservationProbabilities(observations);

        //Debug
        System.out.println("Transition Probabilities: " + transitions);
        System.out.println("Observation Probabilities: " + observations);

        // Test the Viterbi algorithm
        String[] sampleInput = {"bark", "loud"};
        String result = pos.viterbiProcessor(sampleInput);

        // Print the result
        System.out.println("Result: " + result);


    }

    public static void test1() {
        POS pos = new POS();

        // Set up transition probabilities
        HashMap<String, HashMap<String, Double>> transitions = new HashMap<>();

        HashMap<String, Double> startTransitions = new HashMap<>();
        startTransitions.put("N", 0.6);
        startTransitions.put("V", 0.4);
        transitions.put("#", startTransitions);

        HashMap<String, Double> nounTransitions = new HashMap<>();
        nounTransitions.put("V", 0.3);
        nounTransitions.put("Adj", 0.7);
        transitions.put("N", nounTransitions);

        HashMap<String, Double> verbTransitions = new HashMap<>();
        verbTransitions.put("N", 0.8);
        verbTransitions.put("Adj", 0.2);
        transitions.put("V", verbTransitions);

        // Set up observation probabilities
        HashMap<String, HashMap<String, Double>> observations = new HashMap<>();

        HashMap<String, Double> nounObservations = new HashMap<>();
        nounObservations.put("dog", 0.5);
        nounObservations.put("bark", 0.1);
        observations.put("N", nounObservations);

        HashMap<String, Double> verbObservations = new HashMap<>();
        verbObservations.put("bark", 0.6);
        verbObservations.put("loud", 0.2);
        observations.put("V", verbObservations);

        HashMap<String, Double> adjObservations = new HashMap<>();
        adjObservations.put("loud", 0.7);
        adjObservations.put("dog", 0.1);
        observations.put("Adj", adjObservations);

        // Set the probabilities in the POS object
        pos.setTransitionProbabilities(transitions);
        pos.setObservationProbabilities(observations);

        // Test the Viterbi algorithm
        String[] sampleInput = {"dog", "bark"};
        String result = pos.viterbiProcessor(sampleInput);

        // Print the result
        System.out.println("Test 1 Result: " + result);
    }


    public static void main(String[] args) {
        test0();
    }

}

