package wallet.api.domain.expenseCategory.entity;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wallet.api.domain.expense.entity.Expense;
import wallet.api.domain.expenseCategory.dtos.ExpenseCateogryBodyDTO;
import wallet.api.domain.expenseCategory.dtos.UpdateExpenseCategoryDTO;

@Table(name="expense_categories")
@Entity(name="expenseCategory")
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
public class ExpenseCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private String description;

    @JsonManagedReference
    @OneToMany(mappedBy = "category",  cascade={ CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
    private Set<Expense> expenses  = new HashSet<>();;



    public ExpenseCategory(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
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
