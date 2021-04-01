import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class musicApp {

	static Scanner scanner = new Scanner(System.in);
	static Connection con;
	static PreparedStatement pst;
	static Statement st;
	static ResultSet rs;
	
	public static void main(String[] args) throws SQLException {
		try {
			Class.forName("org.mariadb.jdbc.Driver");
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			
			System.out.print("Enter connection info(port id pw) -> ");
			String str = scanner.nextLine();
			String info[] = str.split(" ");
			con = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:" + info[0] + "/musicdb", info[1], info[2]);
			
			//con = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/musicdb", "root", "990929");
			
			if(con == null) {
				System.out.println("DB connection failed");
				return;
			}
			System.out.println("DB connection success");
		}catch(Exception e) {
			System.out.println("DB connection failed");
			return;
		}
		
		st = con.createStatement();
		String input = "1";
		
		while(input != "0") {
			System.out.println("\n------------Main Menu-------------");
			System.out.println("0. Exit");
			System.out.println("1. Top 1 Music");
			System.out.println("2. Sign Up");
			System.out.println("3. User mode");
			System.out.println("4. Manager mode");
			System.out.println("----------------------------------");
			System.out.print("\nInput -> ");
			input = scanner.nextLine();
			
			try {
				switch(input){
				case "0":
					return;
				case "1":
					top1music();
					continue;
				case "2":
					signUp();
					continue;
				case "3"://user
					System.out.print("Enter your UserID -> ");
					int uid = Integer.parseInt(scanner.nextLine());
					userMode(uid);
					continue;
				case "4"://Manager
					System.out.print("Enter your ManagerID -> ");
					int mngid = Integer.parseInt(scanner.nextLine());
					managerMode(mngid);
					continue;
				default:
					continue;
				}
			}catch (Exception e) {
				continue;
			}
		}
		
		st.close();
		rs.close();
		con.close();
	}
	
	public static void top1music() throws SQLException {
		rs = st.executeQuery("select mid, title from music where count=(select MAX(count) from music)");
		
		System.out.println("\n--------------------------------");
		System.out.println("    MusicID          Title");
		System.out.println("--------------------------------");
		
		while(rs.next()) {
			int mid = rs.getInt(1);
			String title = rs.getString(2);
			System.out.println("      " + mid + "             " + title);
		}
		
		return;
	}
	
	public static void signUp() throws SQLException {		
		System.out.print("Enter your new ID -> ");
		int newid = Integer.parseInt(scanner.nextLine());
		
		pst = con.prepareStatement("select uid from user where uid=?");
		pst.setInt(1, newid);
		rs = pst.executeQuery();
		
		if(rs.next()) {
			System.out.println("Already exists this ID");
			return;
		}
		
		System.out.print("Enter your name -> ");
		String name = scanner.nextLine();
		System.out.print("Enter your phone number -> ");
		String phone = scanner.nextLine();
		System.out.print("Enter your email -> ");
		String email = scanner.nextLine();
		System.out.print("Enter your birth date(YYYY-MM-DD) -> ");
		String bdate = scanner.nextLine();
		
		try {
			pst = con.prepareStatement("insert into user(uid, name, phone, email, bdate) values (?,?,?,?,?)");
			pst.setInt(1, newid);
			pst.setString(2, name);
			pst.setString(3, phone);
			pst.setString(4, email);
			pst.setDate(5, java.sql.Date.valueOf(bdate));
			pst.executeQuery();
		}catch(SQLException e) {
			e.printStackTrace();
			System.out.println("Already exists or invalid");
			return;
		}
		
		System.out.println("\nSign Up Success");
		
		return;
	}
	
	public static void userMode(int uid) throws SQLException{
		pst = con.prepareStatement("select uid from user where uid=?");
		pst.setInt(1, uid);
		rs = pst.executeQuery();
		
		if(!rs.next()) {
			System.out.println("Invalid ID");
			return;
		}
		
		while(true) {
			System.out.println("\n--------------User--------------");
			System.out.println("UserID: " + uid);
			System.out.println("0. Return to previous menu");
			System.out.println("1. Play Music");
			System.out.println("2. Add Music into playlist");
			System.out.println("3. Remove Music from playlist");
			System.out.println("4. Create new playlist");
			System.out.println("5. Remove playlist");
			System.out.println("6. Show my playlist");
			System.out.println("7. Enter my playlist");
			System.out.println("--------------------------------");
			System.out.print("Input -> ");
			String input = scanner.nextLine();
			
			String name;
			int music;
			boolean check = true;
			
			switch(input) {
			case "0":
				return;
			case "1"://play
				System.out.print("Enter musicID to play -> ");
				music = Integer.parseInt(scanner.nextLine());
				
				pst = con.prepareStatement("select mid from music where mid=?");
				pst.setInt(1, music);
				rs = pst.executeQuery();
				
				if(!rs.next()) {
					System.out.println("Not exist");
					continue;
				}
				
				try {
					pst = con.prepareStatement("update music set count=count+1 where mid=?");
					pst.setInt(1, music);
					pst.executeQuery();
					
					System.out.println("MusicID "+music+" played");
					continue;
				}catch(Exception e){
					continue;
				}
			case "2"://add music
				System.out.print("Enter your playlist name to add music -> ");
				name = scanner.nextLine();
				
				pst = con.prepareStatement("select pname from playlist where uid=? and pname=?");
				pst.setInt(1, uid);
				pst.setString(2, name);
				rs = pst.executeQuery();
				
				if(!rs.next()) {
					System.out.println("Invalid Playlist");
					continue;
				}
				
				playlistAddMusic(uid, name);
				continue;
			case "3"://remove music
				System.out.print("Enter your playlist name to remove music -> ");
				name = scanner.nextLine();
				
				pst = con.prepareStatement("select pname from playlist where uid=? and pname=?");
				pst.setInt(1, uid);
				pst.setString(2, name);
				rs = pst.executeQuery();
				
				if(!rs.next()) {
					System.out.println("Invalid Playlist");
					continue;
				}
				
				playlistRemoveMusic(uid, name);
				continue;
			case "4"://create playlist				
				try {
					System.out.print("Enter playlist name to create -> ");
					name = scanner.nextLine();
					
					pst = con.prepareStatement("insert into playlist(uid, pname, pdate) values (?,?,now())");
					pst.setInt(1, uid);
					pst.setString(2, name);
					pst.executeQuery();
					
					System.out.println("Playlist "+name+" is created");
					continue;
				}catch(SQLException e) {
					System.out.println("Already exists");
					continue;
				}
			case "5"://remove playlist
				try {
					System.out.print("Enter your playlist name to remove -> ");
					name = scanner.nextLine();
					
					pst = con.prepareStatement("select pname from playlist where uid=? and pname=?");
					pst.setInt(1, uid);
					pst.setString(2, name);
					rs = pst.executeQuery();
					
					if(!rs.next()) {
						System.out.println("Not exist or Invalid");
						continue;
					}
					
					pst = con.prepareStatement("delete from playlisthas where uid=? and pname=?");
					pst.setInt(1, uid);
					pst.setString(2, name);
					pst.executeQuery();
					
					pst = con.prepareStatement("delete from playlist where uid=? and pname=?");
					pst.setInt(1, uid);
					pst.setString(2, name);
					pst.executeQuery();
					
					System.out.println("Playlist "+name+" is removed");
					continue;
				}catch(SQLException e){
					e.printStackTrace();
					System.out.println("Invalid Playlist");
					continue;
				}
			case "6"://show
				pst = con.prepareStatement("select pname, nummusic from playlist where uid=?");
				pst.setInt(1, uid);
				rs = pst.executeQuery();
				
				while(rs.next()) {
					if(check) {
						System.out.println("\n--------------------------------------------");
						System.out.println("    Playlist Name       Number of Music");
						System.out.println("--------------------------------------------");
						check = false;
					}
					String pname = rs.getString(1);
					int cnt = rs.getInt(2);
					System.out.println("     " + pname + "                    " + cnt);
				}
				
				if(check) System.out.println("Nothing to show");
				continue;
			case "7"://enter, show music
				try {
				System.out.print("Enter playlist name -> ");
				name = scanner.nextLine();
				
				pst = con.prepareStatement("select M.mid, M.title from music as M, playlisthas as P where P.uid=? and pname=? and P.mid=M.mid");
				pst.setInt(1, uid);
				pst.setString(2, name);
				rs = pst.executeQuery();
				
				while(rs.next()) {
					if(check) {
						System.out.println("\n-------------------------------------");
						System.out.println("     Music ID         Title");
						System.out.println("-------------------------------------");
						check = false;
					}
					int mid = rs.getInt(1);
					String title = rs.getString(2);
					System.out.println("     " + mid + "                " + title);
				}
				if(check) System.out.println("Nothing to show");
				continue;
				}catch(SQLException e) {
					e.printStackTrace();
					continue;
				}
			default:
				continue;
			}
			
		}
	}
	
	public static void playlistAddMusic(int uid, String pname) throws SQLException {						
		try {
			System.out.print("Enter MusicID to add -> ");
			int music = Integer.parseInt(scanner.nextLine());
			
			pst = con.prepareStatement("insert into playlisthas(uid, pname, mid) values (?,?,?)");
			pst.setInt(1, uid);
			pst.setString(2, pname);
			pst.setInt(3, music);
			pst.executeQuery();
			
			pst = con.prepareStatement("update playlist set nummusic=nummusic+1 where uid=? and pname=?");
			pst.setInt(1, uid);
			pst.setString(2, pname);
			pst.executeQuery();
			
			System.out.println("MusicID "+music+" added into "+pname);
			return;
			
		}catch (SQLException e) {
			System.out.println("Already added or Invalid");
			return;
		}
	}
	
	public static void playlistRemoveMusic(int uid, String pname) throws SQLException{
		try {
			System.out.print("Enter MusicID to remove -> ");
			int music = Integer.parseInt(scanner.nextLine());
			
			pst = con.prepareStatement("select mid from playlisthas where uid=? and pname=? and mid=?");
			pst.setInt(1, uid);
			pst.setString(2, pname);
			pst.setInt(3, music);
			rs = pst.executeQuery();
			
			if(!rs.next()) {
				System.out.println("This music not exist");
				return;
			}
			
			pst = con.prepareStatement("delete from playlisthas where uid=? and pname=? and mid=?");
			pst.setInt(1, uid);
			pst.setString(2, pname);
			pst.setInt(3, music);
			pst.executeQuery();
			
			pst = con.prepareStatement("update playlist set nummusic=nummusic-1 where uid=? and pname=?");
			pst.setInt(1, uid);
			pst.setString(2, pname);
			pst.executeQuery();
			
			System.out.println("MusicID "+music+" removed from "+pname);
			return;
		}catch(SQLException e) {
			System.out.println("Cannot remove or invalid");
			return;
		}
	}
	
	public static void managerMode(int mngid) throws SQLException{
		pst = con.prepareStatement("select mngid from manager where mngid=?");
		pst.setInt(1, mngid);
		rs = pst.executeQuery();
		
		if(!rs.next()) {
			System.out.println("Invalid ID");
			return;
		}
		
		while(true) {
			System.out.println("\n-------------Manager------------------");
			System.out.println("ManagerID: " + mngid);
			System.out.println("0. Return to previous menu");
			System.out.println("1. Show Registered Music by Manager " + mngid);
			System.out.println("2. Register Music");
			System.out.println("3. Remove Music");
			System.out.println("4. Select User to Manage");
			System.out.println("5. Remove User");
			System.out.println("6. Show Manager " + mngid+"'s Users");
			System.out.println("--------------------------------------");
			System.out.print("Input -> ");
			String input = scanner.nextLine();
			
			int music;
			boolean check = true;
			
			switch(input) {
			case "0" : 
				return;
			case "1"://show registered music
				pst = con.prepareStatement("select mid, title from music where mngid=?");
				pst.setInt(1, mngid);
				rs = pst.executeQuery();
				
				while (rs.next()) {
					if (check) {
						System.out.println("\n--------------------------------------------");
						System.out.println("    MusicID         Title");
						System.out.println("--------------------------------------------");
						check = false;
					}
					int mid = rs.getInt(1);
					String title = rs.getString(2);
					System.out.println("   " + mid + "             " + title);
				}
				if (check) System.out.println("Nothing registered");
				continue;
			case "2"://register music
				try {
					System.out.print("Enter MusicID to Register -> ");
					music = Integer.parseInt(scanner.nextLine());
					System.out.print("Enter Title -> ");
					String title = scanner.nextLine();
					System.out.print("Enter Artist -> ");
					String aname = scanner.nextLine();

					pst = con.prepareStatement("insert into music(mid, title, rdate, mngid) value(?,?,?,?)");
					pst.setInt(1, music);
					pst.setString(2, title);
					pst.setDate(3, new java.sql.Date(System.currentTimeMillis()));
					pst.setInt(4, mngid);
					pst.executeQuery();

					pst = con.prepareStatement("insert into artist(aname, mid) value(?,?)");
					pst.setString(1, aname);
					pst.setInt(2, music);
					pst.executeQuery();
					
					System.out.println("MusicID "+music+" is registered");
					continue;
				}catch(SQLException e) {
					System.out.println("Already exists this MusicID");
					continue;
				}
			case "3"://remove music
				try {
					System.out.print("Enter MusicID to remove -> ");
					music = Integer.parseInt(scanner.nextLine());

					pst = con.prepareStatement("select mngid from music where mngid=? and mid=?");
					pst.setInt(1, mngid);
					pst.setInt(2, music);
					rs = pst.executeQuery();

					if (!rs.next()) {
						System.out.println("Invalid ID");
						continue;
					}

					pst = con.prepareStatement("delete from artist where mid=?");
					pst.setInt(1, music);
					pst.executeQuery();
					
					rs = st.executeQuery("select pname, uid from playlisthas where mid=" + music);
					
					while(rs.next()) {
						
						String pname = rs.getString(1);
						int uid = rs.getInt(2);
						
						pst=con.prepareStatement("update playlist set nummusic=nummusic-1 where pname=? and uid=?");
						pst.setString(1, pname);
						pst.setInt(2, uid);
						pst.executeQuery();
					}
					
					pst = con.prepareStatement("delete from playlisthas where mid=?");
					pst.setInt(1, music);
					pst.executeQuery();
					
					pst = con.prepareStatement("delete from music where mngid=? and mid=?");
					pst.setInt(1, mngid);
					pst.setInt(2, music);
					pst.executeQuery();

					System.out.println("MusicID "+music+" is removed");
					continue;
				}catch(SQLException e) {
					System.out.println("Cannot remove or invalid");
					continue;
				}
			case "4"://select user
				try {
					System.out.print("Enter UserID to manage -> ");
					int uid = Integer.parseInt(scanner.nextLine());
					
					pst = con.prepareStatement("select uid from user where uid=? and mngid is null");
					pst.setInt(1, uid);
					rs = pst.executeQuery();
					
					if(!rs.next()) {
						System.out.println("Already exist or Invalid ID");
						continue;
					}
					
					pst = con.prepareStatement("update user set mngid=? where uid=?");
					pst.setInt(1, mngid);
					pst.setInt(2, uid);
					pst.executeQuery();
					
					System.out.println("UserID "+uid+" is selected");
					continue;					
				}catch(SQLException e) {
					System.out.println("Cannot manage or Invalid");
					continue;
				}
			case "5"://remove user
				try {
					System.out.print("Enter UserID to remove -> ");
					int uid = Integer.parseInt(scanner.nextLine());
					
					pst = con.prepareStatement("select uid from user where uid=? and mngid=?");
					pst.setInt(1, uid);
					pst.setInt(2, mngid);
					rs = pst.executeQuery();
					
					if(!rs.next()) {
						System.out.println("Invalid ID");
						continue;
					}
					pst = con.prepareStatement("delete from playlisthas where uid=?");
					pst.setInt(1, uid);
					pst.executeQuery();
					
					pst = con.prepareStatement("delete from playlist where uid=?");
					pst.setInt(1, uid);
					pst.executeQuery();
					
					pst = con.prepareStatement("delete from user where uid=? and mngid=?");
					pst.setInt(1, uid);
					pst.setInt(2, mngid);
					pst.executeQuery();
					
					System.out.println("UserID "+uid+" is removed");
					continue;
				}catch(SQLException e) {
					System.out.println("Cannot remove or Invalid");
					continue;
				}
			case "6"://show users
				rs = st.executeQuery("select uid from user where mngid="+mngid);
				if(!rs.next()) {
					System.out.println("Nothing to show");
					continue;
				}
				
				rs = st.executeQuery("select uid, name from user where mngid="+mngid);
				System.out.println("\n--------------------------------------------");
				System.out.println("    UserID         Name");
				System.out.println("--------------------------------------------");
				
				while(rs.next()) {
					int uid = rs.getInt(1);
					String name = rs.getString(2);
					System.out.println("        " + uid + "           " + name);
				}
				continue;
			default:
				continue;
			}
		}
	}
}
