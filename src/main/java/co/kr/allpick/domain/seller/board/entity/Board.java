// Board.java
package co.kr.allpick.domain.seller.board.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "boards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String sellerName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}