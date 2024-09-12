package wallet.api.domain.expenseCategory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wallet.api.domain.expenseCategory.dtos.ExpenseCateogryBodyDTO;
import wallet.api.domain.expenseCategory.dtos.UpdateExpenseCategoryDTO;

@Table(name="expense_categories")
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
    String description;




    public ExpenseCategory(ExpenseCateogryBodyDTO dto) {
        this.name = dto.name();
        this.description = dto.description();
    }

    public void update(UpdateExpenseCategoryDTO dto) {
        if(dto.name() != null) {
            this.name = dto.name();
        }
        if(dto.description() != null) {
            this.description = dto.description();
        }
    }


}
