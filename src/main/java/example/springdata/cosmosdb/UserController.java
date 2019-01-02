package example.springdata.cosmosdb;

import com.microsoft.azure.spring.data.cosmosdb.core.query.DocumentDbPageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserRepository repository;

    @RequestMapping("/adduser")
    public ResponseEntity addUser(){
        cleanup();
        setup();
        print();

        return ResponseEntity.ok(sb.toString());
    }


    private static final String ID_1 = "id_1";
    private static final String NAME_1 = "vinod";

    private static final String ID_2 = "id_2";
    private static final String NAME_2 = "rav";

    private static final String ID_3 = "id_3";
    private static final String NAME_3 = "ami";

    private static final String EMAIL = "xxx-xx@test.com";
    private static final String POSTAL_CODE = "0123456789";
    private static final String STREET = "sarjapur road";
    private static final String CITY = "Bangalore";
    private static final String ROLE_CREATOR = "creator";
    private static final String ROLE_CONTRIBUTOR = "contributor";
    private static final int COST_CREATOR = 234;
    private static final int COST_CONTRIBUTOR = 666;
    private static final Long COUNT = 123L;

    private final Address address = new Address(POSTAL_CODE, STREET, CITY);
    private final Role creator = new Role(ROLE_CREATOR, COST_CREATOR);
    private final Role contributor = new Role(ROLE_CONTRIBUTOR, COST_CONTRIBUTOR);
    private final User user_1 = new User(ID_1, EMAIL, NAME_1, COUNT, address, Arrays.asList(creator, contributor));
    private final User user_2 = new User(ID_2, EMAIL, NAME_2, COUNT, address, Arrays.asList(creator, contributor));
    private final User user_3 = new User(ID_3, EMAIL, NAME_3, COUNT, address, Arrays.asList(creator, contributor));


    public void print() {
        printList(this.repository.findByEmailOrName(this.user_1.getEmail(), this.user_1.getName()));

        printList(this.repository.findByCount(COUNT, Sort.by(new Sort.Order(Sort.Direction.ASC, "count"))));

        printList(this.repository.findByNameIn(Arrays.asList(this.user_1.getName(), "fake-name")));

        queryByPageable();
    }

    private void queryByPageable() {
        final int pageSize = 2;
        final Pageable pageable = new DocumentDbPageRequest(0, pageSize, null);
        final Page<User> page = this.repository.findByAddress(address, pageable);
        System.out.println("***** Printing Page 1 *****");
        printList(page.getContent());

        final Page<User> nextPage = this.repository.findByAddress(address, page.getPageable());
        System.out.println("***** Printing Page 2 *****");
        printList(nextPage.getContent());
    }

    public void setup() {
        this.repository.save(user_1);
        this.repository.save(user_2);
        this.repository.save(user_3);
    }

    public void cleanup() {
        sb = new StringBuilder();
        this.repository.deleteAll();
    }

    StringBuilder sb;

    private void printList(List<User> users) {
        users.forEach(user -> sb.append(user));
    }
}
