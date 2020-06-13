//package mnt.miczfs.tide.mp.src;
import java.io.IOException;
import java.util.*;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;

public class TransReducer2 extends Reducer<LongWritable, DnaWritable, LongWritable, NullWritable> {
	     
	@Override
	public void reduce(LongWritable key, Iterable<DnaWritable> values, Context context) throws IOException, InterruptedException {
	//@Override
	//public void reduce(Long key, Iterable<DnaWritable> values, Context context) throws IOException, InterruptedException {	
		context.write(new LongWritable(key.get()),NullWritable.get());	
		/*List<Long> chr2Bin= new ArrayList<Long>();  
		List<Long> chr2chr3Bin= new ArrayList<Long>();
		List<Long> chr3Bin= new ArrayList<Long>();
		long currentWindow = -1;
		int currentChr = -1;
						
		for (DnaWritable value : values) {
			//if (value.getBamWindow().get() == currentWindow)					
			if (value.getBamWindow() == currentWindow) //Window appears more than once
			{
				//if(value.getChr().get() == currentChr)
				if(value.getChr() == currentChr)
					continue;
				else
				{
					if (chr2Bin.indexOf(currentWindow) != -1)
						chr2Bin.remove(currentWindow);
					if (chr3Bin.indexOf(currentWindow) != -1)
						chr3Bin.remove(currentWindow);
					if (chr2chr3Bin.indexOf(currentWindow) == -1)
						chr2chr3Bin.add(currentWindow);
				}
			}
			else //New Window
			{
				//currentWindow = value.getBamWindow().get();
				//currentChr = value.getChr().get();
				currentWindow = value.getBamWindow();
                		currentChr = value.getChr();
				if(currentChr == 2)
					
					chr2Bin.add(currentWindow);	
				else
					chr3Bin.add(currentWindow);
						
			}
		}
		System.out.println("chr2Bin size: " + chr2Bin.size() + " , chr3Bin size: " + chr3Bin.size());
		if ((chr2Bin.size() > 0) && (chr3Bin.size() > 0))
		{
			System.out.println("passed 1");
			Long [] chr2SortedBin = chr2Bin.toArray(new Long[chr2Bin.size()]);
			Long [] chr3SortedBin = chr3Bin.toArray(new Long[chr3Bin.size()]);
			Long [] chr2chr3SortedBin = chr2chr3Bin.toArray(new Long[chr2chr3Bin.size()]);
			Arrays.sort(chr2SortedBin);
			Arrays.sort(chr3SortedBin);
			Arrays.sort(chr2chr3SortedBin);
			if (chr2chr3Bin.size() > 0)
			{
				//if ((((chr2SortedBin[chr2Bin.size()-1])<(chr2chr3SortedBin[0])) && ((chr2chr3SortedBin[chr2chr3Bin.size()-1])<(chr3SortedBin[0]))) ||
				//(((chr3SortedBin[chr3Bin.size()-1])<(chr2chr3SortedBin[0])) && ((chr2chr3SortedBin[chr2chr3Bin.size()-1])<(chr2SortedBin[0]))))
					context.write(new LongWritable(key.get()),NullWritable.get());

			}
			else{
				//System.out.println(chr2Bin.size() + " " + chr3Bin.size() + " " + chr2SortedBin[0] + " " + chr3SortedBin[0]);
				//if (((chr2SortedBin[chr2Bin.size()-1])<(chr3SortedBin[0])) || ((chr3SortedBin[chr3Bin.size()-1])<(chr2SortedBin[0]))){
				//	System.out.println("passed 3");
					context.write(new LongWritable(key.get()),NullWritable.get());
				//}
			}
		}*/
	}
}
