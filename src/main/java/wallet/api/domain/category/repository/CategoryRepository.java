package wallet.api.domain.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wallet.api.domain.category.entity.Category;
import wallet.api.domain.transaction.entity.TransactionType;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, String> {

    @Query("select c from Category c left join c.owner o where o is null or o.id = ?1")
    List<Category> findAllVisible(String userId);

    @Query("select c from Category c left join c.owner o where c.type = ?2 and (o is null or o.id = ?1)")
    List<Category> findAllVisibleByType(String userId, TransactionType type);

    @Query("select c from Category c left join c.owner o where c.id = ?2 and (o is null or o.id = ?1)")
    Optional<Category> findVisibleById(String userId, String id);

    @Query("select c from Category c left join c.owner o where c.id in ?2 and (o is null or o.id = ?1)")
    List<Category> findAllVisibleByIdIn(String userId, Collection<String> ids);
}
