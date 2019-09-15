package com.atguigu.mvcapp.CustomerDAOImpl;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.atguigu.mvcapp.dao.CriteriaCustomer;
import com.atguigu.mvcapp.dao.CustomerDAO;
import com.atguigu.mvcapp.dao.DAO;
import com.atguigu.mvcapp.domain.Customer;

public class CustomerDAOJdbcImpl extends DAO<Customer> implements CustomerDAO{

	public List<Customer> getAll() {
		String sql = "SELECT id,name,address,phone FROM customersnew";
		return getForList(sql);
	}

	public void save(Customer customer) {
		String sql = "INSERT customersnew(name,address,phone) VALUES(?,?,?)";
		update(sql, customer.getName(),customer.getAddress(),customer.getPhone());
	}

	public Customer get(Integer id) {
		String sql = "SELECT id,name,address,phone FROM customersnew WHERE id = ?";
		return get(sql, id);
	}

	public void delete(Integer id) {
		String sql = "DELETE FROM customersnew WHERE id = ?";
		update(sql, id);
	}

	public long getCountWithName(String name) {
		String nameString = null;
		try {
			nameString = new String(name.getBytes("ISO-8859-1"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String sql = "SELECT COUNT(id)  FROM customersnew WHERE name = ?";
		System.out.println(nameString);
		System.out.println(sql);
		return getForValue(sql, nameString);
	}

	@Override
	public List<Customer> getForListWithCriteriaCustomer(CriteriaCustomer cc) {
		
		String sql = "SELECT id,name,address,phone FROM customersnew WHERE "
				+ "name LIKE ? AND address LIKE ? AND phone LIKE ?";
		
		// 淇敼浜� CriteriaCustomer 鐨� getter 鏂规硶,浣垮叾杩斿洖鐨勫瓧绗︿覆涓湁 "%%"
		// 鑻ヨ繑鍥炲�间负 null 鍒欒繑鍥� "%%",鑻ヤ笉涓� null, 鍒欒繑鍥�  "%"+ 瀛楁鏈韩鐨勫�� +"%"
		return getForList(sql,cc.getName(),cc.getAddress(),cc.getPhone());
	}

	@Override
	public void update(Customer customer) {
		String sql = "UPDATE customersnew SET name = ? ,address = ? , phone = ? WHERE id = ?";
		update(sql, customer.getName(),customer.getAddress(),customer.getPhone(),customer.getId());
	}

}
