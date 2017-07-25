package com.utils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.store.HBaseClient;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

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
	private HTable table;
	public GetTableRows() throws IOException {
		table = new HBaseClient().getTable("carsInfo");
	}
	public static void main(String[] args) {
		try {
			GetTableRows get = new GetTableRows();
			get.getOneRow("2478-1002448");

//			List<String> dlList = Arrays.asList("id1","id2");
//			get.deleteRows(dlList);

			DateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String start = "20170101";
			String end = "20170201";
			Long from = sdf.parse(start).getTime();
			Long to = sdf.parse(end).getTime();
			while(from<to){
				long temp = from+24*3600*1000;
//				temp = (temp<to) ? temp: to;
				get.getRows(String.valueOf(from), String.valueOf(temp));
				from = temp;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
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
			table.delete(deletes);
			table.close();
			System.out.println("删除成功_"+count);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getOneRow(String id) {
		Get scan = new Get(id.getBytes());
		try {
			Result r = table.get(scan);
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
			ResultScanner resultScanner = table.getScanner(scan);
			Iterator<Result> iterator = resultScanner.iterator();
			byte[] family = Bytes.toBytes("info");
            while(iterator.hasNext()) {
                Result result = iterator.next();
                count++;
                byte[] id = Bytes.toBytes("id");
                if(result.containsColumn(family,id)) {
                    String s = Bytes.toString(result.getValue(family,id));
                    System.out.println(s);
                }else{
                    continue;
                }
                if (count++ % 1000 == 0) {
                    System.out.println("---------");
                }
            }
	        table.close();
		} catch (IOException e) {
			e.printStackTrace();
		}       
	}

}
