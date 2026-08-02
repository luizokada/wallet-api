package wallet.api.domain.transaction.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wallet.api.domain.category.entity.Category;
import wallet.api.domain.transaction.dto.CreateTransactionDTO;
import wallet.api.domain.transaction.dto.ImportTransactionItemDTO;
import wallet.api.domain.transaction.dto.UpdateTransactionDTO;
import wallet.api.domain.wallet.entity.Wallet;

import java.time.ZoneId;
import java.util.Date;

@Table(name = "transactions")
@Entity(name = "Transaction")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private int amount;
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TransactionType type;

    @Column(name = "category_id", insertable = false, updatable = false)
    private String categoryId;

    @Column(name = "wallet_id", insertable = false, updatable = false)
    private String walletId;
    @Column(name = "created_at")
    private Date createdAt;
    @Column(name = "expense_date")
    private Date date;

    @Column(name = "installment_number")
    private Integer installmentNumber;
    @Column(name = "installment_total")
    private Integer installmentTotal;

    @Column(name = "series_id")
    private String seriesId;

    @JsonManagedReference
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    public Transaction(Wallet wallet, Category category, CreateTransactionDTO body) {
        this.wallet = wallet;
        this.category = category;
        this.type = body.type();
        this.date = body.date();
        this.amount = body.amount();
        if (body.categoryId() != null) {
            this.categoryId = body.categoryId();
        }
        if (body.description() != null) {
            this.description = body.description();
        }

    }

    public Transaction(Wallet wallet, Category category, ImportTransactionItemDTO item) {
        this.wallet = wallet;
        this.category = category;
        this.type = item.type();
        this.date = Date.from(item.date().atStartOfDay(ZoneId.systemDefault()).toInstant());
        this.amount = item.amount();
        this.description = item.description();
        this.installmentNumber = item.installmentNumber();
        this.installmentTotal = item.installmentTotal();
        this.seriesId = item.seriesId();
        if (category != null) {
            this.categoryId = category.getId();
        }
    }

    public void update(UpdateTransactionDTO payload, Category category) {
        if (payload.type() != null) {
            this.type = payload.type();
        }
        if (payload.amount() != null) {
            this.amount = payload.amount();
        }
        if (payload.description() != null) {
            this.description = payload.description();
        }
        if (payload.date() != null) {
            this.date = payload.date();
        }
        if (payload.categoryId() != null) {
            this.category = category;
        }
    }

}
