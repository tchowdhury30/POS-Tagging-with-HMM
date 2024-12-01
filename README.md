# POS Tagging with Hidden Markov Model (HMM)

## Overview
This project implements a Part of Speech (POS) Tagging algorithm using a Hidden Markov Model (HMM) approach. The system processes a sentence of words and assigns the correct part of speech (POS) tag to each word based on the context provided by neighboring words. This process is done by leveraging the Viterbi algorithm to find the most probable sequence of tags given a sequence of words.

## Key Features
- **Hidden Markov Model (HMM):** A statistical model where the states are POS tags and the observations are words in a sentence.
- **Viterbi Algorithm:** Used to efficiently compute the best tag sequence for a sentence based on transition and observation probabilities.
- **Training:** Uses tagged sentences to build a model of tag transitions and word-to-tag associations.
- **Evaluation:** The system is tested on unseen sentences to calculate accuracy, comparing predicted POS tags against actual tags.
- **User Interface:** A simple console interface allows users to input sentences for POS tagging.

## Data Structures
- **Transition Probabilities:** Represents the likelihood of one tag following another.
- **Observation Probabilities:** Represents the likelihood of a word occurring given a tag.
- **HashMaps:** Utilized for storing transitions, observations, and probabilities for efficient lookups.
- **Backpointer:** Tracks the most likely previous tag for each state during Viterbi decoding to allow backtracking and recovering the optimal sequence.

## Functionality

1. **Training the Model:**
   - The system processes tagged training data to build transition and observation probability models.
   - Transition probabilities represent how likely one POS tag follows another, while observation probabilities represent how likely a word corresponds to a particular tag.
   
2. **Viterbi Decoding:**
   - Given a sequence of words, the system applies the Viterbi algorithm to determine the most likely sequence of POS tags.
   - The algorithm computes the best path using transition and observation probabilities, tracking the score for each possible tag at each word position.

3. **Testing and Evaluation:**
   - The system can process sentences input through the console and output the predicted POS tags.
   - It evaluates performance by comparing predicted tags with actual tags from test data, calculating accuracy based on correct tag predictions.

## Training Data Files
- `simple-train-sentences.txt`: Contains training sentences.
- `simple-train-tags.txt`: Contains corresponding tags for the sentences.

## Test Data Files
- `simple-test-sentences.txt`: Contains test sentences for evaluation.
- `simple-test-tags.txt`: Contains corresponding correct tags for test sentences.

## Example Usage
```bash
# To train the model with training data
POS pos = new POS();
pos.learnData("texts/simple-train-sentences.txt", "texts/simple-train-tags.txt");

# To test POS tagging from the console
pos.testPOSFromConsole();

# To evaluate the model on test data
pos.evaluatePerformance("texts/simple-test-sentences.txt", "texts/simple-test-tags.txt");

MIT License

This project was written in Dartmouth's CS10