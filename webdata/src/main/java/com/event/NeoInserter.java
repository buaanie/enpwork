package com.event;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;



/**
 * Created by ACT-NJ on 2016/10/29.
 */
public class NeoInserter {
    private static Connection connection;
    private Statement  stmt;
    static{
        try {
            Class.forName("org.neo4j.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:neo4j://10.1.1.33:7474", "neo4j", "123456");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        registerShutdownHook();
    }
    public static void main(String[] args) {
        ScanNewsInfo newsInfo = new ScanNewsInfo();
        ScanWeiboEvent weiboEvent = new ScanWeiboEvent();
        NeoInserter inserter = new NeoInserter();
        Date from = new Date(1480780800000L);
        Date to = new Date(from.getTime()+3600*24*1000L);
        while(to.getTime()<1480906427000L){
            List<RelatedEvent> events = weiboEvent.filterEventFromTo(from,to);
            List<Neo4jData> neo4j = newsInfo.getNewsFromES(events);
//            inserter.delete(neo4j);
            inserter.insert(neo4j);
            from = to;
            to = new Date(to.getTime()+24*3600*1000L);
            System.out.println("------"+DateFormatUtils.format(to, "yyyy-MM-dd HH:mm")+"-------");
        }
    }
    public void insert(List<Neo4jData> neo4jDatas){
        if(neo4jDatas.size()==0){
            return;
        }
        try {
        	stmt = connection.createStatement();
        	int num;
            for(Neo4jData data:neo4jDatas){
                String id  =  data.getID().replace("-", "_");
                String name = "";
                if(data.getName().equals(""))
                	continue;
                name = data.getName().replace(" ","").replace("　　","").replace("　", "");
                String test  = name.replaceAll("\\pP|\\pS", "*").replace("\\", "*");
                String checkE = "start m=node:node_auto_index('name:"+test+"') return m.name";
                ResultSet resE  =  stmt.executeQuery(checkE);
                num = 0;
                while(resE.next()){
                    if(!resE.getString("m.name").equals("")){
                        num++;
                    }
                }
                resE.close();
                //如已有该条目则跳过
                if(num>0) {
                	System.out.println("重复插入 : "+name);
                	continue;
                };
                String type = data.getType().equals("")?"-":data.getType();
                String keywords = data.getCorewords().equals("")?"-":data.getCorewords();
                String time = data.getTimeString().equals("")?"-":data.getTimeString();
                String location = data.getLoca().equals("")?"-":data.getLoca();
                String participant = data.getPart().equals("")?"-":data.getPart();
                String news_desc = data.getNewsDesc().equals("")?"-":data.getNewsDesc();
                String insertE = "create ( event"+id+":NEvent {id:'"+id+"',name:'"+name
                        +"',location:'"+location
                        +"',time:'"+time
                        +"',type:'"+type
                        +"',participant:'"+participant
                        +"',keywords:'"+keywords
                        +"',news_desc:'"+news_desc+"'})";
//                更加标准的执行方式
//                String query = "MERGE (plane:Plane {name: {1}}) RETURN plane";
//                try (PreparedStatement stmt = con.prepareStatement(query)) {
//                    stmt.setString(1,cols[0]);
//                    stmt.executeUpdate();
//                }
                stmt.executeQuery(insertE).close();
                if(!participant.equals("-")){
                	int l = participant.split(" ").length;
                	for(int i=0;i<l;i++){
                        String parx = participant.split(" ")[i];
                        if(parx.equals("中新网")||parx.equals("新华社")||parx.equals("新华网")||parx.equals("财新网")||parx.equals("新京报")||parx.equals("凤凰网"))
                        	continue;
                        if(parx.equals(""))
                        	parx = participant;
                        System.out.print(parx + "     --- ");
                        String checkP = "start n=node:node_auto_index('name:"+parx+"') return n.name";
                        ResultSet resP =  stmt.executeQuery(checkP);
                        num = 0;
                        while(resP.next()){
                            if(!resP.getString("n.name").equals("")){
                                num++;
                            }
                        }
                        resP.close();
                        //没有查找到相关人物，新建人物
                        if(num==0){
                            String insertP = "create ( person"+id+":NPerson {id:'"+id+"',name:'"+parx+"'})";
                            stmt.executeQuery(insertP).close();
                        }
                        String insertR = "start m=node:node_auto_index('name:"+test+"'),n=node:node_auto_index('name:"+parx+"') create (n)-[r:Part_In]->(m) return r";
                        stmt.executeQuery(insertR).close();
                	}
                }
                System.out.println("插入成功 : "+name);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            System.out.println(new Date().toLocaleString()+"  关闭会话");
            try {
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
    private static void registerShutdownHook() {
        // TODO Auto-generated method stub
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                try {
                    System.out.println(new Date().toLocaleString()+"  关闭连接");
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } );
    }
    public void delete(List<Neo4jData> neo4jDatas){
        if(neo4jDatas.size()==0){
            return;
        }
        try {
        	stmt = connection.createStatement();
        	int num;
            for(Neo4jData data:neo4jDatas){
            	String name = data.getName().equals("")?"-":data.getName().replaceAll("[\\\"()pP:：‘’“”~^ ]", "*");
                String deleteR = "start m=node:node_auto_index('name:"+name+"') match (m)-[r]-() delete r,m";
//                String deleteE = "start m=node:node_auto_index('name:*"+name+"*'),n=node:node_auto_index('name:*"+name
//                		+"*') match (m)-[r]->(n) delete r,m,n";
                stmt.executeQuery(deleteR).close();
                System.out.println(name);
            }
        }catch(SQLException e){
        	e.printStackTrace();
        }finally {
            System.out.println(new Date().toLocaleString()+"  关闭会话");
            try {
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
}
