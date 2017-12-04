import edu.princeton.cs.algs4.*;

/**
 *  The Outcast class provides a method to identify an outcast in a set of wordnet nouns.
 *  An outcast is a noun for which the sum of the distances from the other nouns is maximum.
 *  @author Fernando Franco
 */

public class Outcast {
    
    private WordNet wordnet;
    
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    /**
     * Returns theoutcast given a set of nouns.
     *
     * @param Set of nouns.
     * @return outcast.
     */
    public String outcast(String[] nouns) {
        
        String outcast = null;
        int max = 0;
        
        for (String noun : nouns) {
            int distance = 0;
            for (String others : nouns) {
                if (!noun.equals(others)) {
                    distance += wordnet.distance(noun, others);
                }
            }
            
            if(distance > max) {
                max = distance;
                outcast = noun;
            }
        }
        
        return outcast;
    }

    //Test method
    public static void main(String[] args) {
	WordNet wordnet = new WordNet("synsets.txt", "hypernyms.txt");
	Outcast outcast = new Outcast(wordnet);
	for (int t = 0; t < args.length; t++) {
	    In in = new In(args[t]);
	    String[] nouns = in.readAllStrings();
	    StdOut.println(args[t] + ": " + outcast.outcast(nouns));
	}
    }
}
