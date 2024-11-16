package com.task.library.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aspect for logging execution of service methods and measuring performance metrics.
 */
@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Pointcut that matches book addition and update methods in BookService.
     */
    @Pointcut("execution(* com.task.library.service.BookService.createBook(..)) || " +
            "execution(* com.task.library.service.BookService.updateBook(..))")
    public void bookModificationMethods() {}

    /**
     * Pointcut that matches patron transaction methods in BorrowingService.
     */
    @Pointcut("execution(* com.task.library.service.BorrowingService.borrowBook(..)) || " +
            "execution(* com.task.library.service.BorrowingService.returnBook(..))")
    public void patronTransactionMethods() {}

    /**
     * Around advice that logs method execution time for book modifications.
     */
    @Around("bookModificationMethods()")
    public Object logBookModification(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethodExecutionTime(joinPoint);
    }

    /**
     * Around advice that logs method execution time for patron transactions.
     */
    @Around("patronTransactionMethods()")
    public Object logPatronTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethodExecutionTime(joinPoint);
    }

    /**
     * Utility method to log method execution time.
     */
    private Object logMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        logger.info("Entering method: {} with arguments: {}", methodName, joinPoint.getArgs());

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();

            long timeTaken = System.currentTimeMillis() - startTime;
            logger.info("Exiting method: {} with result: {}. Time taken: {} ms", methodName, result, timeTaken);

            return result;
        } catch (Throwable throwable) {
            long timeTaken = System.currentTimeMillis() - startTime;
            logger.error("Exception in method: {} with message: {}. Time taken: {} ms", methodName, throwable.getMessage(), timeTaken);
            throw throwable;
        }
    }

    /**
     * Pointcut that matches all methods in service classes.
     */
    @Pointcut("within(com.task.library.service..*)")
    public void allServiceMethods() {}

    /**
     * AfterThrowing advice to log exceptions in service methods.
     */
    @AfterThrowing(pointcut = "allServiceMethods()", throwing = "e")
    public void logExceptions(JoinPoint joinPoint, Throwable e) {
        logger.error("Exception in method: {} with cause: {}", joinPoint.getSignature().toShortString(), e.getCause() != null ? e.getCause() : "NULL");
    }
}
