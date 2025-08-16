package io.oneinch.mcp.monitoring;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

/**
 * Interceptor for automatic monitoring of method calls.
 * Records metrics for performance tracking and observability.
 */
@Monitored
@Interceptor
@Priority(Interceptor.Priority.LIBRARY_AFTER)
public class MonitoringInterceptor {

    private static final Logger log = LoggerFactory.getLogger(MonitoringInterceptor.class);

    @Inject
    MonitoringService monitoringService;

    @AroundInvoke
    public Object monitor(InvocationContext context) throws Exception {
        String methodName = context.getMethod().getName();
        String className = context.getMethod().getDeclaringClass().getSimpleName();
        String operationName = className + "." + methodName;
        
        Timer.Sample sample = monitoringService.startApiTimer();
        Instant start = Instant.now();
        
        log.debug("Starting monitored operation: {}", operationName);
        
        try {
            // Record request
            monitoringService.recordApiRequest();
            
            // Execute the method
            Object result = context.proceed();
            
            // Handle async results
            if (result instanceof CompletableFuture) {
                CompletableFuture<?> future = (CompletableFuture<?>) result;
                return future.whenComplete((res, throwable) -> {
                    Duration duration = Duration.between(start, Instant.now());
                    
                    if (throwable != null) {
                        monitoringService.recordApiFailure();
                        log.debug("Monitored operation {} failed after {}: {}", 
                                operationName, duration, throwable.getMessage());
                    } else {
                        monitoringService.recordApiSuccess();
                        monitoringService.recordApiResponseTime(duration);
                        log.debug("Monitored operation {} completed successfully in {}", 
                                operationName, duration);
                    }
                    
                    sample.stop(monitoringService.getApiResponseTimer());
                });
            } else {
                // Synchronous result
                Duration duration = Duration.between(start, Instant.now());
                monitoringService.recordApiSuccess();
                monitoringService.recordApiResponseTime(duration);
                sample.stop(monitoringService.getApiResponseTimer());
                
                log.debug("Monitored operation {} completed successfully in {}", 
                        operationName, duration);
                
                return result;
            }
            
        } catch (Exception e) {
            Duration duration = Duration.between(start, Instant.now());
            monitoringService.recordApiFailure();
            sample.stop(monitoringService.getApiResponseTimer());
            
            log.debug("Monitored operation {} failed after {}: {}", 
                    operationName, duration, e.getMessage());
            
            throw e;
        }
    }

}