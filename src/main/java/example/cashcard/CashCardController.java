package example.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/cashcards")
class CashCardController {

    private final CashCardRepository cashCardRepository;

    private CashCardController(final CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<CashCard> findById(@PathVariable final Long requestedId, final Principal principal) {

        final CashCard cashCard = findCashCard(requestedId, principal);

        return cashCard != null ? ResponseEntity.ok(cashCard) : ResponseEntity.notFound().build();
    }

    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody final CashCard newCashCardRequest,
                                                final UriComponentsBuilder ucb,
                                                final Principal principal) {

        final CashCard cashCardWithOwner = new CashCard(null, newCashCardRequest.amount(), principal.getName());
        final CashCard savedCashCard = cashCardRepository.save(cashCardWithOwner);

        final URI locationOfNewCashCard = ucb.path("cashcards/{id}").buildAndExpand(savedCashCard.id()).toUri();

        return ResponseEntity.created(locationOfNewCashCard).build();
    }

    @GetMapping
    private ResponseEntity<List<CashCard>> findAll(final Pageable pageable, final Principal principal) {

        final Page<CashCard> page = cashCardRepository.findByOwner(principal.getName(),
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
                ));

        return ResponseEntity.ok(page.getContent());
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putCashCard(@PathVariable final Long requestedId,
                                             @RequestBody final CashCard cashCardUpdate,
                                             final Principal principal) {

        final CashCard cashCard = findCashCard(requestedId, principal);

        if (cashCard == null) return ResponseEntity.notFound().build();

        final CashCard updatedCashCard = new CashCard(requestedId, cashCardUpdate.amount(), principal.getName());
        cashCardRepository.save(updatedCashCard);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteCashCard(@PathVariable final Long id, final Principal principal) {

        if (!cashCardRepository.existsByIdAndOwner(id, principal.getName())) {
            return ResponseEntity.notFound().build();
        }

        cashCardRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    private CashCard findCashCard(final Long requestedId, final Principal principal) {
        return cashCardRepository.findByIdAndOwner(requestedId, principal.getName());
    }
}
