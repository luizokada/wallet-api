package wallet.api.domain.expense.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wallet.api.domain.expense.dto.CreateExpenseDTO;
import wallet.api.domain.expense.dto.UpdateExpenseDTO;
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

    public void update(UpdateExpenseDTO payload, ExpenseCategory expenseCategory){
        if(payload.amount()!=null){
            this.amount = payload.amount();
        }
        if(payload.description()!=null){
            this.description = payload.description();
        }
        if(payload.expenseDate()!=null){
            this.expense_date = payload.expenseDate();
        }
        if(payload.categoryId()!=null){
            this.category = expenseCategory;
        }
    }

    public void toStr(){
        System.out.println("Expense: "+this.id);
        System.out.println("Amount: "+this.amount);
        System.out.println("Description: "+this.description);
        System.out.println("Category: "+this.categoryId);
        System.out.println("Wallet: "+this.walletId);
        System.out.println("Created At: "+this.createdAt);
        System.out.println("Expense Date: "+this.expense_date);
    }


}
