package com.utils;

import java.io.IOException;
import java.util.*;

import com.store.HBaseClient;
import org.apache.commons.lang3.StringUtils;
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
			news_table = HBaseClient.getTable("newsInfo");//newsInfo nnews
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
//				get.getRowsPeriod(from,to);
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

	public void getRowsByID(List<String> ids){
		long count = 0;
		List<Get> gets = new ArrayList<Get>();
		for(String id:ids){
			Get g = new Get(id.getBytes());
			gets.add(g);
			count++;
		}
		try {
			StringBuilder  sb = new StringBuilder ();
			Result[] result = weibo_table.get(gets);
			byte[] family = Bytes.toBytes("info");
			byte[] title = Bytes.toBytes("title");
			byte[] content = Bytes.toBytes("text");
			for (Result r : result) {
				if(r.containsColumn(family,content)) {
                    String s1 = Bytes.toString(r.getValue(family,title));
					sb.append(s1+"\n");
                    String s2 = Bytes.toString(r.getValue(family,content));
					sb.append(s2+"\n");
					if (count++ % 1000 == 0) {
						long  time = System.currentTimeMillis();
						filePersist.storeFile(sb.toString(),String.valueOf(time));
						sb.delete(0,sb.length());
						System.out.println(time);
					}
				}else{
					continue;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//获取某段事件的数据，注意替换table
	public void getRowsPeriod(String startRow, String stopRow) {
		long count = 0;
		try {
            Scan scan = new Scan();
            scan.setStartRow(startRow.getBytes());
            if(stopRow!=null) {
                scan.setStopRow(stopRow.getBytes());
            }
			ResultScanner resultScanner = news_table.getScanner(scan);
			Iterator<Result> iterator = resultScanner.iterator();
            byte[] family = Bytes.toBytes("info");
            byte[] title = Bytes.toBytes("title");
            byte[] content = Bytes.toBytes("content");
			StringBuilder  sb = new StringBuilder ();
            while(iterator.hasNext()) {
				Result result = iterator.next();
                if(result.containsColumn(family,content)) {
//                    String s1 = Bytes.toString(result.getValue(family,title));
//					sb.append(s1+"\n");
                    String s2 = StringUtils.removePattern(Bytes.toString(result.getValue(family,content)),"（[^）]*）|\\([^\\)]*\\)");
					if(s2.length()>20)
					    sb.append(s2+"\n");
					if (count++ % 1500 == 0) {
						long  time = System.currentTimeMillis();
                        filePersist.storeFile(sb.toString(),String.valueOf(time));
						sb.delete(0,sb.length());
//						System.out.println(startRow+"-"+time);
					}
                }else{
                    continue;
                }
            }
			long  time = System.currentTimeMillis();
            filePersist.storeFile(sb.toString(),String.valueOf(time));
	        weibo_table.close();
		} catch (IOException e) {
			e.printStackTrace();
		}       
	}

	public void getNewsFrom(String startRow){
        getRowsPeriod(startRow,null);
	}
}
