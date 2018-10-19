package ca.bcit.comp4613.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.ServletSecurity.TransportGuarantee;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.PushBuilder;

@WebServlet( name="HomeServlet", displayName="Home Servlet", urlPatterns="/home")
@ServletSecurity(@HttpConstraint(transportGuarantee = TransportGuarantee.CONFIDENTIAL))
public class HomeServlet extends HttpServlet {
	
	//application image is pushed by Server before html page
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

				// Returns a new PushBuilder instance if HTTP/2 enabled or null if it is not enabled/supported.
				PushBuilder pushBuilder = request.newPushBuilder();
				
				String name = (String) request.getAttribute("name");
				String image = (String) request.getAttribute("image");
				
				if((name == null || name.isEmpty()) && image == null) {
					if (pushBuilder != null ){
						pushBuilder
								.path(request.getContextPath()+"/image/duke.waving.gif")
								.addHeader("content-type", "image/gif")
								.push();
					}
				}

				else {
					if (pushBuilder != null ){
						pushBuilder
								.path(request.getContextPath()+image)
								.addHeader("content-type", "image/gif")
								.push();
					}
				}
				
				response.setContentType("text/html");
				PrintWriter out = response.getWriter();
			
				String myHtml = createForm(request, response, name, image);				
				out.println(myHtml);
				out.close();
	}

	//create form
	public String createForm(HttpServletRequest request, HttpServletResponse response, String name, String image) throws IOException {

		//start html body
		StringBuilder myHtml = new StringBuilder("<html>" + "<head><title>Hello</title></head>"
				+ "<body  bgcolor=\"#ffffff\">");
		
		//create the entire form with fields
		String formHtml = "<h2>Hello my name is Duke. What is yours?"
				+ "<form action = 'home' method = 'POST'>"
				+ "<input type = 'text' name = 'first_name'>"
				+ "&nbsp&nbsp<input type = 'submit' value = 'Submit' />"
				+ "&nbsp&nbsp<input type = 'reset' value = 'Reset' /></form>";
		
		if((name == null || name.isEmpty()) && (image == null || image.isEmpty())) {
			myHtml.append("<img src=\"" + request.getContextPath() + "/image/duke.waving.gif\" alt=\"Duke waving\">");
			return myHtml.append(formHtml).append("</body></html>").toString();
		}
		
		else {			
			myHtml.append("<img src=\"" + request.getContextPath() + image+"\" alt=\""+ name + "waving\">");
			myHtml.append(formHtml);
			
			//if user submits form without user input
			if(name == null || name.isEmpty()) {
				return myHtml.append("</body></html>").toString();
			}
			else {
				myHtml.append("<br><h2>Hello, " + (name.substring(0, 1).toUpperCase()+name.substring(1)) + "!</h2>" );
				return myHtml.append("</body></html>").toString();
			}
		}
	}
	

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		doGet(request, response);
	}	
}
