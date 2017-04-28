package com.get.apm.api.ut;


import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ScanUnitTest {
  class User {
    int id;
    String name;

    public User(int id, String name) {
      this.id = id;
      this.name = name;
    }

    public int getId() {
      return id;
    }

    public void setId(int id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this).add("id", id).add("name", name).toString();
    }
  }

  private List<User> users;

  @Before
  public void setUp() {
    users = ImmutableList.of(new User(1, "user2"), new User(1, "user1"), new User(3, "user3"));
  }

  @Test
  public void testComparing() {
    //http://stackoverflow.com/questions/24436871/very-confused-by-java-8-comparator-type-inference
    System.out.println(users.stream().sorted(Comparator.<User, Integer>comparing(u -> u.getId()).thenComparing(u -> u.getName())).collect(Collectors.toList()));
  }
}
