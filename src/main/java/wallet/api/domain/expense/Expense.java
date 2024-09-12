package wallet.api.domain.expense;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "expenses")
@Entity(name = "expense")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    int amount;
    String description;
    @Column(name = "category_id")
    String categoryId;
    @Column(name = "walletId")
    String walletId;



}
