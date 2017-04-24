package task;

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
/**
 * Created by lsg on 7/3/2017.
 */
class Person{
    Long id;
    String name;

    public Person(Long id ,String name) {
        this.id=id;
        this.name=name;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
        return "person[ name=" + name + " ]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (!id.equals(person.id)) return false;
        return name.equals(person.name);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
public class MyTest extends BaseJunit4Test{
    @Test
    @Transactional
    public void removeDuplicationByProperty()throws Exception{
        Person p1 = new Person(1l, "jack");
        Person p2 = new Person(3l, "jack");
        Person p3 = new Person(2l, "tom");
        Person p4 = new Person(4l, "hanson");
        Person p5 = new Person(5l, "胶布虫");

        List<Person> persons = Arrays.asList(p1, p2, p3, p4, p5, p5, p1, p2, p2);
        persons.stream().filter(distinctByKey(p -> p.getName())).forEach(p -> System.out.println(p));
        persons=  persons.stream().filter(distinctByKey(p -> p.getName())).collect(toList());
       System.out.println("---->"+persons);
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}





