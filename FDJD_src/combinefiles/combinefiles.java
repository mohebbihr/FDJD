
/*MapReduce application 
*/
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class combinefiles {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		// TODO Auto-generated method stub
				
		String mp_output_dir = args[0];
		String outFilePath = mp_output_dir + "fpart-all";
		PrintWriter writer = new PrintWriter(outFilePath);
		HashMap<Integer, Integer> hmap = new HashMap<Integer, Integer>();
		System.out.println();
		System.out.println("**Starting Combining Files**");
		System.out.println("Output File: "+ outFilePath);

		File folder = new File(mp_output_dir);
		File[] listOfFiles = folder.listFiles();
		System.out.println("Number of input files: " + listOfFiles.length); 
		for (int i = 0; i < listOfFiles.length; i++) 
		{
			if ((listOfFiles[i].isFile()) && !listOfFiles[i].getName().endsWith("l") && listOfFiles[i].getName().length() > 2)
			{
				String fileName = listOfFiles[i].getName();
				String inFile = folder.getAbsolutePath()+ "/" + fileName;
				System.out.println("Combining " + inFile + " ..");
				String scan;
			        try {
            				FileReader file = new FileReader(inFile);
            				BufferedReader br = new BufferedReader(file);
					while ((scan = br.readLine()) != null) {
						int fid = Integer.parseInt(scan);
						if (!hmap.containsKey(fid)){ // we write only unique ids to the output.
							hmap.put(fid, fid);	
							//writer.println(fid);
						}
					}
				}catch(Exception e){
					System.out.println(e.getMessage());
				}
			 }
		}
		// sort the hashmap
		TreeMap<Integer,Integer> sorted = new TreeMap<>(hmap);
		Set<Entry<Integer, Integer>> mappings = sorted.entrySet();
		int count = 0;
		for(Entry<Integer,Integer> mapping: mappings){
			count ++;
			if (count % 4 == 0){
				writer.println(mapping.getKey());
			}
		}
		writer.close();
		System.out.println("** Combining is Done**");
	}
}
