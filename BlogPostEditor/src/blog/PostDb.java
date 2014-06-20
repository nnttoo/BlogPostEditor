package blog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostDb {
	public String dbfile;
	
	public PostDb(String dbfilein){		
		dbfile = dbfilein;
		prepareDb();
	} 
	
	public void prepareDb(){
		try{ 
			String sqlpage = "CREATE TABLE IF NOT EXISTS  blogpost "
					+ "(_id INTEGER PRIMARY KEY, "
					+ " postid    TEXT,"  
					+ " permalink TEXT,"
					+ " title TEXT,"
					+ " content TEXT, "
					+ " keyword TEXT, "
					+ " category TEXT, "
					+ " date TEXT, "
					+ " post_status TEXT "
					+ ")";
			
			DbOpen dbopen = new DbOpen(sqlpage);
			dbopen.stm.executeUpdate();			
			dbopen.close();
			
		}catch(Exception e){e.printStackTrace();}
		
	}
	
	public boolean checkSavedPost(String id){
		boolean result = false;
			try{
				String sql = "SELECT _id FROM blogpost WHERE postid = ?";
				DbOpen dbopen = new DbOpen(sql);
				dbopen.stm.setString(1, id);
				ResultSet rs = dbopen.stm.executeQuery();
					if (rs.isBeforeFirst() ) {    
						result = true;
						} 
				rs.close();
				dbopen.close();			
			} catch(Exception e){}
		
		return result;
	}  
	public List<postDBContent> getRecentPost(String startPost,String type){  // _id , postid, title, date, post_status, permalink
		List<postDBContent> result = new ArrayList<postDBContent>();
			try{
				DbOpen dbopen;
				if(type.equals("all")){
					
				String sql = "SELECT * FROM blogpost  ORDER BY date DESC LIMIT ?,20";
				dbopen = new DbOpen(sql);
				dbopen.stm.setString(1, startPost);			 
				} else {
					String sql = "SELECT * FROM blogpost WHERE post_status = ?  ORDER BY date DESC LIMIT ?,20";
					dbopen = new DbOpen(sql);
					dbopen.stm.setString(1, "Locally Saved");				
					dbopen.stm.setString(2, startPost);			
				}				
				
				ResultSet rs = dbopen.stm.executeQuery();
				while(rs.next()){
					postDBContent child = new postDBContent();
					child.dbid = rs.getString("_id");
					child.postId = rs.getString("postid");
					child.title = rs.getString("title");
					child.date = rs.getString("date");
					child.post_status = rs.getString("post_status"); 
					child.permalink = rs.getString("permalink");  
					result.add(child);  
				}
				
				rs.close();
				dbopen.close();
				
			}catch(Exception e){e.printStackTrace();
				System.out.println("error get recent" + startPost);
			}
		
		return result;
	}
	
	public String getDBIdfromPostId(String postid){
		String result = "";
		try{
			DbOpen dbopen = new DbOpen("SELECT _id FROM blogpost WHERE postid = ?");
			dbopen.stm.setString(1, postid);
			
			ResultSet rs = dbopen.stm.executeQuery();
			while(rs.next()){
				result = rs.getString("_id");
			} 
			rs.close();
			dbopen.close();
		}catch(Exception e){e.printStackTrace();}
		
		return result;
	}
	
	public void deletePostByBlogPostId(String blogpostid){
		try{
			DbOpen dbopen = new DbOpen("DELETE FROM blogpost WHERE postid = ?");
			dbopen.stm.setString(1, blogpostid);
			dbopen.stm.executeUpdate();
			
			dbopen.close();
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	public String SaveNewPost(postDBContent dbcontent){  
		String sql;
		DbOpen dbopen = null; 
		String result = dbcontent.dbid;
		try{
			if(!dbcontent.dbid.isEmpty()){
				sql = "UPDATE blogpost SET title = ?, content = ?, keyword = ?, category = ?, date = ?, post_status = ?, permalink =? , postid = ? WHERE _id = ?";
				dbopen = new DbOpen(sql);
				dbopen.stm.setString(1, dbcontent.title);
				dbopen.stm.setString(2, dbcontent.content);
				dbopen.stm.setString(3, dbcontent.keyword);
				dbopen.stm.setString(4, dbcontent.category);
				dbopen.stm.setString(5, dbcontent.date);
				dbopen.stm.setString(6, dbcontent.post_status);
				dbopen.stm.setString(7, dbcontent.permalink);
				dbopen.stm.setString(8, dbcontent.postId);
				dbopen.stm.setString(9, dbcontent.dbid);
			} else {
				if(checkSavedPost(dbcontent.postId)){
					sql = "UPDATE blogpost SET title = ?, content = ?, keyword = ?, category = ?, date = ?, post_status = ?, permalink =? WHERE postid = ?";
					dbopen = new DbOpen(sql);
					dbopen.stm.setString(1, dbcontent.title);
					dbopen.stm.setString(2, dbcontent.content);
					dbopen.stm.setString(3, dbcontent.keyword);
					dbopen.stm.setString(4, dbcontent.category);
					dbopen.stm.setString(5, dbcontent.date);
					dbopen.stm.setString(6, dbcontent.post_status);
					dbopen.stm.setString(7, dbcontent.permalink);
					dbopen.stm.setString(8, dbcontent.postId);
				} else {
					sql = "INSERT INTO blogpost ( title,content,keyword,category,date,post_status,permalink,postid) VALUES(?,?,?,?,?,?,?,?)"; 
					dbopen = new DbOpen(sql);
					dbopen.stm.setString(1, dbcontent.title);
					dbopen.stm.setString(2, dbcontent.content);
					dbopen.stm.setString(3, dbcontent.keyword);
					dbopen.stm.setString(4, dbcontent.category);
					dbopen.stm.setString(5, dbcontent.date);
					dbopen.stm.setString(6, dbcontent.post_status);
					dbopen.stm.setString(7, dbcontent.permalink);
					dbopen.stm.setString(8, dbcontent.postId);
				}
			}
			dbopen.stm.executeUpdate();
			dbopen.close();	 
			

			result = getDBIdfromPostId(dbcontent.postId);
		}catch(Exception e){e.printStackTrace();}  
		
		return result;
	} 
	public postDBContent getPostById(String dbid){ 
		postDBContent result = new postDBContent();
			try{
				
				String sql = "SELECT * FROM blogpost WHERE _id = ?";
				DbOpen dbopen = new DbOpen(sql);
				dbopen.stm.setString(1, dbid);
				ResultSet rs = dbopen.stm.executeQuery();
				if(rs.next()){					
					result.dbid = rs.getString("_id");
					result.postId = rs.getString("postid");
					result.title = rs.getString("title");
					result.content = rs.getString("content");
					result.keyword = rs.getString("keyword");
					result.category = rs.getString("category");
					result.date = rs.getString("date");
					result.permalink = rs.getString("permalink"); 
				}
				
				rs.close();
				dbopen.close();
				
			}catch(Exception e){e.printStackTrace();}
			return result;  
	}
	
	public String countPost(String type){
		String result = null;
		String locallyQuery = (type.equals("draft"))? " WHERE post_status = 'Locally Saved'":"";
			try{
				String sql = "SELECT COUNT(*) AS jumlah FROM blogpost" + locallyQuery;
				DbOpen dbopen = new DbOpen(sql);
				ResultSet rs = dbopen.stm.executeQuery();
				while(rs.next()){
					
					result = rs.getString("jumlah");
				}
				
				rs.close();
				dbopen.close();
			}catch(Exception e){e.printStackTrace();}
		
		return result;
	}
	
	class DbOpen {
		Connection c;
		PreparedStatement stm;
		public DbOpen(String sql) throws SQLException{ 
				c = DriverManager.getConnection("jdbc:sqlite:" + dbfile);
				stm = c.prepareStatement(sql);  
		} 		
		
		public void close(){
			try{
				stm.close(); 
			} catch(Exception e){e.printStackTrace();}

			try {
				c.close();
			} catch (SQLException e) { 
				e.printStackTrace();
			}
		}
	}
	
	

}
