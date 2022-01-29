package com.bl.gson.Day33_JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import java.util.Scanner;

import model.DepartmentModel;
import model.EmployeePayrollModel;
import model.SalaryModel;

public class EmployeePayrollService {
	
	SqlQueries sql = new SqlQueries();
	
	static List<DepartmentModel> departmentList;
	
	public static void main(String[] args) {
		Connection con = DatabaseConnector.getConnection();
		EmployeePayrollService service = new EmployeePayrollService();
		
		Scanner sc = new Scanner(System.in);
		EmployeePayrollModel model = new EmployeePayrollModel();
	
		System.out.println("Enter the employee name :");
		model.setName(sc.nextLine());
		
		System.out.println("Enter the Gender:");
		model.setGender(sc.nextLine());
		System.out.println("Enter the joining date (yyyy-mm-dd) :");
		String dateIntStr = sc.nextLine();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
		
		try {
			model.setStart(sdf.parse(dateIntStr));
		} catch (ParseException e) {
			System.out.println("Entered date may be in wrong format, please try again");
			e.printStackTrace();
		}
		
		SalaryModel salary = new SalaryModel();
		System.out.println("Enter the basic pay:");
		salary.setBasic_pay(sc.nextDouble());
		
		System.out.println("Enter the employee Department :");
		departmentList = service.listDepartment(con);
		departmentList.forEach(dept ->
		{System.out.println(dept);});
		int dept_id = sc.nextInt();
		while(service.getselectedDepartment(dept_id) == null) {
			System.out.println("Invalid department id, please enter again.");
			dept_id = sc.nextInt();
		}
		model.setDepartment(service.getselectedDepartment(dept_id));
		sc.close();
		
		if (service.addEmployeeData(model, salary, con) == 1) {
			System.out.println("employee data has been saved");
		}else {
			System.out.println("Some issue is there, please try again later..");
		}
		final int count = 7;
		int choice = 0;
		while(choice != count) {
			System.out.println("choose any one option\n1. get Employee data"
					+ "\n2. get Salary details"
					+ "\n3.Add Employee Data \n4. Add Employee Salary"
					+ "\n5.Update Salary "
					+ "\n6.count Employees"
					+ "\n7.get Total salary");
			choice = sc.nextInt();
			switch(choice) {
			case 1:
//				String query = "select * from employee_tbl";
				service.getEmployeeList(con);
				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				break;
			case 5:
				break;
			case 6:
				break;
			case 7:
				break;
			default:
				break;
			}
		}
	}

	private DepartmentModel getselectedDepartment(int dept_id) {
		for (DepartmentModel departmentModel : departmentList) {
			if (departmentModel.getDept_id() == dept_id) {
				return departmentModel;
			}
		}
		return null;
	}
	
