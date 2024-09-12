package wallet.api.domain.expenseCategory;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name="expense_cateogories")
@Entity(name="expenseCategory")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
public class ExpenseCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String name;
}
