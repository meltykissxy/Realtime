import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class JavaTest {
    @Test
    public void run01() {
        List<Human> list = new ArrayList<Human>();
        list.add(new Human("lss", 18));
        list.add(new Human("ly", 16));
        list.add(new Human("yjy", 17));

        // 如果没有重写equals方法，下面这句代码是false的
        System.out.println(list.contains(new Human("ly", 16)));
    }
}

class Human {
    String name;
    int age;

    public Human(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Human human = (Human) o;

        if (age != human.age) return false;
        return name != null ? name.equals(human.name) : human.name == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + age;
        return result;
    }
}
