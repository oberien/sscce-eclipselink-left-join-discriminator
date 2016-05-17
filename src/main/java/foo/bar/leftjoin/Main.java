package foo.bar.leftjoin;

import foo.bar.leftjoin.entities.SubClass;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("leftjoin");

        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();

        SubClass t = new SubClass("Sub1");
        em.persist(t);
        t = new SubClass("Sub2");
        em.persist(t);

        em.getTransaction().commit();

        // Queries with `LEFT OUTER JOIN ON` on subclasses with a discriminator column results in normal JOIN.
        // In this Example querying all `SubClass`es with a `LEFT OUTER JOIN ON` should result in the following tuples:
        // ```java
        // (Sub1, null),
        // (Sub2, null)
        // ```
        // Instead no row is returned, as the query is
        // ```sql
        // SELECT t0.ID, t0.DTYPE, t0.NAME, t1.ID, t1.DTYPE, t1.NAME
        // FROM SUPERCLASS t0
        //   LEFT OUTER JOIN SUPERCLASS t1
        //     ON (t1.NAME = ?)
        // WHERE (((t0.DTYPE = ?) AND (t1.DTYPE = ?)) AND (t1.DTYPE = ?))
        // bind => [unknown, Sub, Sub, Sub]
        // ```
        // Apart from there being too many `DTYPE`-Checks (which will be optimized by the db),
        //   the check on the Discriminator column is in the where-clause after all other checks.
        // As we only have `(Project, null)` tuples, the check `null.DTYPE == "Sub"` fails and all tuples are dropped.
        // Therefore the `LEFT OUTER JOIN ON` results in a normal `JOIN ON`
        //
        // Instead the discriminator-column check should only happen in the on clause:
        // ```sql
        // SELECT t0.ID, t0.DTYPE, t0.NAME, t1.ID, t1.DTYPE, t1.NAME
        // FROM SUPERCLASS t0
        //   LEFT OUTER JOIN SUPERCLASS t1
        //     ON (t1.NAME = ?) AND (t1.DTYPE = ?)
        // WHERE (t0.DTYPE = ?)
        // bind => [unknown, Sub, Sub]

        List<?> result = em.createQuery("SELECT sub1, sub2 from SubClass sub1 LEFT OUTER JOIN SubClass sub2 ON sub2.name = 'unknown'").getResultList();
        System.out.println("Result length: " + result.size());
    }
}
