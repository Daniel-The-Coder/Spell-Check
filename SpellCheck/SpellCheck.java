import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

public class SpellCheck {
	
	//word - similarity factor class
	//similarity factor of correct word = (number of letters coinciding with misspelt word)/(length of word)
	public class WordSimilarity{
		String Word;
		float Similarity;
	}

	//function to create a list of wordLength objects and count all words for length of WordLength array
	Map readWordFile()throws FileNotFoundException {
		Map<Integer, List> WordsMap = new HashMap<Integer, List>();
		Scanner WordsFile = new Scanner(new File("words.txt"));
		//to create an array of WordLength type
		SpellCheck spellCheck = new SpellCheck();
		while (WordsFile.hasNext()) {
		    String word = WordsFile.nextLine();
			//to create a WordLength object
			int Length = word.length();
			if (WordsMap.containsKey(Length)){
				List wordsList = WordsMap.get(Length);
				wordsList.add(word);
				WordsMap.put(Length,  wordsList);
			}
			else{
				List<String> wordsList = new ArrayList<String>();
				wordsList.add(word);
				WordsMap.put(Length,  wordsList);
			}
		}
		WordsFile.close();
		return WordsMap;
	}
	
		
	// function to compare 2 words and return similarity factor (no of coinciding letters/length)
	float SimilarityFactor(String word1, String word2) throws FileNotFoundException{
		float L = (float)word1.length(); //it is equal to word2.length();
		float SimChar = (float)0;
		int i;
		for (i=0; i<L; i++){
			if (word1.substring(i,i+1).equals(word2.substring(i,i+1))){
				//didn't work with ==
				SimChar++;
			}
		}
		float SimFact = SimChar/L;
		return SimFact;
	}
		
		
	//function to return best match for the input word
	String WordMatch(String word) throws FileNotFoundException{
		Map<Integer, List> WordsMap=readWordFile();
		int Len = word.length();
		List wordsList = WordsMap.get(Len);//to get list of words of same length as input word
		String wordLowerCase = "";
		List<Boolean> wordCaseList = new ArrayList<Boolean>();
		for (int i=0; i<word.length(); i++){
			//to convert word to lower case for comparison
			wordLowerCase += word.substring(i, i+1).toLowerCase();
			//to create a boolean list to store case(upper/lower)
			if (word.substring(i, i+1).equals(word.substring(i, i+1).toUpperCase())){
				wordCaseList.add(true);
			}
			else{
				wordCaseList.add(false);
			}
		}
		boolean flag = false;
		for (int j=0; j<wordsList.size(); j++){
			if (wordsList.get(j) == wordLowerCase){
				flag = true;
				return word;
			}
		}
				
		// if word isn't in the list, i.e. if it's wrong
		if(flag == true){
			return word;
		}
		else{
			//list of words with same length as input word
			int count2 = wordsList.size();
			List<WordSimilarity> WordSimilarityList = new ArrayList<WordSimilarity>();
			for (int k=0; k<count2; k++){
				String WORD = (String) wordsList.get(k);
				float simFactor = SimilarityFactor(WORD, wordLowerCase); // DEFINE SimilarityFactor
				WordSimilarity SimObj = new WordSimilarity(); 
				SimObj.Word = WORD;
				SimObj.Similarity = simFactor;
				WordSimilarityList.add(SimObj);
					}
			
			// to find most similar word
			WordSimilarity MostSimilarWordObj = WordSimilarityList.get(0);
			for (int m=0; m<count2; m++){
				if (WordSimilarityList.get(m).Similarity > MostSimilarWordObj.Similarity){
					MostSimilarWordObj = WordSimilarityList.get(m);
				}
			}
			String newWordLower = MostSimilarWordObj.Word;
			String newWord = "";
			for (int i=0; i<newWordLower.length(); i++){
				//if upper case
				if (wordCaseList.get(i)){
					//to convert letter to upper case and add
					newWord += newWordLower.substring(i,i+1).toUpperCase();
				}
				else{
					//to add letter in lower case
					newWord += newWordLower.substring(i,i+1);
				}
			}
					
			return newWord;
		}
	}
	
	//function to take fileName as input, correct spellings and save it with new name in same directory
	void correctFile() throws FileNotFoundException {
		//to prompt for input
		System.out.print("Enter a file name: ");
	
		Scanner fileNameScanner = new Scanner(System.in);
		String fileName = fileNameScanner.nextLine();
		fileNameScanner.close();
		Scanner inputFile = new Scanner(new File(fileName));//to open file
		
		String newFileName = "C:\\Users\\Lord Daniel\\Desktop\\edited_".concat(fileName);
		PrintStream newFile = new PrintStream(newFileName);
		while (inputFile.hasNext()) {
		    String line = inputFile.nextLine();
		    String strippedLine = line.trim();//to remove leading and trailing white spaces
		    if (strippedLine.equals(""))  {
		    	//if empty line
		    	newFile.println(line);
		    }
		    else {
			    line += "$";//to get last word too, $ removed later
			    int Len = line.length();
			    String word = "";
			    String otherChars = "";
			    String newLine = "";
			    //to convert line into list of characters
			    char[] chars = line.toCharArray();
			    for (char c : chars) {
			        if(Character.isLetter(c)) {
			        	//to convert c from char to String d
			        	otherChars="";
			        	String d = Character.toString(c);
			        	word = word+d;
			    	}
			        else {
			        	if (otherChars.equals("")){
			        		//if we are at the first char after the word
			        		//to add correct version of word to newLine
			        		String correctWord = WordMatch(word);
			        		newLine+=correctWord;
			        		System.out.println(newLine);
			        		String d = Character.toString(c);//here c is not an alphabet
			        		newLine+=d;
			        		otherChars+=d; //to avoid error is 2 non letter characters in a row
			        		word = ""; //change word back to an empty string
			        	}
			        	
			        	else{
			        		String d = Character.toString(c);//here c is not an alphabet
			        		newLine+=d;
			        	
			        	}
			        }
			    }
			    System.out.println(newLine.substring(newLine.length()-1));
			    if (newLine.substring(newLine.length()-1).equals("$")){
			    	//== didn't work in the statement above
			    	newFile.println(newLine.substring(0, newLine.length()-1));
			    }
			    else {
			    	newFile.println(newLine); //to write new corrected line on newFile
			    	System.out.println(newLine);
			    }
			}
		}
			inputFile.close();
			newFile.close();
	}
	
		public static void main(String[] args) throws FileNotFoundException {
			//to create an object of class SpellCheck
			SpellCheck spellcheck  = new SpellCheck();
			spellcheck.correctFile(); //spellcheck. to access non static method in main
		}
	}

					