	private List<DepartmentModel> listDepartment(Connection con) {
		Statement st;
		List<DepartmentModel> departmentList = new ArrayList<>();
		
		try {
			st = con.createStatement();
			ResultSet rs = st.executeQuery("select * from department_tbl");
			while(rs.next()) {
				DepartmentModel dept = new DepartmentModel();
				dept.setDept_id(rs.getInt(1));
				dept.setDept_name(rs.getString(2));
				dept.setDescription(rs.getString(3));
				departmentList.add(dept);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return departmentList;
	}
	
	public void getEmployeeList(Connection con) {
		List<EmployeePayrollModel> employeeList = new ArrayList<>();
		
		Statement selectEmpStatement;
		try {
			
			selectEmpStatement = con.createStatement();
			ResultSet employeeResult = selectEmpStatement.executeQuery(sql.GET_EMPLOYEE_QUERY);
			
			while(employeeResult.next()) {
				EmployeePayrollModel model1 = new EmployeePayrollModel();
				model1.setEmp_id(employeeResult.getInt("emp_id"));
				model1.setName(employeeResult.getString("name"));
				DepartmentModel department = new DepartmentModel();
				department.setDept_id(employeeResult.getInt("dept_id"));
				
				model1.setDepartment(department);
				model1.setGender(employeeResult.getString("gender"));
				model1.setStart(employeeResult.getDate("start"));
				
				employeeList.add(model1);
				
				
			}
			employeeList.forEach(emp -> {
				System.out.println("Emp id :"+emp.getEmp_id());
				System.out.println("Emp name :" +emp.getName());
				System.out.println("Department id" + emp.getDepartment().getDept_id());
				System.out.println("Gender :"+ emp.getGender());
				System.out.println("Date of joining :"+emp.getStart());
				System.out.println("----------------------------------------------------------");
			});
			selectEmpStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public int updateSalary(double amount, String empName,Connection con) {
		int updateStatus = 0;
		
		try {
			PreparedStatement ps = con.prepareStatement(sql.UPDATE_EMPLOYEE_SALARY_QUERY);
			ps.setDouble(1, amount);
			ps.setString(2, empName);
			
			updateStatus = ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return updateStatus;
	}
	
	public void getEmployeeSalaryDetails(Connection con) {
		try {
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql.GET_EMP_SALARY_DETAILS_QUERY);
			while(rs.next()) {
				System.out.println("Emp id :"+rs.getInt("emp_id"));
				System.out.println("Emp Name : " + rs.getString("name"));
				System.out.println("Salary: " + rs.getString("basic_pay"));
				System.out.println("---------------------------------------------");	
			}
			st.close();
			rs.close();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	
	public int addEmployeeData(EmployeePayrollModel model, SalaryModel salary, Connection con) {
		int insertStatus = 0;
		try {
			con.setAutoCommit(false);
		} catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
		try {
			PreparedStatement ps = con.prepareStatement(sql.ADD_EMPLOYEE_QUERY, PreparedStatement.RETURN_GENERATED_KEYS);
			ps.setString(1, model.getName());
			ps.setInt(2, model.getDepartment().getDept_id());
			ps.setString(3, model.getGender());
			ps.setDate(4, new Date(model.getStart().getTime()));
			
			insertStatus = ps.executeUpdate();
			if(insertStatus == 1) {
				ResultSet rsNewEmp = ps.getGeneratedKeys();
				if(rsNewEmp.next()) {
					int emp_id = rsNewEmp.getInt(1);
					insertStatus = this.addSalaryDetails(emp_id, salary, con);
				}
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return insertStatus;
	}

	private int addSalaryDetails(int emp_id, SalaryModel salary, Connection con) {
		int insertSalaryStatus = 0;
		
		salary.setDeduction(0.2 * salary.getBasic_pay());
		salary.setTaxable_pay(salary.getBasic_pay() - salary.getDeduction());
		salary.setTax(salary.getTaxable_pay() - salary.getTax());
		salary.setEmp_id(emp_id);
		
		try {
			PreparedStatement ps = con.prepareStatement(sql.ADD_SALARY_QUERY);
			
			ps.setDouble(1, salary.getBasic_pay());
			ps.setDouble(2, salary.getDeduction());
			ps.setDouble(3, salary.getTaxable_pay());
			ps.setDouble(4, salary.getTax());
			ps.setDouble(5, salary.getNet_pay());
			ps.setInt(6, salary.getEmp_id());
			
			insertSalaryStatus = ps.executeUpdate();
			if(insertSalaryStatus == 1) {
				con.commit();
			}
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return insertSalaryStatus;
	}
	
	public void countEmployee(Connection con) {
		int maleEmpCount = 0;
		int femaleEmpCount = 0;
		
		try {
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select gender, count(*) from employee_tbl group by gender");
			 while(rs.next()) {
				 if (rs.getString("gender").equals("M")) {
					maleEmpCount = rs.getInt("count(*)");
				} else {
					femaleEmpCount = rs.getInt("count(*)");
				}
			 }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Male employee are : "+ maleEmpCount);
		System.out.println("Female employee are :"+ femaleEmpCount);
	}
	
	public void EmployeeTotalSalary(Connection con) {
		int maleEmpTotal = 0, femaleEmpTotal = 0;
		try {
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(
					"select gender,sum(basic_pay) from employee_tbl group by gender");
			while (rs.next()) {
				if (rs.getString("gender").equals("M")) {
					maleEmpTotal = rs.getInt("sum(basic_pay)");
				} else {
					femaleEmpTotal = rs.getInt("sum(basic_pay)");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Male employees Total pay : " + maleEmpTotal);
		System.out
				.println("Female employees Total pay : " + femaleEmpTotal);
	}
	
	public void EmployeesAvg(Connection con) {
		int maleEmpAvg = 0, femaleEmpAvg = 0;
		try {
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(
					"select gender,Avg(*) from employee_tbl group by gender");
			while (rs.next()) {
				if (rs.getString("gender").equals("M")) {
					maleEmpAvg = rs.getInt("avg(*)");
				} else {
					femaleEmpAvg = rs.getInt("avg(*)");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Avg Male employees are : " + maleEmpAvg);
		System.out
				.println("Avg Female employees are : " + femaleEmpAvg);
	}
	
	
	public void getTotalSalary(Connection con) {
		long totalSalary = 0;
		
		try {
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("Select sum(basic_pay) from salary_tbl");
			
			while(rs.next()) {
				totalSalary = rs.getLong(1);
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		System.out.println("Total Salary paid by organisation :" +totalSalary);
	}
	
	public int deleteEmployee(String name, Connection con) {
		int status = 0;
		
		try {
			Statement st = con.createStatement();
			
			status = st.executeUpdate("Delete from employee_tbl where name = ' " + name +"'"); 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}

	

}
