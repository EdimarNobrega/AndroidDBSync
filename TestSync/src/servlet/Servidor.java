package servlet;



import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import modelo.SyncServidor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bd.Sincronizacao;

/**
 * Servlet implementation class MontarJson
 */
public class Servidor extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private SyncServidor mjl;
	private SyncServidor mjla;  
	private JSONArray banco ;
	private PrintWriter out;

	public Servidor() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


		try {
			String s=request.getParameter("banco");	
			if(s==null){
				s="Edimar";
			}
			System.out.println(s);
			response.setContentType("application/json");
			out = response.getWriter();
			mjl=new SyncServidor( "jdbc:sqlserver://TATI-PC:1433;"+ "databaseName=escola;user=sa;password=senha");
			banco=mjl.getBancoLocal();	
			//out.print(banco);
			if(!(s.equals("Edimar"))){
				mjla=new SyncServidor( "jdbc:sqlserver://TATI-PC:1433;"+ "databaseName=escola;user=sa;password=senha");
				JSONArray array  = new JSONArray();
				JSONArray bancoAndroid  = new JSONArray(s);
				int x=mjla.sicronizarBServidoComBLocal(bancoAndroid, banco);
				array.put(x);
				out.print(array);
				out.flush();
				System.out.println("Sucesso na Exportarção!!!!!");
			}else{			
				
				out.print(banco);
				out.flush();
				System.out.println("Sucesso na Importarção!!!!!");
			}

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
