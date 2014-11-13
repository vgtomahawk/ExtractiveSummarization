import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.process.*;
public class POSTagger {
    public HashMap<String, String> pennToInternal;
    public MaxentTagger tagger;

    public POSTagger() {
        tagger = new MaxentTagger("pos-model/english-bidirectional-distsim.tagger");
        loadMappings();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        POSTagger posTagger = new POSTagger();
        MaxentTagger tagger = new MaxentTagger("pos-model/english-bidirectional-distsim.tagger");
        posTagger.loadMappings();
        String example = "It is Max's girl";
        long startTime = System.currentTimeMillis();
        String[] taggedString = posTagger.tagSentence(example);
        long endTime = System.currentTimeMillis();
        System.out.println(taggedString);
        System.out.println(endTime - startTime);
        DocumentPreprocessor dp=new DocumentPreprocessor("document.txt");
        Stemmer stemmer=new Stemmer();
        for(List sentence: dp)
        {
        	System.out.println(sentence);
        	List stemmedSentence=new ArrayList<String>();
        	for(Object word:sentence)
        	{
                stemmer.add(word.toString().toCharArray(),word.toString().length());
                stemmer.stem();
                stemmedSentence.add(stemmer.toString());
        	}
        	System.out.println(stemmedSentence);
        }
    	//String INPUT_LINE_GRAPH = "output/lineGraph.txt";
        String INPUT_LINE_GRAPH = "output/sampleGraph.txt";
    	String MODULARITY_FILE = "modularityMatrix/matrix.txt";
    	
    	// MCL
    	String INFLATION = "1.25";
    	String MCL_FILE = "MCL/mcl.txt";
    	List<String> command = new ArrayList<String>();
    	/*
    	command.add("sh");
        command.add("myscript.sh");
        command.add(INPUT_LINE_GRAPH);
        command.add(INFLATION);
        command.add(MCL_FILE);
        */
    	command.add("mcl");
    	command.add(INPUT_LINE_GRAPH);
    	command.add("-I");
    	command.add(INFLATION);
    	command.add("--abc");
    	command.add("-o");
    	command.add(MCL_FILE);
        SystemCommandExecutor commandExecutor = new SystemCommandExecutor(command);
        int result = commandExecutor.executeCommand();        
    	
        /*
        s.add('s');
        s.add('i');
        s.add('n');
        s.add('g');
        s.add('i');
        s.add('n');
        s.add('g');
        */
        /*
        String sampleWord=new String("singing");
        s.add(sampleWord.toCharArray(),sampleWord.length());
        s.stem();
        System.out.println(s.toString());
        sampleWord=new String("pushing");
        s.add(sampleWord.toCharArray(),sampleWord.length());
        s.stem();
        System.out.println(s.toString());*/
    }

    public void loadMappings() {
        String filename = "pos-model/penn_to_internal.txt";
        pennToInternal = new HashMap<String, String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                pennToInternal.put(parts[0], parts[1]);
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Not able to read files");
        }
    }

    public String[] tagSentence(String sentence) {
        String pennSentence = tagger.tagString(sentence);
        List<String> internalSentence = new ArrayList<String>();
        String[] words = pennSentence.split(" ");
        for (String word : words) {
            String[] tag = word.split("_");
            internalSentence.add(tag[0] + "_" + pennToInternal.get(tag[1]));
        }
        return internalSentence.toArray(new String[internalSentence.size()]);
    }
}
