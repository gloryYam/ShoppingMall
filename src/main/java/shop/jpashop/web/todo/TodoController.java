package shop.jpashop.web.todo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TodoController {

    @GetMapping("/todo")
    public String home() {
        return "todo/toList";
    }
}
