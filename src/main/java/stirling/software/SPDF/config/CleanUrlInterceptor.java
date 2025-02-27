package stirling.software.SPDF.config;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class CleanUrlInterceptor implements HandlerInterceptor {

	private static final List<String> ALLOWED_PARAMS = Arrays.asList("lang", "endpoint", "endpoints", "logout", "error");

	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String queryString = request.getQueryString();
		if (queryString != null && !queryString.isEmpty()) {
			String requestURI = request.getRequestURI();

			Map<String, String> parameters = new HashMap<>();

			// Keep only the allowed parameters
			String[] queryParameters = queryString.split("&");
			for (String param : queryParameters) {
				String[] keyValue = param.split("=");
				System.out.print("astirli " + keyValue[0]);
				if (keyValue.length != 2) {
					continue;
				}
				System.out.print("astirli2 " + keyValue[0]);

				if (ALLOWED_PARAMS.contains(keyValue[0])) {
					parameters.put(keyValue[0], keyValue[1]);
				}
			}

			// If there are any parameters that are not allowed
			if (parameters.size() != queryParameters.length) {
				// Construct new query string
				StringBuilder newQueryString = new StringBuilder();
				for (Map.Entry<String, String> entry : parameters.entrySet()) {
					if (newQueryString.length() > 0) {
						newQueryString.append("&");
					}
					newQueryString.append(entry.getKey()).append("=").append(entry.getValue());
				}

				// Redirect to the URL with only allowed query parameters
				String redirectUrl = requestURI + "?" + newQueryString;
				response.sendRedirect(redirectUrl);
				return false;
			}
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
	}
}
