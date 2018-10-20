package ca.bcit.comp4613.filter;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.AsyncContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import ca.bcit.comp4613.async.MyAsyncListener;

@WebFilter(filterName= "NameFilter", urlPatterns="/home/*", 
			servletNames= {"HomeServlet"}, asyncSupported= true)
public class NameFilter implements Filter {

	// Thread's duration
	private final static int DURATION = 100000;
	
	@Override
	public void init(FilterConfig filterConfig ) throws ServletException
	{
		System.out.println( "Hi From " + getClass().getName() );
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
			
	final Instant startTime = Instant.now();
		
	//casting request to HttpServlet
       HttpServletRequest httpRequest = (HttpServletRequest) request; 
       String name = httpRequest.getParameter("first_name");
       httpRequest.setAttribute( "name", name);

       //if method is post then do filter
       if(httpRequest.getMethod().equalsIgnoreCase("POST")){

    	    final List<String> imageList = getImages(httpRequest);
   	   
   	    final AsyncContext asyncContext = httpRequest.startAsync();
            asyncContext.addListener(new MyAsyncListener(chain));
            asyncContext.setTimeout(DURATION);
   		
            asyncContext.start( new Runnable() { // getting a new thread from the container to do the lengthy task.

    			@Override
    			public void run() {
    				
    				for(int i = 0; i < imageList.size(); i++) {
    					
    					String imgaeName = getImageName(imageList.get(i));
    					
    	 				if(imgaeName.equalsIgnoreCase(name)) {
        		    		   httpRequest.setAttribute( "image", imageList.get(i));
        		    		   break;
        				}
        				else {
        		    		   httpRequest.setAttribute( "image", "/image/duke.waving.gif");
        				}
    				}
   

     		        Instant endTime = Instant.now();
     			    System.out.println( Thread.currentThread().getName() + String.format("Long run task is completed after %d ms", ChronoUnit.MILLIS.between(startTime, endTime))  );
     			    asyncContext.dispatch();    	        	
    			}

    			//get the name from the image to which it belongs to
			private String getImageName(String imageName) {
				int start = imageName.lastIndexOf("/");
				int end = imageName.indexOf(".");
				imageName = imageName.substring(start+1, end);
				return imageName;
			}    
    		});       	   
       	} 
       	else {
    	   chain.doFilter(httpRequest, response);
       }
}
	
//generate map from image folder
 public List<String> getImages(HttpServletRequest httpRequest) {		
     //fetch all the image from image folder
     final ServletContext context = httpRequest.getServletContext();
     final Set<String> setOfImages = context.getResourcePaths("/image");		
     return new ArrayList<String>(setOfImages);
  }
}
