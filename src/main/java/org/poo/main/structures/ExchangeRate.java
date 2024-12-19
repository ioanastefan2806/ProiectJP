package org.poo.main.structures;

/**
 * Represents an exchange rate between two currencies.
 */
public class ExchangeRate {
    private String fromCurrency;
    private String toCurrency;
    private double rate;

    /**
     * Constructs an ExchangeRate object with the specified source currency,
     * target currency, and rate.
     *
     * @param fromCurrency the source currency code (e.g., "USD")
     * @param toCurrency the target currency code (e.g., "EUR")
     * @param rate the exchange rate from the source currency to the target currency
     */
    public ExchangeRate(final String fromCurrency,
                        final String toCurrency,
                        final double rate) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.rate = rate;
    }

    /**
     * Retrieves the source currency code.
     *
     * @return the source currency code
     */
    public String getFromCurrency() {
        return fromCurrency;
    }

    /**
     * Sets the source currency code.
     *
     * @param fromCurrency the new source currency code
     */
    public void setFromCurrency(final String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    /**
     * Retrieves the target currency code.
     *
     * @return the target currency code
     */
    public String getToCurrency() {
        return toCurrency;
    }

    /**
     * Sets the target currency code.
     *
     * @param toCurrency the new target currency code
     */
    public void setToCurrency(final String toCurrency) {
        this.toCurrency = toCurrency;
    }

    /**
     * Retrieves the exchange rate.
     *
     * @return the exchange rate from the source currency to the target currency
     */
    public double getRate() {
        return rate;
    }

    /**
     * Sets the exchange rate.
     *
     * @param rate the new exchange rate
     */
    public void setRate(final double rate) {
        this.rate = rate;
    }

    /**
     * Returns a string representation of the ExchangeRate object.
     *
     * @return a string containing the source currency, target currency, and exchange rate
     */
    @Override
    public String toString() {
        return "ExchangeRate{"
                + "fromCurrency='" + fromCurrency + '\''
                + ", toCurrency='" + toCurrency + '\''
                + ", rate=" + rate + '}';
    }
}
