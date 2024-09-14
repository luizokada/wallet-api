package wallet.api.domain.expense.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wallet.api.domain.expense.dto.CreateExpenseDTO;
import wallet.api.domain.expenseCategory.entity.ExpenseCategory;
import wallet.api.domain.wallet.entity.Wallet;

import java.util.Date;

@Table(name = "expenses")
@Entity(name = "expense")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private int amount;
    private String description;


    @Column(name = "category_id",insertable=false, updatable=false)
    private String categoryId;

    @Column(name = "wallet_id",insertable=false, updatable=false)
    private String walletId;
    @Column(name = "created_at")
    private Date createdAt;
    @Column(name = "expense_date")
    private Date expense_date;

    @JsonManagedReference
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "category_id", nullable = false)
    private ExpenseCategory category;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    public Expense(Wallet wallet, ExpenseCategory expenseCategory,CreateExpenseDTO body){
        this.wallet = wallet;
        this.category = expenseCategory;
        this.expense_date = body.expenseDate();
        this.amount = body.amount();
        if(body.categoryId()!=null){
            this.categoryId = body.categoryId();
        }
        if(body.description()!=null){
            this.description = body.description();
        }


    }


}
