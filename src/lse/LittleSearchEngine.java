package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		/** COMPLETE THIS METHOD **/
		if(docFile == null) {
			throw new FileNotFoundException("File not found");
		}
		HashMap<String, Occurrence> hash = new HashMap<String, Occurrence>();
		Scanner f = new Scanner(new File(docFile));
		while(f.hasNext()) {
			String word = f.next();
			String keyword = getKeyword(word);
			if(keyword == null) {
				continue;
			}
			else if(hash.containsKey(keyword)) {
				hash.get(keyword).frequency++;
			}else {
				hash.put(keyword, new Occurrence(docFile, 1));
			}
		}
		f.close();
		return hash;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		/** COMPLETE THIS METHOD **/
		
		for(Map.Entry<String, Occurrence> entry : kws.entrySet()) {
			if(keywordsIndex.containsKey(entry.getKey())) {
				keywordsIndex.get(entry.getKey()).add(entry.getValue());
				insertLastOccurrence(keywordsIndex.get(entry.getKey()));
			}else {
				ArrayList<Occurrence> v = new ArrayList<Occurrence>();
				v.add(entry.getValue());
				keywordsIndex.put(entry.getKey(), v);
			}
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		/** COMPLETE THIS METHOD **/
		if(word == null || word.equals("")) {
			return null;
		}
		String ignoreCase = word.toLowerCase();
		int i = 0;
		int end = ignoreCase.length();
		for(; i < ignoreCase.length(); i++) {
			char c = ignoreCase.charAt(i);
			if(!(Character.isLetter(c))) {
				if(Character.isDigit(c)) {
					return null;
				}
				for(int j = i; j < ignoreCase.length(); j++) {
					char k = ignoreCase.charAt(j);
					if(Character.isLetter(k)) {
						return null;
					}else if(!(k == ',' || k == '.' || k == '?' || k == ':' || k == ';' || k == '!')) {
						return null;
					}
				}
				end = i;
				break;
			}
		}
		String toBeRet = ignoreCase.substring(0, end);
		if(noiseWords.contains(toBeRet) || toBeRet.equals("")) {
			return null;
		}
		return toBeRet;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		/** COMPLETE THIS METHOD **/
		if(occs.size() <= 1) {
			return null;
		}
		
		Occurrence temp = occs.remove(occs.size()-1);
		int f = temp.frequency;
		int min = 0;
		int max = occs.size() - 1;
		ArrayList<Integer> ind = new ArrayList<Integer>();
		int mid = -1;
		while(min <= max) {
			mid = (min+max)/2;
			Occurrence compare = occs.get(mid);
			int compF = compare.frequency;
			ind.add(mid);
			if(min == max) {
				break;
			}
			if(f == compF) {
				occs.add(occs.indexOf(compare), temp);
				return ind;
			}
			else if(f > compF) {
				max = mid - 1;
			}
			else {
				min = mid + 1;
			}
			
		}
		int fr = occs.get(min).frequency;
		if(f >= fr) {
			occs.add(min, temp);
		}else {
			occs.add(min+1, temp);
		}
		return ind;
		
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		/** COMPLETE THIS METHOD **/
		if((!(keywordsIndex.containsKey(kw1))) && (!(keywordsIndex.containsKey(kw2)))) {
			return null;
		}
		if(!keywordsIndex.containsKey(kw1)) {
			ArrayList<Occurrence> k = keywordsIndex.get(kw2);
			ArrayList<String> ret = new ArrayList<String>();
			ArrayList<String> docs = new ArrayList<String>();
			for(int i = 0; i < k.size(); i++) {
				Occurrence o = k.get(i);
				docs.add(o.document);
			}
			for(int i = 0; i < 5; i++) {
				ret.add(docs.get(i));
			}
			return ret;
		}
		if(!keywordsIndex.containsKey(kw2)) {
			ArrayList<Occurrence> k = keywordsIndex.get(kw1);
			ArrayList<String> ret = new ArrayList<String>();
			ArrayList<String> docs = new ArrayList<String>();
			for(int i = 0; i < k.size(); i++) {
				Occurrence o = k.get(i);
				docs.add(o.document);
			}
			for(int i = 0; i < 5; i++) {
				ret.add(docs.get(i));
			}
			return ret;
		}
		ArrayList<String> docs = new ArrayList<String>();
		ArrayList<Occurrence> k1 = keywordsIndex.get(kw1);
		ArrayList<Occurrence> k2 = keywordsIndex.get(kw2);
		int i1 = 0;
		int i2 = 0;
		while(true) {
			if(i1 == k1.size() && i2 == k2.size()) {
				break;
			}
			
			if(i1 == k1.size()) {
				for(int j = i2; j < k2.size(); j++) {
					Occurrence o = k2.get(j);
					if(docs.contains(o.document)) {
						continue;
					}
					docs.add(o.document);
				}
				break;
			}
			if(i2 == k2.size()) {
				for(int j = i1; j < k1.size(); j++) {
					Occurrence o = k1.get(j);
					if(docs.contains(o.document)) {
						continue;
					}
					docs.add(o.document);
				}
				break;
			}
			Occurrence o1 = k1.get(i1);
			Occurrence o2 = k2.get(i2);
			int freq1 = o1.frequency;
			int freq2 = o2.frequency;
			if(freq1 > freq2) {
				if(docs.contains(o1.document)) {
					i1++;
					continue;
				}
				docs.add(o1.document);
				i1++;
			}else if(freq1 < freq2) {
				if(docs.contains(o2.document)) {
					i2++;
					continue;
				}
				docs.add(o2.document);
				i2++;
			}else {
				if(docs.contains(o1.document) || docs.contains(o2.document)) {
					i1++;
					i2++;
					continue;
				}
				docs.add(o1.document);
				i1++;
				i2++;
			}
		}
		
		ArrayList<String> toBeRetDoc = new ArrayList<String>();
		for(int c = 0; c < 5 && c < docs.size(); c++) {
			toBeRetDoc.add(docs.get(c));
		}
		return toBeRetDoc;
	
	}
}
