package com.ge.apm.web;

import com.github.davidmoten.rx.jdbc.ConnectionProvider;
import com.github.davidmoten.rx.jdbc.Database;
import org.springframework.web.bind.annotation.*;
import webapp.framework.web.service.UserContext;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {
  @Resource(name = "rxJdbcConnectionProvider")
  private ConnectionProvider rxJdbhtcConnectionProvider;

  @RequestMapping(value = "/self", method = RequestMethod.GET)
  @ResponseBody
  public String currentUser() {
    return UserContext.getCurrentLoginUser().getName();
  }

  @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
  @ResponseBody
  public String getUserById(@PathVariable int userId) {
    return Database.from(rxJdbhtcConnectionProvider)
      .select("select name from user_account where id = :id")
      .parameter("id", userId)
      .getAs(String.class)
      .first()
      .toBlocking()
      .single();
  }
}
