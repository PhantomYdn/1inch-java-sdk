package io.oneinch.mcp.ratelimit;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Rate limiting interceptor that can be applied to methods requiring rate limiting.
 */
@RateLimit
@Interceptor
@Priority(2000)
public class RateLimitInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RateLimitInterceptor.class);

    @Inject
    RateLimitService rateLimitService;

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        // Use method name and class as default client ID
        String clientId = context.getMethod().getDeclaringClass().getSimpleName() + 
                         "." + context.getMethod().getName();
        
        // Check annotation for custom client ID
        RateLimit annotation = context.getMethod().getAnnotation(RateLimit.class);
        if (annotation != null && !annotation.clientId().isEmpty()) {
            clientId = annotation.clientId();
        }

        if (!rateLimitService.isRequestAllowed(clientId)) {
            long waitTime = rateLimitService.getSecondsUntilReset(clientId);
            String message = String.format(
                "Rate limit exceeded for %s. Please wait %d seconds before retrying.",
                clientId, waitTime
            );
            
            log.warn(message);
            throw new RateLimitExceededException(message, waitTime);
        }

        return context.proceed();
    }


}