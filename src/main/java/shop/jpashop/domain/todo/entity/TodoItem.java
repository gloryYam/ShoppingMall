package shop.jpashop.domain.todo.entity;

import static javax.persistence.GenerationType.IDENTITY;


import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.*;

import lombok.*;

@Entity
@Table(name = "todo_item")
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodoItem {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(length = 1, columnDefinition = "int default 0")
    private Integer isChecked;

    @Enumerated(EnumType.STRING)
    @Column(length = 4, nullable = false)
    private Status status;

    @Column
    private LocalDateTime regDate;

    @Column
    private LocalDateTime modDate;

    @Transient
    private List<Long> prevTodoIds;
}