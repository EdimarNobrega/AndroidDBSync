

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import bd.Sincronizacao;

/**
 * Servlet implementation class ExemploServidor
 */
public class ExemploServidor extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Sincronizacao mjl;
	private Sincronizacao mjla;  
	private JSONArray banco ;
	private PrintWriter out;

	public ExemploServidor() {
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
			
			response.setContentType("application/json");
			out = response.getWriter();
			mjl=new Sincronizacao( "jdbc:sqlserver://TATI-PC:1433;"+ "databaseName=escola;user=sa;password=senha");
			banco=mjl.getSyncDataBaseItensJson();	
			
			if(!(s.equals("Edimar"))){
				mjla=new Sincronizacao( "jdbc:sqlserver://TATI-PC:1433;"+ "databaseName=escola;user=sa;password=senha");
				JSONArray array  = new JSONArray();
				JSONArray bancoAndroid  = new JSONArray(s);
				int x=mjla.sicronizarBServidoComBLocal(bancoAndroid, banco);
				array.put(x);
				out.print(array);
				out.flush();
				System.out.println("Sucesso!!!!!");
			}else{			
				
				out.print(banco);
				out.flush();
				System.out.println("Sucesso!!!!!");
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
