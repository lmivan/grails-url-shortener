package net.kaleidos.shortener

import grails.test.spock.IntegrationSpec

class ShortenerControllerIntegrationSpec extends IntegrationSpec {

    ShortenerController controller

    def setup() {
        controller = new ShortenerController()
    }

    void 'redirect to a target url from a short url'() {
        setup:
            def shortenInstance = new ShortenUrl(shortUrl: shortUrl, targetUrl: targetUrl)
            shortenInstance.save()

        and: 'mock collaborator'
            def urlShortenerService = Stub(UrlShortenerService)
            urlShortenerService.getTargetUrl(shortUrl) >> targetUrl
            controller.urlShortenerService = urlShortenerService

        and: 'params'
            controller.params.shortUrl = shortUrl

        when:
            controller.redirectToTargetUrl()

        then:
            controller.response.status == 302
            controller.response.redirectedUrl == targetUrl

        where:
            targetUrl = "http://kaleidos.net"
            shortUrl = "abcde"
    }

    void 'try to redirect when the short url does not exist'() {
        setup: 'mock collaborator'
            def urlShortenerService = Stub(UrlShortenerService)
            urlShortenerService.getTargetUrl(_) >> null
            controller.urlShortenerService = urlShortenerService

        and: 'params'
            controller.params.shortUrl = "SHORT_URL_DOES_NOT_EXIST"

        when:
            controller.redirectToTargetUrl()

        then:
            controller.response.status == 404
    }
}