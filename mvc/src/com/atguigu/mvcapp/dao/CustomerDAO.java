package com.atguigu.mvcapp.dao;

import java.util.List;

import com.atguigu.mvcapp.domain.Customer;

public interface CustomerDAO {

	public List<Customer> getForListWithCriteriaCustomer(CriteriaCustomer cc);
	public List<Customer> getAll();
	
	public void save(Customer customer);
	
	public Customer get(Integer id);
	
	public void delete(Integer id);
	
	public void update(Customer customer);
	
	/**
	 *  杩斿洖鍜� name 鐩哥瓑鐨勮褰曟暟
	 * @param name
	 * @return
	 */
	public long getCountWithName(String name);
	
}
