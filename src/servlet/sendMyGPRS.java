package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.scok.ServerSocketListenerThread;

public class sendMyGPRS extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public sendMyGPRS() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("    This is ");
		out.print(this.getClass());
		out.println(", using the GET method");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		String portName = request.getParameter("mySend");//获取特定的值
		char[] portNameList = portName.toCharArray();//将字符串转化为字符数组
		byte[] portNameListNum = new byte[portNameList.length];
		for(int i = 0; i < portNameList.length; i++){//将字符转化为ascii码
			portNameListNum[i] = (byte)portNameList[i];
			try {
				Thread.sleep(120);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	try{
        		//socket.sendUrgentData(0xFF);
        		ServerSocketListenerThread.netOutputStream.write(portNameListNum[i]);
        		}catch(Exception ex){
        			System.out.println("对方已经断开发送失败跳出发送循环");
        			break;
        	}
        	if(i == portNameList.length - 1){
             	try{
            		//socket.sendUrgentData(0xFF);
            		ServerSocketListenerThread.netOutputStream.write(0x0A);//发送结束符
            		}catch(Exception ex){
            			System.out.println("对方已经断开发送失败");
            	}
        	}
		}

		
		
		out.write("1");
		out.flush();
		out.close();
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
