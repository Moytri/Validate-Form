package ca.bcit.comp4613.async;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;

public class MyAsyncListener implements AsyncListener {
	FilterChain chain;
	public MyAsyncListener( FilterChain chain ) {
		this.chain = chain;
	}

	@Override
	public void onComplete(AsyncEvent event) throws IOException {
		System.out.println( Thread.currentThread().getName() + "Inside onComplete method.");
	}

	@Override
	public void onTimeout(AsyncEvent event) throws IOException {
		System.out.println("MyAsyncListener onTimeout");
		ServletResponse response = event.getAsyncContext().getResponse();
		PrintWriter out = response.getWriter();
		out.write("TimeOut Error in Processing");
	}

	@Override
	public void onError(AsyncEvent event) throws IOException {
		System.out.println("Inside onError method.");
		ServletResponse response = event.getAsyncContext().getResponse();
		PrintWriter out = response.getWriter();
		out.write("Error in Processing");
	}

	@Override
	public void onStartAsync(AsyncEvent event) throws IOException {
		System.out.println("Inside onStartAsync method.");
	}

}
