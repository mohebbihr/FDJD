//package mnt.miczfs.tide.mp.src;
import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class TransMapper1 extends  Mapper<LongWritable, Text, LongWritable, DnaWritable> {
				 
	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
	//@Override
	//public void map(Long key, Text value, Context context) throws IOException, InterruptedException {  
	 try{
		String line = value.toString();
		StringTokenizer tokenizer = new StringTokenizer(line);
		if(tokenizer.hasMoreTokens()){
			/*LongWritable bamWindow = new LongWritable(Long.parseLong(tokenizer.nextToken()));
			LongWritable read = new LongWritable(Long.parseLong(tokenizer.nextToken()));
			LongWritable refWindow = new LongWritable(Long.parseLong(tokenizer.nextToken()));
			IntWritable chr = new IntWritable(Integer.parseInt(tokenizer.nextToken()));
			DoubleWritable dist = new DoubleWritable(Double.parseDouble(tokenizer.nextToken()));
			DnaWritable dnaW = new DnaWritable(bamWindow,read,refWindow,chr,dist);
			*/
			Long bamWindow = Long.parseLong(tokenizer.nextToken());
                        Long read = Long.parseLong(tokenizer.nextToken());
                        Long refWindow = Long.parseLong(tokenizer.nextToken());
                        //Integer chr = Integer.parseInt(tokenizer.nextToken());
                        Double dist = Double.parseDouble(tokenizer.nextToken());
                        String rightChr = tokenizer.nextToken();
			String leftChr = tokenizer.nextToken();
			// covert chr1 to 1
			//Long rightChrNum = Long.parseLong(rightChr.substring(rightChr.length() - 1));
                        //Long leftChrNum = Long.parseLong(leftChr.substring(leftChr.length() - 1));
			//DnaWritable dnaW = new DnaWritable(bamWindow,read,refWindow,chr,dist);
			//DnaWritable dnaW = new DnaWritable(bamWindow,read,refWindow,dist, rightChrNum, leftChrNum);
			DnaWritable dnaW = new DnaWritable(bamWindow,read,refWindow,dist, rightChr, leftChr);
			//context.write(bamWindow,dnaW);
			context.write(new LongWritable(bamWindow),dnaW);
		}
	   }catch(Exception e){}
	}
}
