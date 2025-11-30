package com.basssoft.arms.booking.service;

import com.basssoft.arms.account.domain.Account;
import com.basssoft.arms.account.domain.AccountDTO;
import com.basssoft.arms.account.repository.AccountRepository;
import com.basssoft.arms.account.service.AccountSvcImpl;
import com.basssoft.arms.booking.domain.Booking;
import com.basssoft.arms.booking.domain.BookingDTO;
import com.basssoft.arms.booking.repository.BookingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/** * Booking Service Implementation

 * arms application
 * @author Matthew Bass
 * @version 2.0
 */
@Service
@Transactional
public class BookingSvcImpl implements IbookingService {

    // dependencies
    private final BookingRepository repo;
    private final AccountSvcImpl accountService;
    private final AccountRepository accountRepository;
    private final ModelMapper mapper;

    // constructor
    // injection of dependencies
    // and ModelMapper config
    public BookingSvcImpl(BookingRepository repo,
                          AccountSvcImpl accountService,
                          ModelMapper mapper,
                          AccountRepository accountRepository) {
        this.repo = repo;
        this.accountService = accountService;
        this.mapper = mapper;
        this.accountRepository = accountRepository;

        // stop ModelMapper from mapping Account -> Integer
        this.mapper.createTypeMap(Booking.class, BookingDTO.class)
                .addMappings(m -> {
                    m.skip(BookingDTO::setCustomer);
                    m.skip(BookingDTO::setProvider);
                });

        // DTO -> Entity mapping also skips accounts (set manually in service methods)
        this.mapper.createTypeMap(BookingDTO.class, Booking.class)
                .addMappings(m -> {
                    m.skip(Booking::setCustomer);
                    m.skip(Booking::setProvider);
                });
    }




    /**
     * Create new Booking

     * @param booking BookingDTO
     * @return BookingDTO of created booking
     */
    public BookingDTO createBooking(BookingDTO booking) {

        // null check
        if (booking == null) {
            throw new IllegalArgumentException("BookingDTO must not be null");
        }
        // map DTO to entity
        Booking entity = mapper.map(booking, Booking.class);

        // get accounts for customer and provider
        if (booking.getCustomer() != null) {
            Account customer = accountRepository.findById(booking.getCustomer())
                    .orElseThrow(() -> new NoSuchElementException("Customer not found: " + booking.getCustomer()));
            entity.setCustomer(customer);
        }
        if (booking.getProvider() != null) {
            Account provider = accountRepository.findById(booking.getProvider())
                    .orElseThrow(() -> new NoSuchElementException("Provider not found: " + booking.getProvider()));
            entity.setProvider(provider);
        }
        // persist entity
        Booking saved = repo.save(entity);
        // map back to DTO
        BookingDTO result = mapper.map(saved, BookingDTO.class);
        result.setCustomer(saved.getCustomer() != null ? saved.getCustomer().getAccountId() : null);
        result.setProvider(saved.getProvider() != null ? saved.getProvider().getAccountId() : null);
        // return created DTO
        return result;
    }


    /**
     * Get Booking by ID

     * @param bookingId int
     * @return BookingDTO
     */
    public BookingDTO getBooking(int bookingId) {

        // get booking from repo
        Optional<Booking> opt = repo.findById(bookingId);
        if (opt.isEmpty()) {
            // not found
            throw new NoSuchElementException("Booking not found for id: " + bookingId);
        }
        Booking saved = opt.get();
        // convert to dto
        BookingDTO dto = mapper.map(saved, BookingDTO.class);
        //
        dto.setCustomer(saved.getCustomer() != null ? saved.getCustomer().getAccountId() : null);
        dto.setProvider(saved.getProvider() != null ? saved.getProvider().getAccountId() : null);
        // return dto
        return dto;
    }


    /**
     * Get all Bookings

     * @return List<BookingDTO>
     */
    public List<BookingDTO> getCustomerBookings(int customerId) {

        return repo.findByCustomer_AccountId(customerId)
                .stream()
                .map(saved -> {
                    BookingDTO dto = mapper.map(saved, BookingDTO.class);
                    dto.setCustomer(saved.getCustomer() != null ? saved.getCustomer().getAccountId() : null);
                    dto.setProvider(saved.getProvider() != null ? saved.getProvider().getAccountId() : null);
                    return dto;
                })
                .collect(java.util.stream.Collectors.toList());
    }


    /**
     * Update existing Booking

     * @param booking BookingDTO
     * @return BookingDTO
     */
    public BookingDTO updateBooking(BookingDTO booking) {

        // null check
        if (booking == null || booking.getBookingId() == null) {
            throw new IllegalArgumentException("BookingDTO and bookingId must not be null");
        }
        // check exists in repo
        Optional<Booking> opt = repo.findById(booking.getBookingId());
        if (opt.isEmpty()) {
            throw new NoSuchElementException("Booking not found for id: " + booking.getBookingId());
        }
        // get existing entity
        Booking entity = opt.get();
        // set fields from dto
        entity.setHourlyRate(booking.getHourlyRate());
        entity.setStartTime(booking.getStartTime());
        entity.setEndTime(booking.getEndTime());
        entity.setLocStreet(booking.getLocStreet());
        entity.setLocCity(booking.getLocCity());
        entity.setLocState(booking.getLocState());
        entity.setLocZipCode(booking.getLocZipCode());
        entity.setCompleted(booking.isCompleted());
        entity.setOverHours(booking.getOverHours());
        entity.setPaid(booking.isPaid());
        // get customer and provider accounts if they exist
        if (booking.getCustomer() != null) {
            AccountDTO customerDto = accountService.getAccount(booking.getCustomer());
            entity.setCustomer(customerDto != null ? mapper.map(customerDto, Account.class) : null);
        }
        if (booking.getProvider() != null) {
            AccountDTO providerDto = accountService.getAccount(booking.getProvider());
            entity.setProvider(providerDto != null ? mapper.map(providerDto, Account.class) : null);
        }
        // save updated account entity
        Booking saved = repo.save(entity);

        // map back to dto and return
        BookingDTO result = mapper.map(saved, BookingDTO.class);
        result.setCustomer(saved.getCustomer() != null ? saved.getCustomer().getAccountId() : null);
        result.setProvider(saved.getProvider() != null ? saved.getProvider().getAccountId() : null);
        return result;
    }


    /**
     * Delete Booking by ID

     * @param bookingId int
     * @return int deletedId
     */
    public int deleteBooking(int bookingId) {

        // check exists
        Optional<Booking> opt = repo.findById(bookingId);

        // if not found, return 0
        if (opt.isEmpty()) {
            throw new NoSuchElementException("Booking not found for id: " + bookingId);
        }
        repo.deleteById(bookingId);

        return bookingId;
    }

}
