package task;

import com.ge.apm.domain.UserAccount;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试  
@WebAppConfiguration
@ContextConfiguration({"classpath*:config/applicationContext.xml"})
public class BaseJunit4Test {  
	@Test
	public void init(){
		System.out.println("begin to test ...........");
		UserAccount x1 = new UserAccount();
		UserAccount x2 = new UserAccount();
		UserAccount x3 = new UserAccount();
x1.setId(1);
x2.setId(2);
x3.setId(4);
List<UserAccount> l1= new ArrayList<>();
		l1.add(x1);
		l1.add(x2);
		l1.add(x3);
		/*HashSet<UserAccount> hashSet = new HashSet<UserAccount>();
		hashSet.add(x1);
		hashSet.add(x2);
		hashSet.add(x3);
		System.out.println();*/

	List<UserAccount> userAccountList = new ArrayList<UserAccount>(new HashSet<UserAccount>(l1));
		System.out.println();
	}
} 
