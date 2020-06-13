/*
 * TILSH/Utils.java - Interchromosome Translocation/Insertion Detector 
 * This class provides useful functions for the TDLSH project, which implements an algorithm to detect translocations 
 * in genome using a technique that splits the reads into pre-determined length windows. This class generates 
 * fingerprints to represent those windows. The fingerprints are later submitted to an application that implements MinHash 
 * for the approximate NN search.  
 * Version 1.0 by Rosanne Vetro
 * June 2013
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.nio.ByteBuffer;
import java.lang.StringBuilder;
import java.util.stream.IntStream;

public class Utils {
	
	//public static List<Set> words=null;
	public static ArrayList<ArrayList<String>> words = null;
	//public static ArrayList<String> words = null;
	// we use hoffman codes to convert string of window to binary format.

	public static byte[] stringToByteArray(String str){
		//System.out.println("str: " + str + " ,len: " + str.length());
		//byte[] wbytes = new byte[64]; // 512 / 8  = 64
		//byte[] wbytes = new byte[32]; // 256 / 8  = 32
		byte[] wbytes = new byte[16]; // 128 / 8 = 16
		int byte_idx =0 ;

		for(int i=0; i<str.length(); i+=8){
			short a = Short.parseShort(str.substring(i,i+8), 2);
			ByteBuffer bytes = ByteBuffer.allocate(2).putShort(a);
			wbytes[byte_idx] = bytes.array()[1];
			byte_idx ++;
		}
		return wbytes;
	}
	
	private static String byteArrayToString(byte[] rdata, int offset){ 
		String readstr = "";
		for(int i=offset; i<rdata.length; i++){
			Byte cb = new Byte(rdata[i]);				
			readstr += ("0000000" + Integer.toBinaryString(0xFF & cb)).replaceAll(".*(.{8})$", "$1");
			
		}
		return readstr;
	}

	public static String repeat(int count, String with) {
    		return new String(new char[count]).replace("\0", with);
	}

	private static String binaryString_oneB(Integer index){
		String r = Integer.toBinaryString(index);
		
		int r_diff; 
		if (r.length() < 8){
			r_diff = 8 - r.length();
			r = repeat(r_diff, "0") + r;
		}else{
			r_diff = r.length() - 8;
			r = r.substring(r_diff, r.length());	
		}
		return r;
	}

	//private static boolean pairExists(Set<String> p ){		
	/*private static boolean pairExists(ArrayList<String> p){
		if(words.size() == 0 ) return false;
							
		//for(Set<String> q : words){	
		for(ArrayList<String> q: words){
			for( String s: p){
				if(q.contains(s)){
					return true;				
				}
			}									
		}			
		
		return false;
	}*/

	//Permute considering reverse complement
	//public static List<Set> permute(char[] chars, int length) {
	public static ArrayList<ArrayList<String>> permute(char[] chars, int length){ 
        //public static ArrayList<String> permute(char[] chars, int length){

	    System.out.println("chars lenth: " + chars.length + " , length: " + length);
	    final double NUMBER_OF_PERMUTATIONS = Math.pow(chars.length, length);// list is longer than needed
	    System.out.println("NUMBER_OF_PERMUTATIONS: " + NUMBER_OF_PERMUTATIONS);
	    
	    //words = new ArrayList<Set>();
	    words = new ArrayList<ArrayList<String>>();
	    //words = new ArrayList<String>();

	    char[] temp = new char[length];
	    Arrays.fill(temp, '0');

	    for (int i = 0; i < NUMBER_OF_PERMUTATIONS; i++) {
	        int n = i;
	        for (int k = 0; k < length; k++) {
	            temp[k] = chars[n % chars.length];
	             n /= chars.length;
	        }
		String tempstr = String.valueOf(temp);
		String rtempstr = reverseComplement(tempstr);

	        //Set<String> pair = new HashSet<String>(); 
	        ArrayList<String> pair = new ArrayList<String>();
		pair.add(tempstr);
		// problem? when the reverse complement of temp is equal to temp
	        pair.add(rtempstr);
		//if(pairExists(pair) == false){
		if(words.indexOf(pair) == -1){
			words.add(pair);
		}

		if(tempstr != rtempstr){
			ArrayList<String> pair_rc = new ArrayList<String>();
			pair_rc.add(rtempstr);
			pair_rc.add(tempstr);

			if (words.indexOf(pair_rc) == -1)
			//if (pairExists(pair_rc) == false)
	        		words.add(pair_rc);
		}
	    }
	    return words;
	}

	// This method convert character window into binary window, we assign each number associated
	// to a fream a bit in a 32 bit finger print. 
	public static byte[] binaryCodingJaccardWindow(String window, short permuteLength,boolean isReference){
		
		int jw_size = words.size();
		//int jw_size = 128; // we have 128 bits binary window. 
		//int jw_size = 512; // more than its needed.
		//FixedSizeBitSet jw = new FixedSizeBitSet(jw_size);
		//jw.clear();
		String bw = "";

		// why we need this if and else ??? !!!!
		//if(isReference)
		//	jw.set(1);
		//else
		//	jw.set(2);
		
		//bw = jw.toString();
		
		for (int i=0; i<= window.length()-permuteLength;i+= permuteLength){
			String current = window.substring(i, i+permuteLength);
			//COSIDER LOWER AND UPPER THE SAME
			/*****added to onsider reverse complement*****/
			//Set<String> pair = new HashSet<String>();
			ArrayList<String> pair = new ArrayList<String>();
			pair.add(current.toUpperCase());
			pair.add(reverseComplement(current.toUpperCase()));
			
			/********************************************/
			if (words.indexOf(pair) == -1){
				// consider a pair that cotains reverseComplement and current.
				ArrayList<String> pair_rc = new ArrayList<String>();
	                        pair_rc.add(reverseComplement(current.toUpperCase()));
				pair_rc.add(current.toUpperCase());
        	                if (words.indexOf(pair_rc) != -1)
					//jw.set(words.indexOf(pair_rc));
					bw = binaryString_oneB(words.indexOf(pair_rc)) + bw;
				else
					bw = binaryString_oneB(0) + bw; 
					//System.out.println("Did not find permutation and rc: " + window);

			}else{				
				//jw.set(words.indexOf(pair));
				bw = binaryString_oneB(words.indexOf(pair)) + bw;
			}
		}
		//System.out.println("bw len: "+ bw.length() + " ,bw: " + bw);
		return stringToByteArray(bw);
	}
	
	public static String jaccardWindow(String window, short permuteLength){
		
		//We have 4 possible letters, we skip N	
		int jw_size = words.size();
		//int jw_size = 256; // for chunksize = 4
		BitSet jw = new BitSet(jw_size);
				
		for (int i=0; i<= window.length()-permuteLength;i++){
			String current = window.substring(i, i+permuteLength);
			//COSIDER LOWER AND UPPER THE SAME
			/*****added to onsider reverse complement*****/
			//Set<String> pair = new HashSet<String>();
			ArrayList<String> pair = new ArrayList<String>();
			pair.add(current.toUpperCase());
			pair.add(reverseComplement(current.toUpperCase()));
			/********************************************/
			if (words.indexOf(pair) == -1)
				System.out.println("Did not find permutation and rc: "+ window);
			else				
				jw.set(words.indexOf(pair));
		}
		String jwString = "";
		int i;
		for(i=0; i<jw_size; i++){
			if (jw.get(i)){
				jwString += String.valueOf(i);
				if (i < jw_size-1)
					jwString += " ";
			}
		}
		return jwString;
	}

	public static String reverseComplement(String window){ 

		char[] rc = new char[window.length()];
	       	for (int i = window.length()-1; i >= 0; i--){
	       		int index = window.length()-1-i;
	       		rc[index] = window.charAt(i);
	       		char sym = rc[index];
			if(sym == 'A' || sym == 'a') {
				rc[index] = 'T';
			} else if(sym == 'G' || sym == 'g'){
				rc[index] = 'C';
			} else if(sym == 'C' || sym == 'c') {
				rc[index] = 'G';
			} else if(sym == 'T' || sym == 't') {
				rc[index] = 'A';
			} else if(sym == 'U' || sym == 'u'){
				rc[index] = 'A'; // consider U as T
			}
		}
		return String.valueOf(rc);
	}
}

class FixedSizeBitSet extends BitSet {
    private final int nbits;

    public FixedSizeBitSet(final int nbits) {
        super(nbits);	
        this.nbits = nbits;
    }

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(nbits);
        IntStream.range(0, nbits).mapToObj(i -> get(i) ? '1' : '0').forEach(buffer::append);
        return buffer.toString();
    }
}
