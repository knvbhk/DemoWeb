/*
 * Copyright 2004-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.filter;
import java.io.File;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * @author Roy Tsang
 *
 */
/**
 * Servlet Filter implementation class AngularFilter
 */
@WebFilter("/frontEnd/*")
public class AngularFilter implements Filter {
	FilterConfig filterConfig;
    /**
     * Default constructor. 
     */
    public AngularFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		this.filterConfig = null;
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		String destination,angularRootPath,realPath; 
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse=(HttpServletResponse)response;
		ServletContext context = httpRequest.getServletContext();
		destination =httpRequest.getServletPath();
		realPath =context.getRealPath(destination);
		angularRootPath="";
		File f = new File(realPath);
		if (f.exists()) {
			chain.doFilter(request, response);
		} else {
			FilterRegistration fr= context.getFilterRegistration(this.getClass().getName());
			for (String mapping: fr.getUrlPatternMappings()) {
				mapping=mapping.replace("/*", "");
				if (destination.indexOf(mapping)>-1) {
					angularRootPath=mapping;
					break;
				}
			}
			if (angularRootPath.equals("")) {
				httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
			} else {
				filterConfig.getServletContext().getRequestDispatcher(angularRootPath+"/index.html").forward(request, response);
			}
		}
		f=null;		
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		this.filterConfig=fConfig;
	}

}
