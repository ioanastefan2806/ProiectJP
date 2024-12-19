package org.poo.main.structures;

/**
 * Functional interface for providing conversion rates between two currencies.
 */
@FunctionalInterface
public interface ConversionRateProvider {

    /**
     * Retrieves the conversion rate between two specified currencies.
     *
     * @param fromCurrency the source currency code (e.g., "USD")
     * @param toCurrency the target currency code (e.g., "EUR")
     * @return the conversion rate from the source currency to the target currency
     */
    double getRate(String fromCurrency, String toCurrency);
}
