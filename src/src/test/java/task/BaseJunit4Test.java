package task;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试  
@WebAppConfiguration
@ContextConfiguration({"classpath*:config/applicationContext.xml"})
public class BaseJunit4Test {  
	@Test
	public void init(){
		System.out.println("begin to test ...........");
	}
} 
