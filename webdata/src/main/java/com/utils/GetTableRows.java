package com.utils;

import java.io.IOException;
import java.util.*;

import com.store.HBaseClient;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
{row key, column( =<family> + <label>), version} ->cell
|------------------------------------------------------|
|row_key  --|--   column_family1  --|-- column_family2 |
|           |- column1 -|- column2 -|- qualifier -|    |
|-----------|------------------------------------------|
|  row_key1 |timestamp:a|t1:c       |                  |
|           |ts2:b      |           |                  |
|------------------------------------------------------|
|    key2   |t1:a       |t1:c       |                  |
|           |t2:b       |           |                  |
|------------------------------------------------------|
 */
public class GetTableRows {
	private HTable weibo_table;
	private HTable news_table;
	private FilesOpt filePersist = null;
	private Logger logger;
	public GetTableRows(){
		filePersist = new FilesOpt();
		logger = LoggerFactory.getLogger(GetTableRows.class);
//		weibo_table = new HTable()
		try {
			weibo_table = HBaseClient.getTable("weibosInfo");
			news_table = HBaseClient.getTable("nnews");
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
	public static void main(String[] args) {
			GetTableRows get = new GetTableRows();
//			get.getOneRow("2478-1002448");

//			List<String> dlList = Arrays.asList("id1","id2");
//			get.deleteRows(dlList);

//			DateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//			Calendar start = Calendar.getInstance();
//			start.set(2017,7,11);
//			Calendar end = Calendar.getInstance();
//			end.set(2017,7,13);
//			while(start.before(end)){
//				String from = sdf.format(start.getTime());
//				start.add(Calendar.DAY_OF_MONTH,1);
//				String to = sdf.format(start.getTime());
//				get.getRows(from,to);
//			}

			String from = "tct";
			get.getNewsFrom(from);
	}

	public void deleteRows(List<String> delete_ids) {
		long count = 0;
		List<Delete> deletes=new ArrayList<Delete>();
		for(String id:delete_ids){
			Delete d = new Delete(id.getBytes());
			deletes.add(d);
			count++;
		}
		try {
			weibo_table.delete(deletes);
			weibo_table.close();
			System.out.println("删除成功_"+count);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getOneRow(String id) {
		Get scan = new Get(id.getBytes());
		try {
			Result r = weibo_table.get(scan);
			if (r.size() == 0) {
				System.out.println("找不到相关内容");
				return;
			}
			String rowKeyString = Bytes.toString(r.getRow());
            System.out.println(rowKeyString);
            for (Cell cell : r.rawCells()) { //listCells sorted
//				if(new String(cell.getQualifierArray()).matches("floor|reply|watch"))
//					valuei = String.valueOf(Bytes.toLong(cell.getValueArray()));
//				else if(new String(cell.getQualifierArray()).matches("jing"))
//					valuei = String.valueOf(Bytes.toBoolean(cell.getValueArray()));
                System.out.println(new String(cell.getQualifier())+": "+new String(cell.getValue()));
                System.out.print(new String(CellUtil.cloneQualifier(cell))+" :");
                System.out.println(Bytes.toString(CellUtil.cloneValue(cell)));
                String s = Bytes.toString(cell.getValueArray(),cell.getValueOffset(),cell.getValueLength());
                System.out.println(s);
            }
        } catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getRows(String startRow,String stopRow) {
		long count = 0;
		try {
            Scan scan = new Scan();
            scan.setStartRow(startRow.getBytes());
            scan.setStopRow(stopRow.getBytes());
			ResultScanner resultScanner = weibo_table.getScanner(scan);
			Iterator<Result> iterator = resultScanner.iterator();
			byte[] family = Bytes.toBytes("info");
			StringBuffer sb = new StringBuffer();
            while(iterator.hasNext()) {
				Result result = iterator.next();
//                byte[] title = Bytes.toBytes("title");
                byte[] content = Bytes.toBytes("text");
                if(result.containsColumn(family,content)) {
//                    String s1 = Bytes.toString(result.getValue(family,title));
//					sb.append(s1+"\n");
//                    String s2 = Bytes.toString(result.getValue(family,content));
//					sb.append(s2+"\n");
					if (count++ % 1000 == 0) {
						long  time = System.currentTimeMillis();
//						filePersist.storeFile(sb.toString(),String.valueOf(time));
//						sb.delete(0,sb.length());
						System.out.println(startRow+"-"+time);
					}
                }else{
                    continue;
                }
            }
//			long  time = System.currentTimeMillis();
//			filePersist.storeFile(sb.toString(),String.valueOf(time));
//	        weibo_table.close();
		} catch (IOException e) {
			e.printStackTrace();
		}       
	}

	public void getNewsFrom(String startRow){
		long count = 0;
		Scan scan = new Scan(startRow.getBytes());
		ResultScanner resultScanner = null;
		try {
			resultScanner = news_table.getScanner(scan);
			Iterator<Result> iterator = resultScanner.iterator();
			byte[] family = Bytes.toBytes("info");
			while(iterator.hasNext()) {
				Result result = iterator.next();
				byte[] content = Bytes.toBytes("title");
				if(result.containsColumn(family,content)) {
					String s = Bytes.toString(result.getValue(family,content));
					if (count++ % 1000 == 0) {
						long  time = System.currentTimeMillis();
						System.out.println(s+"-"+time);
					}
				}else{
					continue;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
