package shop.jpashop.repository.item;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.jpashop.domain.item.entity.Item;

public interface ItemJpaRepository extends JpaRepository<Item, Long>, ItemCustom {

}
