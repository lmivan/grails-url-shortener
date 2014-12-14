package net.kaleidos.shortener

import grails.test.spock.IntegrationSpec
import spock.lang.Unroll

class ShortenUrlIntegrationSpec extends IntegrationSpec {

    @Unroll
    void 'save a short url for a target url: #targetUrl'() {
        setup:
            def shortenInstance = new ShortenUrl(shortUrl: shortUrl, targetUrl: targetUrl)

        when:
            shortenInstance.save()

        then:
            shortenInstance.hasErrors() == false
            shortenInstance.id != null
            shortenInstance.shortUrl == shortUrl
            shortenInstance.targetUrl == targetUrl
            shortenInstance.hits == 0

        where:
            shortUrl = "abcde"
            targetUrl << ["http://kaleidos.net", "http://localhost:8080", "http://localhost:123", "http://localhost:80", "http://localhost"]
    }
}