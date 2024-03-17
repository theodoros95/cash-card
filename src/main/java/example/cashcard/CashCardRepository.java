package example.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

interface CashCardRepository extends CrudRepository<CashCard, Long>, PagingAndSortingRepository<CashCard, Long> {
    CashCard findByIdAndOwner(final Long id, final String owner);

    Page<CashCard> findByOwner(final String owner, final PageRequest pageRequest);

    boolean existsByIdAndOwner(final Long id, final String owner);
}
