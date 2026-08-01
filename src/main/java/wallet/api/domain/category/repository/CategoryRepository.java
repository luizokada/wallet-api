package wallet.api.domain.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wallet.api.domain.category.entity.Category;
import wallet.api.domain.transaction.entity.TransactionType;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, String> {
    List<Category> findAllByType(TransactionType type);
}
