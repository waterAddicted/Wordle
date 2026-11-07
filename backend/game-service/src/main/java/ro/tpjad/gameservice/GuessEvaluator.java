package ro.tpjad.gameservice;

// two-pass compare, handles duplicate letters correctly
public class GuessEvaluator {

    public enum LetterResult {
        CORRECT, MISPLACED, WRONG
    }

    public static LetterResult[] evaluate(String word, String guess) {
        int length = word.length();
        LetterResult[] result = new LetterResult[length];
        boolean[] usedInWord = new boolean[length];

        // pass 1: exact matches
        for (int i = 0; i < length; i++) {
            if (guess.charAt(i) == word.charAt(i)) {
                result[i] = LetterResult.CORRECT;
                usedInWord[i] = true;
            }
        }

        // pass 2: right letter, wrong spot
        for (int i = 0; i < length; i++) {
            if (result[i] != null) {
                continue;
            }
            char letter = guess.charAt(i);
            boolean found = false;
            for (int j = 0; j < length; j++) {
                if (!usedInWord[j] && word.charAt(j) == letter) {
                    usedInWord[j] = true;
                    found = true;
                    break;
                }
            }
            result[i] = found ? LetterResult.MISPLACED : LetterResult.WRONG;
        }

        return result;
    }
}
