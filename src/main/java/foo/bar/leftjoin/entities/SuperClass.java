package foo.bar.leftjoin.entities;

import javax.persistence.*;

@Entity
@Table
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn
public abstract class SuperClass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;

    public SuperClass() {
    }

    public SuperClass(String name) {
        this.name = name;
    }
}
