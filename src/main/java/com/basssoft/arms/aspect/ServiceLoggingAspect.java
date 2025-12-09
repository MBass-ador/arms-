package com.basssoft.arms.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Method;
import java.time.Instant;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import static com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter.serializeAllExcept;

/**
 * Aspect for logging service layer operations
 *
 * arms application
 * @author Matthew Bass
 * @version 6.0
 */
@Aspect
@Component
public class ServiceLoggingAspect {

    // get SLF4J loggers
    private static final Logger transactionLogger = LoggerFactory.getLogger("com.basssoft.arms.transaction");
    private static final Logger accessLogger = LoggerFactory.getLogger("com.basssoft.arms.access");
    private static final Logger securityLogger = LoggerFactory.getLogger("com.basssoft.arms.security");

    // Jackson ObjectMapper for JSON serialization (with JavaTimeModule conversion)
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    /** \@AfterReturning advice
     *  logs all public service layer create** methods (currently only createBooking() )
     *  logs: method name, created object ID, and timestamp to transaction log.

     * @param joinPoint JoinPoint
     * @param result Object created
     */
    @AfterReturning(
            pointcut = "execution(public * com.basssoft.arms..service.*.create*(..))",
            returning = "result"
    )
    public void logCreate(JoinPoint joinPoint, Object result) {

        // null check
        if (result == null) return;

        // attempt to get ID via common getter names
        Object id = null;
        try {
            for (String getter : new String[]{"getBookingId", "getAccountId", "getInvoiceId", "getId"}) {
                Method method = result.getClass().getMethod(getter);
                id = method.invoke(result);
                if (id != null) break;
            }
        } catch (Exception ignored) {}

        // set method name and timestamp
        String method = joinPoint.getSignature().toShortString();
        String timestamp = Instant.now().toString();

        // handle dto values
        String json;
        try {
            // omit providerName and customerName, pretty-print
            ObjectWriter writer = objectMapper
                    .writer(new SimpleFilterProvider()
                            .addFilter("omitNames", serializeAllExcept("providerName", "customerName")));
            json = writer.writeValueAsString(result);

            // insert line breaks for readable 4-line log entry
            json = json.replace(",\"locStreet\":", ",\n    \"locStreet\":");
        } catch (Exception e) {
            json = "Could not serialize object: " + e.getMessage();
        }
        // log creation
        transactionLogger.info("Created: {} id={} at {}\n    object:{}", method, id, timestamp, json);
    }




    /** \@AfterReturning advice
     *  logs access to all public service layer get* methods (including GetAll methods)
     *  logs: method name, accessed object ID, and timestamp to access log

     * @param joinPoint JoinPoint
     */
    @AfterReturning(
            pointcut = "execution(public * com.basssoft.arms..service.*.get*(..))",
            returning = "result"
    )
    public void logAccess(JoinPoint joinPoint, Object result) {

        // null check
        if (result == null) return;

        // get ID via common getter names
        Object id = null;
        try {
            // common ID getter names (including generic fallback)
            for (String getter : new String[]{"getAccountId", "getBookingId", "getInvoiceId", "getId"}) {
                Method method = result.getClass().getMethod(getter);
                id = method.invoke(result);
                if (id != null) break;
            }
        } catch (Exception ignored) {}

        // set method name and timestamp
        String method = joinPoint.getSignature().toShortString();
        String timestamp = Instant.now().toString();

        // log access
        accessLogger.info("Accessed: {} id={} at {}", method, id, timestamp);
    }




