package com.atguigu.mvcapp.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atguigu.mvcapp.CustomerDAOImpl.CustomerDAOJdbcImpl;
import com.atguigu.mvcapp.dao.CriteriaCustomer;
import com.atguigu.mvcapp.dao.CustomerDAO;
import com.atguigu.mvcapp.domain.Customer;


/**
 * Servlet implementation class CustomerServlet
 */
public class CustomerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private CustomerDAO customerDAO = new CustomerDAOJdbcImpl();
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
/*	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String method = request.getParameter("method");
		
		switch (method) {
			case "add": add(request,response);break;
			case "query": query(request,response);break;
			case "delete": delete(request,response);break;
		}
		
	}*/

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		//1. 鑾峰彇 ServletPath: /edit.do 鎴� /addCustomer.do
		String servletPath = request.getServletPath();
		//2. 鍘婚櫎 / 鍜� .do, 寰楀埌绫讳技浜� edit 鎴� addCustomer 杩欐牱鐨勫瓧绗︿覆
		String methodName = servletPath.substring(1, servletPath.length() - 3);
		
		
		
			try {
				//3. 鍒╃敤鍙嶅皠鑾峰彇 methodName 瀵瑰簲鐨勬柟娉�
				Method method = getClass().getDeclaredMethod(methodName, HttpServletRequest.class,HttpServletResponse.class);
				//3. 鍒╃敤鍙嶅皠璋冪敤瀵瑰簲鐨勬柟娉�
				method.invoke(this, request,response);
				
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// 鍙互鏈変竴浜涘搷搴�.
				response.sendRedirect("error.jsp");
			}
		 
		
		
	}
	
	
	private void delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String idStr = request.getParameter("id");
		
		int id = 0;
		// try ... catch 鐨勪綔鐢�: 闃叉 idStr 涓嶈兘杞负 int 绫诲瀷
		// 鑻ヤ笉鑳借浆鍒� id = 0,鏃犳硶杩涜浠讳綍鍒犻櫎鎿嶄綔.
		try {
			id = Integer.parseInt(idStr);
			customerDAO.delete(id);
		} catch (Exception e) {}
		
		response.sendRedirect("query.do");
		
	}

	private void query(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// 鑾峰彇妯＄硦鏌ヨ鐨勮姹傚弬鏁�
		String name = request.getParameter("name");
		String address = request.getParameter("address");
		String phone = request.getParameter("phone");
		// 鎶婅姹傚弬鏁板皝瑁呬负涓�涓� CriteriaCustomer 瀵硅薄
		CriteriaCustomer cc = new CriteriaCustomer(name, address, phone);
		
		//1. 璋冪敤 CustomerDAO 鐨� getForListWithCriteriaCustomer 寰楀埌 Customer 鐨勯泦鍚�.
		List<Customer> customers = customerDAO.getForListWithCriteriaCustomer(cc);
		
		//2. 鎶� Customer 鐨勯泦鍚堟斁鍏� request 涓�.
		request.setAttribute("customers", customers);
		
		//3. 杞彂椤甸潰鍒� index.jsp(涓嶈兘浣跨敤閲嶅畾鍚�)
		request.getRequestDispatcher("index.jsp").forward(request, response);
		
		
	}

	private void addCustomer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//1. 获取表单参数: name，address，phone
		String name = request.getParameter("name");
		String address = request.getParameter("address");
		String phone = request.getParameter("phone");	
		
		//2. 检验 name 是否已经被占用:
		//2.1 调用 CustomerDAO 的 getCountWithName(String name) 获取 name 在数据库中是否存在
		long count = new CustomerDAOJdbcImpl().getCountWithName(name);

		//2.2 若返回值大于 0 ，则相应 newcustomer.jsp 页面:
		//通过转发的方式来响应 newcustomer.jsp
		
		if(count > 0) {
			//2.2.1 要求在 newcustomer.jsp 页面 显示一个错误消息: 用户名 name 已经被占用，请重新选择。
			// 在request 中放入一个属性 message，在页面上通过 request.getAttrubute("message") 的方式来显示。
			request.setAttribute("message", "用户名" + name + "已经被占用，请重新选择");
			
			//2.2.2 newcustomer.jsp 的表单值可以回显。
			
			//2.2.3 结束方法 return
			request.getRequestDispatcher("/newcustomer.jsp").forward(request, response);
			return;
		}//else {
			//3. 若验证通过，把表单参数封装为一个 Customer 对象customer
			Customer customer = new Customer(name, address, phone);
			
			//4. 调用 CustomerDAO 的 save(Customer,customer) 方法执行保存操作。
			new CustomerDAOJdbcImpl().save(customer);
			//5. 重定向到 success.jsp 页面。使用重定向可以避免表单的重复提交问题。
			response.sendRedirect("success.jsp");
		//}
	}
	
	private void edit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String forwardPath = "/error.jsp";
		//1. 获取请求参数 id
		String idStr = request.getParameter("id");
		// 防止手工修改为不存在的 id
		int id = -1;
		try {
			//2. 调用 customerDAO 的 customerDAO.get(id) 获取和 id 对应的 Customer 对象 customer。
			Customer customer = customerDAO.get(Integer.parseInt(idStr));
			if(customer != null) {
				forwardPath = "/updatecustomer.jsp";
				//3. 将 customer 放入 request 中。
				request.setAttribute("customer", customer);
			}
		
		} catch (Exception e) {}
		
		//4. 响应 updatecustomer.jsp 页面:转发。
		request.getRequestDispatcher(forwardPath).forward(request, response);
		
	}
	
	private void update(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//1. 获取表单参数: id,name，address，phone,oldName
		String id = request.getParameter("id");
		String name = request.getParameter("name");
		String address = request.getParameter("address");
		String phone = request.getParameter("phone");
		String oldName = request.getParameter("oldName");
		
		
		//2. 检验 name 是否已经被占用:
		//2.1 比较 name 和 oldName 是否相同，若相同说明 name 可用；
		if(!oldName.equalsIgnoreCase(name)) {
			long count = customerDAO.getCountWithName(name);
			//2.3 若返回值大于 0 ，则响应 updatecustomer.jsp 页面:通过转发的方式来响应 newcustomer.jsp
			if(count > 0) {
				//2.3.1 在 updatecustomer.jsp 页面显示一个错误消息:用户名 name 已经被占用，请重新选择。
				// 在request 中放入一个属性 message，在页面上通过 request.getAttrubute("message") 的方式来显示。
				request.setAttribute("message", "用户名" + name + "已经被占用，请重新选择");
				//2.3.2 newcustomer.jsp 的表单值可以回显。
				// address,phone 显示提交表单的新的值，而 name 显示 oldName，而不是新提交的 name
				//2.3.3 结束方法 return
				request.getRequestDispatcher("/updatecustomer.jsp").forward(request, response);
				return;
			}
		}

		//3. 若验证通过，把表单参数封装为一个 Customer 对象customer
		Customer customer = new Customer(name, address, phone);
		customer.setId(Integer.parseInt(id));
		//4. 调用 CustomerDAO 的 update(Customer,customer) 方法执行更新操作。
		customerDAO.update(customer);
		//5. 重定向到 query.do
		response.sendRedirect("query.do");
	}

}
