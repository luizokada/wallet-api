package wallet.api.domain.wallet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wallet.api.domain.transaction.entity.Transaction;
import wallet.api.domain.user.entity.User;

import java.util.HashSet;
import java.util.Set;

@Table(name = "wallets")
@Entity(name = "wallet")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Getter
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "user_id",insertable=false, updatable=false)
    private String userId;

    // coluna legada: o saldo agora é derivado das transações (ver WalletRepository.calculateBalance)
    @Column(name = "balance")
    private Integer balance;

    @OneToMany(mappedBy = "wallet",  cascade={ CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
    private Set<Transaction> transactions  = new HashSet<>();

    @OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;


    public Wallet(User user) {

        this.user = user;
        this.balance = 0;
    }

}
