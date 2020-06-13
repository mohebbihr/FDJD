//package mnt.miczfs.tide.mp.src;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class TransReducer1 extends Reducer<LongWritable, DnaWritable, LongWritable, Text> {
	     
	@Override
	public void reduce(LongWritable key, Iterable<DnaWritable> values, Context context) throws IOException, InterruptedException {
	//Override
	//public void reduce(Long key, Iterable<DnaWritable> values, Context context) throws IOException, InterruptedException {
		
		ArrayList<DnaWritable> list = new ArrayList<DnaWritable>();
		double minDist = Double.MAX_VALUE;
		for (DnaWritable value : values) {
			//long bamWindow = value.getBamWindow().get();
			//long read = value.getRead().get();
			//long refWindow = value.getRefWindow().get();
			//int chr = value.getChr().get();
			//double dist = value.getDist().get();
			long bamWindow = value.getBamWindow();
                        long read = value.getRead();
                        long refWindow = value.getRefWindow();
			//int chr = value.getChr();
			double dist = value.getDist();
			//long rightChrNum = value.getRightChrNum();
			//long leftChrNum = value.getLeftChrNum();
			String rightChr = value.getRightChr();
			String leftChr = value.getLeftChr();
			if (dist > minDist || dist < 0)
				continue;
			else
			if (dist < minDist)
				 list.clear();
			//list.add(new DnaWritable(bamWindow,read,refWindow,chr,dist));
			//list.add(new DnaWritable(bamWindow,read,refWindow,dist, rightChrNum, leftChrNum));
			list.add(new DnaWritable(bamWindow,read,refWindow,dist, rightChr, leftChr));
			//minDist = Math.min(minDist, value.getDist().get());
			minDist = Math.min(minDist, dist);
		}
		for(int i = 0; i < list.size(); i++){
			DnaWritable elem = list.get(i);
			context.write(new LongWritable(elem.getRead()),new Text(elem.toString())); 
			//context.write(new LongWritable(list.get(i).getRead()),new Text(new DnaWritable(list.get(i).getBamWindow(),list.get(i).getRead(),list.get(i).getRefWindow(),list.get(i).getChr(),list.get(i).getDist()).toString()));
			//context.write(new LongWritable(list.get(i).getRead().get()),new Text(new DnaWritable(list.get(i).getBamWindow(),list.get(i).getRead(),list.get(i).getRefWindow(),list.get(i).getChr(),list.get(i).getDist()).toString()));
		}
	}
}
