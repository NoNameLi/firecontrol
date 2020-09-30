package cn.turing.firecontrol.datahandler.util;

import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;


/**
 * Http与Servlet工具类.
 *
 */
public class ServletUtils {


	/**
	 * 把HttpServletRequest Parameters 的值转换到HashMap中
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public static HashMap getParametersToHashMap(HttpServletRequest request) throws Exception {
		Assert.notNull(request, "Request must not be null");
		Enumeration paramEnum = request.getParameterNames();
		HashMap _paranMap = new HashMap();

		String _paramName = null;
		String[] _values = null;

		while(paramEnum.hasMoreElements()){
			
			_paramName = paramEnum.nextElement().toString();
			_values = request.getParameterValues(_paramName);
			
			if (_values == null || _values.length == 0) {
				// Do nothing, no values found at all.
			}
			else if(_values.length == 1 && !_values[0].equals("")){
				_paranMap.put(_paramName, _values[0]);
			}
			else if(_values.length > 1){
				_paranMap.put(_paramName, _values);
			}
		}
		return _paranMap;
	}
}
