package net.kaleidos.shortener

import grails.test.spock.IntegrationSpec
import spock.lang.Unroll

class UrlShortenerServiceIntegrationSpec extends IntegrationSpec {

    def urlShortenerService
    def grailsApplication

    @Unroll
    void 'generate short urls for some numbers: #num'() {
        when:
            def result = urlShortenerService.convert(num)

        then:
            result == shortUrl

        where:
            num   | shortUrl
              0   | '00000'
              4   | '00004'
              10  | '0000a'
              20  | '0000m'
              33  | '0000z'
              34  | '0000A'
              57  | '0000Z'
              58  | '00010'
              68  | '0001a'
    }

    void 'if the number is too long the lenght of the url will be > 5'() {
        when:
            def result = urlShortenerService.convert(num)

        then:
            result.length() > 5

        where:
            num = 1000000000
    }

    void 'short a target url'() {
        when:
            def result = urlShortenerService.shortUrl(targetUrl)

        then:
            result == '00001'
            result.size() == 5

        where:
            targetUrl = "http://kaleidos.net"
    }

    void 'short an already shorted url'() {
        setup:
            def shortUrl1 = urlShortenerService.shortUrl(targetUrl)

        when:
            def shortUrl2 = urlShortenerService.shortUrl(targetUrl)

        then:
            shortUrl1 == shortUrl2

        where:
            targetUrl = "http://kaleidos.net"
    }

    void 'get the full short domain for a target url'() {
        when:
            def result = urlShortenerService.shortUrlFullDomain(targetUrl)

        then:
            result.contains(grailsApplication.config.shortener.shortDomain)

        where:
            targetUrl = "http://kaleidos.net"
    }

    void 'short an already shorted url with full domain url'() {
        setup:
            def shortUrl1 = urlShortenerService.shortUrlFullDomain(targetUrl)

        when:
            def shortUrl2 = urlShortenerService.shortUrlFullDomain(targetUrl)

        then:
            shortUrl1 == shortUrl2

        where:
            targetUrl = "http://kaleidos.net"
    }

    void 'get the targetUrl for a shortUrl'() {
        setup:
            def shortUrl = urlShortenerService.shortUrl(targetUrl)

        when:
            def targetUrl2 = urlShortenerService.getTargetUrl(shortUrl)

        then:
            targetUrl == targetUrl2

        where:
            targetUrl = "http://kaleidos.net"
    }

    void 'increment the number of hits when converting a short url to a target url'() {
        setup:
            def shortenInstance = new ShortenUrl(shortUrl: shortUrl, targetUrl: targetUrl)
            shortenInstance.save()

        when:
            urlShortenerService.getTargetUrl(shortUrl)

        then:
            shortenInstance.hits == old(shortenInstance.hits) + 1

        where:
            targetUrl = "http://kaleidos.net"
            shortUrl = "abcde"
    }

    void 'try to get the targetUrl for a shortUrl that does not exist'() {
        when:
            def targetUrl = urlShortenerService.getTargetUrl("SHORT_URL_DOES_NOT_EXIST")

        then:
            targetUrl == null
    }
}