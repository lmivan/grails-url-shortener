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
        if (grailsApplication.config.shortener?.characters) {
            chars = grailsApplication.config.shortener.characters
        } else {
            chars = ('0'..'9') + ('a'..'h') + ('j'..'k') + ('m'..'z') + ('A'..'H') + ('J'..'K') +
                 ('M'..'Z')
        }

        if (grailsApplication.config.shortener?.minLength) {
            minLength = grailsApplication.config.shortener.minLength
        } else {
            minLength = 5
        }

        shortDomainUrl = grailsApplication.config.shortener?.shortDomain
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
            log.error "ERROR UrlShortener : Errors saving Short Url Instance"
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

        if (shortDomainUrl && shortUrl) {
            return "${shortDomainUrl}/${shortUrl}"
        } else {
            log.error "ERROR UrlShortener : Impossible to generate short url. See 'shortDomainUrl' parameter config or ShortUrl saving errors"
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