    /** \@AfterReturning advice
     *  logs all public service layer update** methods (currently updateAccount(), updateBooking() )
     *  logs: method name, updated object ID, and timestamp to transaction log

     * @param joinPoint JoinPoint
     * @param result Object updated
     */
    @AfterReturning(
            pointcut = "execution(public * com.basssoft.arms..service.*.update*(..))",
            returning = "result"
    )
    public void logUpdate(JoinPoint joinPoint, Object result) {

        // null check
        if (result == null) return;

        // get ID via common getter names
        Object id = null;
        try {
            for (String getter : new String[]{"getBookingId", "getAccountId", "getInvoiceId", "getId"}) {
                Method method = result.getClass().getMethod(getter);
                id = method.invoke(result);
                if (id != null) break;
            }
        } catch (Exception ignored) {}

        // set method name and timestamp
        String method = joinPoint.getSignature().toShortString();
        String timestamp = Instant.now().toString();

        // handle dto values
        String json;
        try {
            ObjectWriter writer = objectMapper
                    .writer(new SimpleFilterProvider()
                            .addFilter("omitNames", serializeAllExcept("providerName", "customerName")));

            json = writer.writeValueAsString(result);

            // add line break for BookingDTO and AccountDTO separately
            String className = result.getClass().getSimpleName();

            if (className.equals("BookingDTO")) {
                json = json.replace(",\"locStreet\":", ",\n    \"locStreet\":");

            } else if (className.equals("AccountDTO")) {
                json = json.replace(",\"street\":", ",\n    \"street\":");
            }
        } catch (Exception e) {
            json = "Could not serialize object: " + e.getMessage();
        }
        // log update
        transactionLogger.info("Updated: {} id={} at {}\n    object:{}", method, id, timestamp, json);
    }



    /** \@AfterReturning advice
     *  logs all public service layer delete** methods (currently only deleteBooking() )
     *  logs: method name, deleted object ID, and timestamp to transaction log

     * @param joinPoint JoinPoint
     * @param result Object deleted
     */
    @AfterReturning(
            pointcut = "execution(public * com.basssoft.arms..service.*.delete*(..))",
            returning = "result"
    )
    public void logDelete(JoinPoint joinPoint, Object result) {
        // get ID from first argument (usually the ID to delete)
        Object id = null;
        if (joinPoint.getArgs().length > 0) {
            id = joinPoint.getArgs()[0];
        }

        String method = joinPoint.getSignature().toShortString();
        String timestamp = java.time.Instant.now().toString();

        // log deletion
        transactionLogger.info("Deleted: {} id={} at {}", method, id, timestamp);
    }



    /** \@AfterReturning advice
     *  logs all public service layer generateInvoice methods
     *  logs: method name, providerId, customerId, amount billed, and timestamp to transaction log

     * @param joinPoint JoinPoint
     * @param result Object result (Mono)
     */
    @AfterReturning(
            pointcut = "execution(public reactor.core.publisher.Mono com.basssoft.arms.invoice.service.InvoiceFactory.generateInvoice(..))",
            returning = "result"
    )
    public void logGenerateInvoice(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Integer providerId = args.length > 0 ? (Integer) args[0] : null;
        Integer customerId = args.length > 1 ? (Integer) args[1] : null;
        String method = joinPoint.getSignature().toShortString();
        String timestamp = Instant.now().toString();

        if (result instanceof reactor.core.publisher.Mono) {
            ((reactor.core.publisher.Mono<?>) result)
                    .doOnNext(invoice -> {
                        Object total = null;
                        try {
                            total = invoice.getClass().getMethod("getTotalAmountDue").invoke(invoice);
                        } catch (Exception e) {
                            total = "N/A";
                        }
                        transactionLogger.info(
                                "Generated Invoice: providerId={} customerId={} totalAmountDue={}\n",
                                providerId, customerId, total
                        );
                    })
                    .subscribe();
        }
    }



    /** \@AfterReturning advice
     *  logs all public service layer methods in security service package
     *  logs: method name, arguments, and timestamp to security log

     * @param joinPoint JoinPoint
     * @param result Object result
     */
    @AfterReturning(
            pointcut = "execution(public * com.basssoft.arms.security.service.*.*(..))",
            returning = "result"
    )
    public void logSecurityServiceEvent(JoinPoint joinPoint, Object result) {
        String method = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        String timestamp = java.time.Instant.now().toString();
        securityLogger.info("SecurityService: {} called at {} with args={}", method, timestamp, args);
    }











}
