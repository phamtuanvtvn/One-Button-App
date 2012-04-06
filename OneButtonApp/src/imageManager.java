import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class imageManager {

	int imageID;
	String imageName, ImageDesc;

	/*
	 * Input: Username Output: An array consisting of a list of Image_ID,
	 * Image_Name and Image_Description for the user identified by username It
	 * can be accessed by a for loop until end of array with value at i, i+1 and
	 * i+2 representing the ImageID, name and description respectively for each
	 * image
	 */
	public static String[] getData(String username) {
		ResultSet rs = null, rs1 = null, rs2 = null;
		String image_details[] = null;
		int count = 0;
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager
					.getConnection("jdbc:sqlite:test.db");
			Statement stat = conn.createStatement();
			DatabaseMetaData dbm = conn.getMetaData();
			rs1 = dbm.getTables(null, null, "image", null);
			if (rs1.next()) {
				rs2 = stat
						.executeQuery("select COUNT(*) from image where user_name='"
								+ username + "';");
				while (rs2.next()) {
					count = rs2.getInt(1);
				}
				image_details = new String[count * 3];
				rs = stat.executeQuery("select * from image where user_name='"
						+ username + "';");
				if (rs == null)
					return null;
				else {
					int i = 0;
					while (rs.next()) {
						image_details[i] = Integer.toString(rs
								.getInt("image_id"));
						i = i + 1;
						image_details[i] = rs.getString("image_name");
						i = i + 1;
						image_details[i] = rs.getString("image_desc");
						i = i + 1;
					}
				}
			}
			rs1.close();
			conn.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return image_details;
	}

	/*
	 * Input: Username, ImageID, ImageName, ImageDesc Output: Nothing Stores the
	 * details into the Image table
	 */
	public static void storeData(String username, int imageId,
			String imageName, String imageDesc) {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager
					.getConnection("jdbc:sqlite:test.db");
			Statement stat = conn.createStatement();
			DatabaseMetaData dbm = conn.getMetaData();
			ResultSet rs1 = dbm.getTables(null, null, "image", null);
			if (!rs1.next()) {
				stat.executeUpdate("create table image (user_name, image_id, image_name, image_desc);");
			}
			PreparedStatement prep = conn
					.prepareStatement("insert into image values (?, ?, ?, ?);");
			prep.setString(1, username);
			prep.setInt(2, imageId);
			prep.setString(3, imageName);
			prep.setString(4, imageDesc);
			prep.addBatch();
			conn.setAutoCommit(false);
			prep.executeBatch();
			conn.setAutoCommit(true);
			conn.close();
			rs1.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}