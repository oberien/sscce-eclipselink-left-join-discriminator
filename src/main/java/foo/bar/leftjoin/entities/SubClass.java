package foo.bar.leftjoin.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("Sub")
public class SubClass extends SuperClass {
    public SubClass() {
    }

    public SubClass(String name) {
        super(name);
    }
}
