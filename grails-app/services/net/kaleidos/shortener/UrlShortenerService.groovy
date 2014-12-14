package net.kaleidos.shortener

import javax.annotation.PostConstruct

class UrlShortenerService {
    static transactional = true

    def grailsApplication
    def sequenceGenerator

    List<String> chars
    Integer minLength
    String shortDomainUrl

    @PostConstruct
    public init() {
        if (grailsApplication.config.shortener.characters) {
            chars = grailsApplication.config.shortener.characters
        } else {
            chars = ['d', '2', 'C', 'S', 'Y', 'v', 'K', '5', 'p', '9', 't', 'k', 'R',
                          'X', 's', '1', 'N', 'c', 'w', 'F', 'q', 'G', 'T', 'J', 'H', '7',
                          'h', 'D', 'B', 'Z', 'j', 'n', '4', '6', 'b', 'z', '3', '8', 'V',
                          'M', 'g', 'm', 'W', 'f', 'y', 'r', '0', 'x', 'P', 'Q']
        }

        if (grailsApplication.config.shortener.minLength) {
            minLength = grailsApplication.config.shortener.minLength
        } else {
            minLength = 4
        }

        shortDomainUrl = grailsApplication.config.shortener.shortDomain
    }

    /**
     * Genereate a short url for a given url
     *
     * @param targetUrl The url to shorten
     * @return the shorted url
     */
    public String shortUrl(String targetUrl) {

        def shortenInstance = ShortenUrl.findByTargetUrl(targetUrl)
        if (shortenInstance) {
            return shortenInstance.shortUrl
        }

        Long nextNumber = sequenceGenerator.getNextNumber()

        def shortUrl = this.convert(nextNumber)
        shortenInstance = new ShortenUrl(targetUrl: targetUrl, shortUrl:shortUrl)
        shortenInstance.save()

        if (!shortenInstance.hasErrors()) {
            return shortUrl
        } else {
            return null
        }
    }

    /**
     * Generate a short url for a given url and return it with the full domain
     *
     * @param targetUrl The url to shorten
     * @return the shorted url with full domain
     */
    public String shortUrlFullDomain(String targetUrl) {
        def shortUrl = this.shortUrl(targetUrl)

        if (shortUrl) {
            return "${shortDomainUrl}/${shortUrl}"
        } else {
            return null
        }
    }

    /**
     * Get the target url from a short url and increment the number of hits
     *
     * @param shortUrl The short url to "expand"
     * @return the target url or null if it doesn't exist
     */
    public String getTargetUrl(String shortUrl) {
        def shortenInstance = ShortenUrl.findByShortUrl(shortUrl)
        if (shortenInstance) {
            shortenInstance.hits++
            shortenInstance.save()
        }

        return shortenInstance?.targetUrl
    }

    private String convert(Long number) {
        return convertToBase(number, chars.size(), 0, "").padLeft(minLength, chars[0])
    }

    private String convertToBase(Long number, Integer base, Integer position, String result) {
        if (number < Math.pow(base, position + 1)) {
            return chars[(number / (long)Math.pow(base, position)) as Integer] + result
        } else {
            Long remainder = (number % (long)Math.pow(base, position + 1))
            return convertToBase (number - remainder, base, position + 1, chars[(remainder / (long)(Math.pow(base, position))) as Integer] + result)
        }
    }
}