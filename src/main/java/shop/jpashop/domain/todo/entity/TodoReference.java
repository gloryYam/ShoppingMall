package shop.jpashop.domain.todo.entity;

import lombok.*;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "todo_reference")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoReference {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "prev_todo_id")
    private TodoItem prevTodoItem;

    @ManyToOne
    @JoinColumn(name = "current_todo_id")
    private TodoItem currentTodoItem;
}