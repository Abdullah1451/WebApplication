

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.*;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Downloading_PDF
 */
@WebServlet("/Downloading_PDF")
public class Downloading_PDF extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	// database connection settings
	String dbURL = "jdbc:mysql://localhost:3306/syllabus";
	String dbUser = "root";
	String dbPass = "Ammi@147";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Downloading_PDF() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getMethod());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int id = Integer.parseInt(request.getParameter("id"));
		System.out.println("ID = "+ id);
		Connection conn = null; // connection to the database
		
		try {
				
			
				// connects to the database
				DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
				conn = DriverManager.getConnection(dbURL, dbUser, dbPass);

				// queries the database
				String sql = "SELECT * FROM Syllabus WHERE id = ?";
				PreparedStatement statement = conn.prepareStatement(sql);
				statement.setInt(1, id);

				ResultSet result = statement.executeQuery();
				if (result.next()) {
					
					// gets file name and file blob data
					String fileName = result.getString("filename");
					Blob  blob = result.getBlob("pdf");
					
					//Getting Binary Stream Of File In InputStream
					InputStream inputStream = blob.getBinaryStream();
					int fileLength = inputStream.available();
					System.out.println("fileLength = " + fileLength);

					ServletContext context = getServletContext();

					// sets MIME type for the file download
					String mimeType = context.getMimeType(fileName);
					System.out.println(mimeType);
					if (mimeType == null) {
						mimeType = "application/pdf";
					}
					
					// set content properties and header attributes for the response
					response.setContentType(mimeType);
					response.setContentLength(fileLength);
					String headerKey = "Content-Disposition";
					String ext = result.getString("exe");
					String headerValue = String.format("attachment; filename=\"%s\"", fileName+ext);
					response.setHeader(headerKey, headerValue);
					
					// writes the file to the client
					OutputStream outStream = response.getOutputStream();

					byte[] buffer = new byte[4096];
					int bytesRead = -1;
					
					//reading 4kb(4096 bytes) and writing till EOF 
					while ((bytesRead = inputStream.read(buffer)) != -1) {
						outStream.write(buffer, 0, bytesRead);
					}

					inputStream.close();
					outStream.close();
					
				}
				else {
					// no file found
					response.getWriter().print("File not found for the id: " + id);
				}
				conn.close();
		}
		catch(Exception e){ System.out.println(e);}
	}

}
