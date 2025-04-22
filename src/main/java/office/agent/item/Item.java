package office.agent.item;

import jakarta.persistence.*;
import office.agent.global.BaseTimeEntity;

//@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DTYPE")
public abstract class Item extends BaseTimeEntity {
//    @Id
    private Long id;
}
